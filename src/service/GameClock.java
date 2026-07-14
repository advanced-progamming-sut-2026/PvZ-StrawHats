package service;

public class GameClock {
    public static final double SECONDS_PER_TICK = 0.1;

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
}
