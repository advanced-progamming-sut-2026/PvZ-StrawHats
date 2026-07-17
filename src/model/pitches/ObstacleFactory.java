package model.pitches;

public final class ObstacleFactory {
    private ObstacleFactory() { }

    public static Obstacle create(ObstacleInformation kind) {
        return switch (kind) {
            case CRATER -> new Crater();
            case GRAVE -> throw new UnsupportedOperationException("Grave obstacles aren't implemented yet.");
        };
    }
}
