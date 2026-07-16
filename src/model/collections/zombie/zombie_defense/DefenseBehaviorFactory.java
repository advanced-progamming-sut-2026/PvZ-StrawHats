package model.collections.zombie.zombie_defense;

import java.util.Map;

public interface DefenseBehaviorFactory {
    DefenseBehavior create(Map<String, Object> params);
}