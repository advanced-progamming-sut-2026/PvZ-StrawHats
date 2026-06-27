package model.greenhouse;

public abstract class PotPlant {
    private Pot pot;

    private float remainingTime;

    private final int award;
    private final int sellPrice;

    public PotPlant(Pot pot, int remainingTime, int award, int sellPrice) {
        this.pot = pot;
        this.remainingTime = remainingTime;
        this.award = award;
        this.sellPrice = sellPrice;
    }

    public void reduceRemainingTime(float delta) {
        remainingTime -= delta;
    }

    public boolean isCollectAble() {
        return remainingTime <= 0;
    }

    public Pot getPot() {
        return pot;
    }
    public float getRemainingTime() {
        return remainingTime;
    }
    public int getAward() {
        return award;
    }
    public int getSellPrice() {
        return sellPrice;
    }

    public void setPot(Pot pot) {
        this.pot = pot;
    }
    public void setRemainingTime(float remainingTime) {
        this.remainingTime = remainingTime;
    }
}
