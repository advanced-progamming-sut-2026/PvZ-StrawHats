package service;

public class GameClock {
    public static final double SECONDS_PER_TICK = 0.1;
    public static final double TIME_EPSILON = 1.0e-9;

    private int tick = 0;

    public void tick() {
        tick++;
    }

    public int getTicks() {
        return tick;
    }

    public double getElapsedSeconds() {
        return tick * SECONDS_PER_TICK;
    }

    public void reset() {
        tick = 0;
    }

    public static boolean hasReached(double elapsedSeconds, double targetSeconds) {
        return elapsedSeconds + TIME_EPSILON >= targetSeconds;
    }

    public static boolean isZero(double seconds) {
        return seconds <= TIME_EPSILON;
    }

    public static double countDown(double seconds, double deltaSeconds) {
        double remaining = Math.max(0, seconds - deltaSeconds);
        return isZero(remaining) ? 0 : remaining;
    }
}
