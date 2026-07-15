package model.greenhouse;

public abstract class PotPlant {
    private Pot pot;

    private long plantedAtMillis;
    private final long growDurationMillis;

    private final Integer plantId;
    private final String plantName;
    private final int awardCoins;

    public PotPlant(Pot pot, long growDurationSeconds, Integer plantId, String plantName, int awardCoins) {
        this.pot = pot;
        this.plantedAtMillis = System.currentTimeMillis();
        this.growDurationMillis = growDurationSeconds * 1000L;
        this.plantId = plantId;
        this.plantName = plantName;
        this.awardCoins = awardCoins;
    }

    public boolean isCollectAble() {
        return getRemainingSeconds() <= 0;
    }

    public long getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - plantedAtMillis;
        long remainingMillis = growDurationMillis - elapsed;
        return Math.max(0, (remainingMillis + 999) / 1000L);
    }

    public void growInstantly() {
        plantedAtMillis = System.currentTimeMillis() - growDurationMillis;
    }

    public abstract boolean isMarigold();

    public Pot getPot() {
        return pot;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public int getAwardCoins() {
        return awardCoins;
    }

    public void setPot(Pot pot) {
        this.pot = pot;
    }
}
