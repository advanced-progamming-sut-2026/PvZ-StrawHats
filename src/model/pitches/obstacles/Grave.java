package model.pitches.obstacles;

/**
 * A gravestone (Ancient Egypt / Dark Ages). Blocks planting on its tile
 * until removed, and — in Dark Ages specifically — has a chance each wave
 * to raise a zombie from underneath (see DarkAge.necromancy).
 */
public class Grave implements Obstacle {
    @Override
    public boolean blocksPlanting() {
        return true;
    }

    @Override
    public String getName() {
        return "Grave";
    }
}
