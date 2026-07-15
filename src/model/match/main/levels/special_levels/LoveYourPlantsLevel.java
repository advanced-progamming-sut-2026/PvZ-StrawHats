package model.match.main.levels.special_levels;

import model.match.main.levels.Level;

public class LoveYourPlantsLevel extends Level {
    private int maxPlantLoss = 3; // if more than this many plants die, you lose

    public int getMaxPlantLoss() { return maxPlantLoss; }
    public void setMaxPlantLoss(int maxPlantLoss) { this.maxPlantLoss = maxPlantLoss; }
}