package model.collections.plant;

package model.collections.plant;

public class ModifiableStat {
    private float baseValue;
    private float currentValue;

    public ModifiableStat(float baseValue) {
        this.baseValue = baseValue;
        this.currentValue = baseValue;
    }
    public void update(float deltaTime) {}
    public float getValue() { return currentValue; }
    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        this.currentValue = baseValue;
    }
}