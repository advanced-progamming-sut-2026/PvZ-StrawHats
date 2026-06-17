package model.pitches;

public class Environment {
    private Square[][] plantingGround = new Square[5][10];

    private static Environment instance;

    private Environment() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                this.plantingGround[i][j] = new Square();
            }
        }
    }

    public static void getInstance() {
        if (instance == null)
            instance = new Environment();
    }
}
