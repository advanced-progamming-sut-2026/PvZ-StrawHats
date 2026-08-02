package model.pitches;

import model.pitches.obstacles.SlipperyDirection;



public record Tile(TileType type, SlipperyDirection slipperyDirection) {
    public Tile(TileType type) {
        this(type, null);
    }

}
