package model.collections;

import com.ussr.pvz.model.entities.zombies.targeting.PlantSideTargetFinder;
import com.ussr.pvz.model.entities.zombies.targeting.TargetFinder;
import com.ussr.pvz.model.entities.zombies.targeting.ZombieSideTargetFinder;
import model.collections.zombie.Zombie;
import util.GameSession;

public enum Faction {
    ZOMBIES(new PlantSideTargetFinder()),
    PLANTS(new ZombieSideTargetFinder());

    private final TargetFinder targetFinder;

    Faction(TargetFinder targetFinder) {
        this.targetFinder = targetFinder;
    }

    public Item findTarget(Zombie self, GameSession session) {
        return targetFinder.findTarget(self, session);
    }
}
