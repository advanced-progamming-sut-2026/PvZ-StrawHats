package model.collections.armour;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;

public class PlantArmour extends Armour {
    private final int reflectiveDamage;
    private final boolean explodeOnBreak;

    public PlantArmour(int HP, int reflectiveDamage, boolean explodeOnBreak) {
        super(HP);
        this.reflectiveDamage = reflectiveDamage;
        this.explodeOnBreak = explodeOnBreak;
    }

    @Override
    public int absorbDamage(int damage) {
        if (damage >= getHP()) {
            int overflow = damage - getHP();
            setHP(0);
            return overflow;
        } else {
            setHP(getHP() - damage);
            return 0;
        }
    }

    public void handleReflection(Zombie dealer, Plant user) {
        if (dealer != null && this.reflectiveDamage > 0 && getHP() > 0) {
            dealer.takeDamage(this.reflectiveDamage, user);
        }
    }

    public boolean isDestroyed() { return getHP() <= 0; }
    public boolean isExplodeOnBreak() { return explodeOnBreak; }
}