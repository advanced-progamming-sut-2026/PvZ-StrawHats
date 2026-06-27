package controller.menus.greenhouse;


import controller.menus.Menu;
import model.greenhouse.Marigold;
import model.greenhouse.Pot;
import model.greenhouse.PotPlant;

import java.util.Random;

public class GreenhouseController extends Menu {

    public void plantPotPlant(Pot pot) {
        if (pot.isLocked() || pot.getPotPlant() != null)
        {
            System.out.println("Cannot plant the pot plant in this pot!");
        }
        else
        {
            PotPlant potPlant;

            Random random = new Random();
            int chance = random.nextInt(100);
            if (chance < 50)
            {
                potPlant = new Marigold(pot);

            }
            else
            {
                // ba ehtemal 50 darsad yeki az plant hayi ke user unlock kardeh (felan hamoon Marigold mizaram)
                potPlant = new Marigold(pot);
            }
            pot.setPotPlant(potPlant);
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void handleCommand(String text) {

    }

    @Override
    public void exitMenu() {

    }

    @Override
    public String showMenu() {
        return "";
    }

}
