package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import service.GameClock;
import util.GameSession;

public class ProspectorMove implements MoveBehavior {
    private enum ProspectorPhase {
        WALKING_LEFT,
        AIRBORNE_LAUNCH,
        REVERSE_WALK
    }

    private ProspectorPhase currentPhase = ProspectorPhase.WALKING_LEFT;

    private double launchCountdown = 10.0;
    private double timeToTravel = 1.5;
    private double airborneSpeedX = 0.0;

    private boolean dynamiteExtinguished = false;

    public void extinguishDynamite() {
        if (currentPhase == ProspectorPhase.WALKING_LEFT) {
            dynamiteExtinguished = true;
        }
    }

    public void litDynamite() {
        if (currentPhase == ProspectorPhase.WALKING_LEFT) {
            if (dynamiteExtinguished) {
                dynamiteExtinguished = false;
            }
        }
    }

    @Override
    public void move(Zombie zombie, GameSession session) {
        if (dynamiteExtinguished) {
            new Walk().move(zombie, session);
            return;
        }

        Position pos = zombie.getPosition();
        if (pos == null) return;

        switch (currentPhase) {
            case WALKING_LEFT -> {
                Position vel = zombie.getSpeed();
                Position newPos = pos.add(vel.scale(GameClock.SECONDS_PER_TICK));
                zombie.setPosition(newPos);

                launchCountdown -= GameClock.SECONDS_PER_TICK;
                if (launchCountdown <= 0) {
                    currentPhase = ProspectorPhase.AIRBORNE_LAUNCH;

                    if (pos.x() > 0) {
                        airborneSpeedX = pos.x() / timeToTravel;
                    }
                }

                if (newPos.x() < 0) {
                    session.onZombieReachedEnd();
                }
            }

            case AIRBORNE_LAUNCH -> {
                double newX = pos.x() - (airborneSpeedX * GameClock.SECONDS_PER_TICK);
                zombie.setPosition(Position.of(Math.max(0, newX), pos.y()));

                timeToTravel -= GameClock.SECONDS_PER_TICK;
                if (timeToTravel <= 0 || zombie.getPosition().x() <= 0) {
                    zombie.setPosition(Position.of(0, pos.y()));
                    currentPhase = ProspectorPhase.REVERSE_WALK;

                    double standardSpeedMagnitude = Math.abs(zombie.getSpeed().x());
                    zombie.setSpeed(Position.of(standardSpeedMagnitude, 0));
                }
            }

            case REVERSE_WALK -> {
                Position vel = zombie.getSpeed();
                Position newPos = pos.add(vel.scale(GameClock.SECONDS_PER_TICK));
                zombie.setPosition(newPos);
            }
        }
    }
}
