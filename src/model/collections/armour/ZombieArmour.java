package model.collections.armour;

public class ZombieArmour extends Armour {
    private final ArmourType armorType;
    private final int maxHP;

    private static final double LAYER_1_THRESHOLD = 0.666;
    private static final double LAYER_2_THRESHOLD = 0.333;

    public ZombieArmour(ArmourType armorType, int HP) {
        super(HP);
        this.armorType = armorType;
        this.maxHP = HP;
    }
    @Override
    public int absorbDamage(int damage) {
        if (isDestroyed()) {
            return damage;
        }

        int currentHP = getHP();
        if (damage >= currentHP) {
            int leftover = damage - currentHP;
            setHP(0);
            return leftover;
        } else {
            setHP(currentHP - damage);
            return 0;
        }
    }

    @Override
    public void changeState() {
        if (isDestroyed()) {
            setStage(ArmourStage.BROKEN);
        } else {
            int damageLayer = getDamageLayer();
            if (damageLayer == 0) {
                setStage(ArmourStage.INTACT);
            } else {
                setStage(ArmourStage.DAMAGED);
            }
        }
    }

    public boolean isDestroyed() {
        return getHP() <= 0;
    }

    public int getDamageLayer() {
        double ratio = (double) getHP() / maxHP;
        if (ratio > LAYER_1_THRESHOLD) return 0;
        if (ratio > LAYER_2_THRESHOLD) return 1;
        return 2;
    }

    public ArmourType getArmorType() {
        return armorType;
    }

    public int getMaxArmorHp() {
        return maxHP;
    }

    public boolean isMetal() {
        return armorType != null && armorType.isMetal();
    }
}