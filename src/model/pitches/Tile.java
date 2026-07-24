package model.pitches;

import model.pitches.obstacles.SlipperyDirection;


public class Tile {
    private final TileType type;
    private final SlipperyDirection slipperyDirection; // only meaningful when type == Slippery

    public Tile(TileType type) {
        this(type, null);
    }

    public Tile(TileType type, SlipperyDirection slipperyDirection) {
        this.type = type;
        this.slipperyDirection = slipperyDirection;
    }

    public TileType getType() {
        return type;
    }

    
    public SlipperyDirection getSlipperyDirection() {
        return slipperyDirection;
    }
}
