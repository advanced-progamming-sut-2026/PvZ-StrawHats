package model.match_mechanisms;

import model.collections.Item;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;

public interface Attack {
    public void dealDamage(Item target);
}
