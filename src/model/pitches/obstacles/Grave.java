package model.pitches.obstacles;


public class Grave implements Obstacle {
    @Override
    public boolean blocksPlanting() {
        return true;
    }

    @Override
    public String getName() {
        return "Grave";
    }
}
