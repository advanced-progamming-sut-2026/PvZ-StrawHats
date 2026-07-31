package model.collections.plant.actstrategy;

import model.collections.plant.AbilityType;
import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.obstacles.Grave;
import model.pitches.obstacles.IceBlock;
import model.utils.GameSession;

import java.util.ArrayList;

public class ExplodeStrategy implements ActStrategy {
    private static final double TRAP_ACTIVATION_RADIUS = 0.55;

    @Override
    public void act(Plant user, GameSession session) {
        if (user.getIntervalTimer() > 0) return;

        if (handleObstaclePlant(user, session)) {
            user.setAlive(false);
            return;
        }

        boolean instant = user.getAbilityType() == AbilityType.INSTANT_EXPLOSIVE;
        if (!instant && !isZombieTouch(user, session)) return;

        ArrayList<Zombie> targets = switch ((int) user.getAbilityValue()) {
            case 1 -> touchDetect(user, session);
            case 2 -> areaDetect(user, session);
            case 3 -> lineDetect(user, session);
            case 4 -> wholePitchDetect(session);
            default -> new ArrayList<>();
        };

        if (!targets.isEmpty()) userAct(user, targets);
        damageStructures(user, session);
        if ((int) user.getAbilityValue() == 4) makeHole(user, session);
        user.setAlive(false);
    }

    private void damageStructures(Plant user, GameSession session) {
        Position center = user.getPosition();
        if (center == null) return;
        int mode = (int) user.getAbilityValue();
        for (model.collections.zombie.zombie_pushing_item.PushableStructure structure : session.getPushableStructures()) {
            Position position = structure.getPosition();
            if (position == null) continue;
            boolean inRange = switch (mode) {
                case 1 -> position.distanceTo(center) <= TRAP_ACTIVATION_RADIUS;
                case 2 -> Math.abs(position.x() - center.x()) <= 1 && Math.abs(position.y() - center.y()) <= 1;
                case 3 -> Math.abs(position.y() - center.y()) < 0.5;
                case 4 -> true;
                default -> false;
            };
            if (inRange) structure.takeDamage(Math.max(0, user.getDamage()), user, session);
        }
    }

    private boolean handleObstaclePlant(Plant user, GameSession session) {
        Position position = user.getPosition();
        if (position == null || session.getEnvironment() == null) return false;
        Cell cell = session.getEnvironment().getCell((int) Math.round(position.y()), (int) Math.round(position.x()));
        if (cell == null || cell.getObstacle() == null) return false;

        if (user.getName().equalsIgnoreCase("Hot Potato") && cell.getObstacle() instanceof IceBlock iceBlock) {
            if (iceBlock.takeDamage(Integer.MAX_VALUE)) cell.setObstacle(null);
            return true;
        }
        if (user.getName().equalsIgnoreCase("Grave Buster") && cell.getObstacle() instanceof Grave) {
            cell.setObstacle(null);
            return true;
        }
        return false;
    }

    private boolean isZombieTouch(Plant user, GameSession session) {
        Position userPos = user.getPosition();
        for (Zombie zombie : session.getZombies()) {
            if (isHostileTarget(zombie) && zombie.getPosition().distanceTo(userPos) <= TRAP_ACTIVATION_RADIUS) return true;
        }
        return false;
    }

    private ArrayList<Zombie> touchDetect(Plant user, GameSession session) {
        ArrayList<Zombie> targets = new ArrayList<>();
        Position userPos = user.getPosition();
        Zombie firstTouch = null;
        double shortest = Double.MAX_VALUE;

        for (Zombie zombie : session.getZombies()) {
            if (!isHostileTarget(zombie)) continue;
            double distance = zombie.getPosition().distanceTo(userPos);
            if (distance <= TRAP_ACTIVATION_RADIUS && distance < shortest) {
                shortest = distance;
                firstTouch = zombie;
            }
        }
        if (firstTouch != null) targets.add(firstTouch);
        return targets;
    }

    private ArrayList<Zombie> areaDetect(Plant user, GameSession session) {
        ArrayList<Zombie> targets = new ArrayList<>();
        Position userPos = user.getPosition();
        for (Zombie zombie : session.getZombies()) {
            if (!isHostileTarget(zombie)) continue;
            Position zomPos = zombie.getPosition();
            if (Math.abs(zomPos.y() - userPos.y()) <= 1 && Math.abs(zomPos.x() - userPos.x()) <= 1) {
                targets.add(zombie);
            }
        }
        return targets;
    }

    private ArrayList<Zombie> lineDetect(Plant user, GameSession session) {
        ArrayList<Zombie> targets = new ArrayList<>();
        Position userPos = user.getPosition();
        for (Zombie zombie : session.getZombies()) {
            if (isHostileTarget(zombie) && Math.abs(userPos.y() - zombie.getPosition().y()) < 0.5) {
                targets.add(zombie);
            }
        }
        return targets;
    }

    private ArrayList<Zombie> wholePitchDetect(GameSession session) {
        ArrayList<Zombie> targets = new ArrayList<>();
        for (Zombie zombie : session.getZombies()) if (isHostileTarget(zombie)) targets.add(zombie);
        return targets;
    }

    private void makeHole(Plant user, GameSession session) {
        Position position = user.getPosition();
        if (position == null || session.getEnvironment() == null) return;
        Cell cell = session.getEnvironment().getCell((int) Math.round(position.y()), (int) Math.round(position.x()));
        if (cell != null && user.getName().equalsIgnoreCase("Doom-shroom")) {
            cell.setObstacle(new model.pitches.obstacles.Crater());
        }
    }

    private void userAct(Plant user, ArrayList<Zombie> targets) {
        int damage = user.getTags().contains(PlantTag.FIRE) ? user.getDamage() * 2 : user.getDamage();
        for (Zombie zombie : targets) {
            if (user.getTags().contains(PlantTag.ICE)) zombie.applyStatus(Zombie.Status.FREEZE, 5.0);
            else if (user.getTags().contains(PlantTag.FIRE)) zombie.applyStatus(Zombie.Status.FIRED, 3.0);
            if (user.getTags().contains(PlantTag.POISON)) zombie.takeDamage(damage, true);
            else zombie.takeDamage(damage, user);
        }
    }

    private boolean isHostileTarget(Zombie zombie) {
        return zombie != null && zombie.isAlive() && !zombie.isHypnotized() && zombie.getPosition() != null;
    }
}
