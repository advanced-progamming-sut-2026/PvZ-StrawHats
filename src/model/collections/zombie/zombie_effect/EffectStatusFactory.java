package model.collections.zombie.zombie_effect;

import java.util.Map;

public interface EffectStatusFactory {
    ZombieEffectStatus create(Map<String, Object> params, Map<String, Object> zombieData);
}