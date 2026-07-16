package model.pitches;

/**
 * Terrain type for a single lawn cell. Only Slippery currently has movement
 * behavior wired up (see MoveBehavior.applySlipperyShift); cells default to
 * no Tile at all (plain ground) unless one is explicitly set on them.
 */
public enum TileType {
    Normal,
    Slippery
}
