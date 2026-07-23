package model.match.main.season.travellog.cave;

import model.collections.zombie.Zombie;
import model.match.main.season.Season;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.Tile;
import model.pitches.TileType;
import model.pitches.obstacles.SlipperyDirection;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.Random;

public class Cave extends Season {
    private static final int ICE_TILE_COUNT = 4;
    private static final Random RANDOM = new Random();

    public ArrayList<Zombie> frozenZombies = new ArrayList<>();

    public Cave() {
        super("Frostbite Caves");
    }

    @Override
    public boolean hasIceTiles() { return true; }

    @Override
    public void placeSeasonObstacles(GameSession session) {
        if (session == null || session.getEnvironment() == null) return;
        Environment env = session.getEnvironment();

        int placed = 0;
        int attempts = 0;
        while (placed < ICE_TILE_COUNT && attempts < 100) {
            attempts++;
            int row = RANDOM.nextInt(env.getRows());
            int col = RANDOM.nextInt(env.getCols());
            Cell cell = env.getCell(row, col);
            if (cell != null && cell.getTile() == null) {
                SlipperyDirection direction = RANDOM.nextBoolean() ? SlipperyDirection.UP : SlipperyDirection.DOWN;
                cell.setTile(new Tile(TileType.Slippery, direction));
                placed++;
            }
        }
    }

    public static void meltIce(Zombie zombie) {
        if (zombie == null) return;
        if (zombie.getStatus() == Zombie.Status.FREEZE) {
            zombie.setStatus(Zombie.Status.NORMAL);
        }
    }
}