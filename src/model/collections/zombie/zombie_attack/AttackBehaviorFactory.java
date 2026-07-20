package model.collections.zombie.zombie_attack;

import java.util.Map;

@FunctionalInterface
public interface AttackBehaviorFactory {
    AttackBehavior create(Map<String, Object> params, Map<String, Object> zombieData);
}

