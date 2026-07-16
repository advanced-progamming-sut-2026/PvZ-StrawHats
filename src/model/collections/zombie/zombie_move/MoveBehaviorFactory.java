package model.collections.zombie.zombie_move;

import java.util.Map;

public interface MoveBehaviorFactory {
    MoveBehavior create(Map<String, Object> params, Map<String, Object> zombieData);
}