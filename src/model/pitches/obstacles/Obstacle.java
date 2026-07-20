package model.pitches.obstacles;

public interface Obstacle {
    /**
     * Whether a plant can ever be placed on a cell holding this obstacle
     * (directly, or by swapping another plant into it).
     */
    boolean blocksPlanting();

    String getName();
}
