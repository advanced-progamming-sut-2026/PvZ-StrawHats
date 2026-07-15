package model.greenhouse;

public class GreenhousePlant extends PotPlant {
    private static final long GROW_DURATION_SECONDS = 8 * 3600L;

    public GreenhousePlant(Pot pot, int plantId, String plantName) {
        super(pot, GROW_DURATION_SECONDS, plantId, plantName, 0);
    }

    @Override
    public boolean isMarigold() {
        return false;
    }
}
