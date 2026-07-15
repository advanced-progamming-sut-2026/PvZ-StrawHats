package model.collections.zombie.zombie_move;

import java.util.Map;

public class MoveBehaviorRegistry {
    public static MoveBehavior create(Object spec, Map<String, Object> data) {
        return (zombie, deltaTimeSeconds, session) -> {
        };
    }
}