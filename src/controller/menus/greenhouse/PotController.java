package controller.menus.greenhouse;

import model.greenhouse.Pot;
import model.greenhouse.PotPlant;
import model.user_data.User;

public class PotController{
    private final Pot pot;

    public PotController(Pot pot) {
        this.pot = pot;
    }

    public void collect() {
        if (!pot.getPotPlant().isCollectAble())
            return;

        //add pot plant award to user coins
        removePlant();
    }

    public void sell() {
        //add pot plant sell price to user coins
        removePlant();
    }

    public int calculateGrowCost() {
        float remainingTime = pot.getPotPlant().getRemainingTime();

        if (remainingTime <= 0)
            return 0;

        float remainingHours = remainingTime / 36000f;
        return (int) Math.ceil(remainingHours);
    }

    public void grow(User user) {
        if (pot.getPotPlant().getRemainingTime() <= 0) {
            System.out.println("Plant is already fully grown.");
        }

        int cost = calculateGrowCost();

//        if (user.getDiamonds() < cost) {
//            System.out.println("Not enough diamonds.");
//        }
//        user.setDiamonds(user.getDiamonds() - cost);

        pot.getPotPlant().setRemainingTime(0);
    }

    public void removePlant() {
        pot.setPotPlant(null);
    }
}
