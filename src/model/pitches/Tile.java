package model.pitches;

import model.pitches.obstacles.SlipperyDirection;


/**
 * @param slipperyDirection only meaningful when type == Slippery
 */
public record Tile(TileType type, SlipperyDirection slipperyDirection) {
    public Tile(TileType type) {
        this(type, null);
    }

}
