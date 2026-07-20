package model.pitches.obstacles;

public final class ObstacleFactory {
    private ObstacleFactory() { }

    public static Obstacle create(ObstacleInformation kind) {
        return switch (kind) {
            case CRATER -> new Crater();
            case GRAVE -> throw new UnsupportedOperationException("Grave obstacles aren't implemented yet.");
            case OCTOPUS_WRAP -> throw new UnsupportedOperationException(
                    "OctopusWrap needs a Plant and hp - construct it directly: new OctopusWrap(plant, hp).");
            case ICE_BLOCK -> throw new UnsupportedOperationException(
                    "IceBlock needs a Plant and hp - construct it directly: new IceBlock(plant, hp).");
        };
    }
}
