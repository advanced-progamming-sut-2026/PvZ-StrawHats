package model.match.mini_games;

import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.ZombieWave;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.List;

public final class MiniGameWaves {
    private MiniGameWaves() {
    }

    public static List<ZombieWave> create(GameSession session, double[] delays, String[][] aliasesByWave) {
        if (delays.length != aliasesByWave.length) {
            throw new IllegalArgumentException("Each mini-game wave needs one delay.");
        }

        List<ZombieWave> waves = new ArrayList<>();
        for (int i = 0; i < aliasesByWave.length; i++) {
            List<Zombie> zombies = new ArrayList<>();
            for (String alias : aliasesByWave[i]) {
                Zombie zombie = ZombieFactory.create(alias, 0, session.getCols() - 1);
                zombie.setPosition(new Position(session.getCols() - 1, 0));
                zombies.add(zombie);
            }
            waves.add(new ZombieWave(delays[i], zombies, i == aliasesByWave.length - 1));
        }
        return waves;
    }
}
