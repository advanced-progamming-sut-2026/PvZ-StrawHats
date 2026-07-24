package model.match.main.season.travellog.egypt;

import java.util.Random;


public class SandStorm {
    private static final Random RAND = new Random();
    private static final int MIN_COLUMNS = 1;
    private static final int MAX_COLUMNS = 4;

    /**
     * Returns how many extra columns a whirlwind-carried zombie should be
     * pushed in on entry (1 to 4). Callers decide whether this wave/zombie
     * qualifies (only the final wave, per the spec).
     */
    public static int sandstorm() {
        return MIN_COLUMNS + RAND.nextInt(MAX_COLUMNS - MIN_COLUMNS + 1);
    }
}
