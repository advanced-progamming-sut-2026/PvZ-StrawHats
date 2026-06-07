package model.match_mechanisms;

public class Time {
    private static int tick;

    public static void update(int amount) {

    }

    public static int getTick() {
        return tick;
    }

    public static void setTick(int tick) {
        Time.tick = tick;
    }
}
