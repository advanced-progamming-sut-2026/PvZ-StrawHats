package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import util.GameSession;

import java.util.Random;

public class WallNutStrategy implements ActStrategy {

    private static final Random RANDOM = new Random();
    private static final double DIVERT_RADIUS = 5.0;
    private static final int TOP_ROW = 0;
    private static final int BOTTOM_ROW = 4;

    @Override
    public void act(Plant user, GameSession session) {
        if (!user.getTags().contains(PlantTag.MOVE_ZOMBIES)) return;
        if (user.getIntervalTimer() > 0) return;

        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive()) continue;
            if (zombie.getPosition().distanceTo(user.getPosition()) < DIVERT_RADIUS) {
                divertZombie(zombie);
                break;
            }
        }

        user.setInternalTimer(user.getActionInterval());
    }


    private void divertZombie(Zombie zombie) {
        Position zomPos = zombie.getPosition();
        int currentRow = (int) zomPos.y();

        int dy;
        if (currentRow <= TOP_ROW) {
            dy = 1;
        } else if (currentRow >= BOTTOM_ROW) {
            dy = -1;
        } else {
            dy = RANDOM.nextBoolean() ? 1 : -1;
        }
        zombie.setPosition(new Position(zomPos.x(), zomPos.y() + dy));
    }
}
