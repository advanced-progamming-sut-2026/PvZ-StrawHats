package model.collections.armour;

public class ArmourFactory {
    public static Armour createArmour(ArmourType type, int hp, int reflectiveDamage, boolean explodeOnBreak) {
        if (type == ArmourType.PLANT_SHIELD) {
            return new PlantArmour(hp, reflectiveDamage, explodeOnBreak);
        } else if (type == ArmourType.IRON_BUCKET || type == ArmourType.CONEHEAD) {
            return new ZombieArmour(type, hp);
        }
        return null;
    }
}