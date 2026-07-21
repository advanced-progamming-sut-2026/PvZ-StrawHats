package model.match.main.season.travellog.darkage;

import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.main.season.Season;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.obstacles.Grave;
import model.utils.GameSession;

import java.util.Random;

public class DarkAge extends Season {
    private static final int GRAVE_COUNT = 3;
    private static final double NECROMANCY_CHANCE = 0.25; // per grave, per wave
    private static final Random RANDOM = new Random();

    public DarkAge() {
        super("Dark Ages");
    }

    @Override
    public boolean isNight() { return true; }

    @Override
    public void placeSeasonObstacles(GameSession session) {
        if (session == null || session.getEnvironment() == null) return;
        Environment env = session.getEnvironment();

        int placed = 0;
        int attempts = 0;
        while (placed < GRAVE_COUNT && attempts < 100) {
            attempts++;
            int row = RANDOM.nextInt(env.getRows());
            int col = RANDOM.nextInt(env.getCols());
            Cell cell = env.getCell(row, col);
            if (cell != null && cell.getObstacle() == null && !cell.hasPlant()) {
                cell.setObstacle(new Grave());
                placed++;
            }
        }
    }

    @Override
    public void onWaveStart(GameSession session, int waveIndex) {
        necromancy(session);
    }

    public static void necromancy(GameSession session) {
        if (session == null || session.getEnvironment() == null) return;
        Environment env = session.getEnvironment();

        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                Cell cell = env.getCell(row, col);
                if (cell == null || !(cell.getObstacle() instanceof Grave) || cell.hasPlant()) continue;

                if (RANDOM.nextDouble() < NECROMANCY_CHANCE) {
                    Zombie risen = ZombieFactory.create("ZombieDefault", row, col);
                    risen.setPosition(new Position(col, row));
                    session.spawnZombie(risen);
                }
            }
        }
    }
}
