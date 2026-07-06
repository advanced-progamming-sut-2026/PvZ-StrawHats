package model.collections.armour;

public class ZombieArmour extends Armour {
    private final int maxHP;

    public ZombieArmour(int HP) {
        super(HP);
        this.maxHP = HP;
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

    @Override
    public void changeState() {
        if (getHP() <= 0) {
            setStage(ArmourStage.BROKEN);
        } else if (getHP() < maxHP / 2) {
            setStage(ArmourStage.DAMAGED);
        } else {
            setStage(ArmourStage.INTACT);
        }
    }
}