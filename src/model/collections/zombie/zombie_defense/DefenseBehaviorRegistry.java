package model.collections.zombie.zombie_defense;

public class DefenseBehaviorRegistry {
    public static DefenseBehavior create(Object spec) {
        return (zombie, damage, damageSource, session) -> damage;
    }
}