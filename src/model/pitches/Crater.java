package model.pitches;

/**
 * Left behind wherever a zombie eats a plant. A crater blocks planting
 * forever after — normally or by swapping another plant into it.
 */
public class Crater implements Obstacle {
    @Override
    public boolean blocksPlanting() {
        return true;
    }

    @Override
    public String getName() {
        return "Crater";
    }
}
