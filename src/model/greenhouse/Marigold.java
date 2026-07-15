package model.greenhouse;

public class Marigold extends PotPlant {
    private static final long GROW_DURATION_SECONDS = 2 * 3600L;
    private static final int COIN_AWARD = 500;

    public Marigold(Pot pot) {
        super(pot, GROW_DURATION_SECONDS, null, "Marigold", COIN_AWARD);
    }

    @Override
    public boolean isMarigold() {
        return true;
    }
}
