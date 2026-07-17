package model.match.mini_games.vasebreaker.vase;

import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.mini_games.vasebreaker.Vasebreaker;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

/**
 * "Gargantuar vase" — guaranteed to release a Gargantuar-class zombie when broken.
 */
public class ZombieVase extends Vase {
    private static final String GARGANTUAR_ALIAS = "ZombieGargantuar";

    public ZombieVase(Position position) {
        super(position);
    }

    @Override
    protected void onBreak(GameSession session, Vasebreaker minigame) {
        Zombie zombie = ZombieFactory.create(GARGANTUAR_ALIAS, (int) position.y(), (int) position.x());
        zombie.setPosition(position);
        session.spawnZombie(zombie);
    }
}
