package model.collections.zombie.zombie_attack;

import java.util.Map;

public class AttackBehaviorRegistry {
    public static AttackBehavior create(Object spec, Map<String, Object> data) {
        return (zombie, session) -> {
            var target = zombie.acquireTarget(session);
            if (target != null) {
                zombie.dealDamage(target);
            }
        };
    }
}
