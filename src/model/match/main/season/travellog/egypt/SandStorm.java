package model.match.main.season.travellog.egypt;

import java.util.Random;


public class SandStorm {
    private static final Random RAND = new Random();
    private static final int MIN_COLUMNS = 1;
    private static final int MAX_COLUMNS = 4;


    public static int sandstorm() {
        return MIN_COLUMNS + RAND.nextInt(MAX_COLUMNS - MIN_COLUMNS + 1);
    }
}
