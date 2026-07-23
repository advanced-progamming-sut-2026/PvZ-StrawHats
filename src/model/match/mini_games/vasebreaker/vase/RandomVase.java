package model.match.mini_games.vasebreaker.vase;

import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match.mini_games.vasebreaker.Vasebreaker;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

import java.util.Random;

/**
 * A regular "?" vase: empty, or hides a normal zombie, or hides a random seed packet.
 * The outcome isn't shown to the player until it's broken.
 */
public class RandomVase extends Vase {
    private static final Random RAND = new Random();
    public enum Content { EMPTY, ZOMBIE, PLANT }

    private final Content content;
    private final int plantId; // only meaningful when content == PLANT
    private final String[] zombieAliases;

    public RandomVase(Position position, int[] unlockedPlantIds, String[] zombieAliases) {
        super(position);
        this.zombieAliases = zombieAliases == null || zombieAliases.length == 0
                ? new String[] {"ZombieDefault"} : zombieAliases.clone();
        double roll = RAND.nextDouble();
        if (roll < 0.5) {
            content = Content.EMPTY;
            plantId = -1;
        } else if (roll < 0.8 || unlockedPlantIds == null || unlockedPlantIds.length == 0) {
            content = Content.ZOMBIE;
            plantId = -1;
        } else {
            content = Content.PLANT;
            plantId = unlockedPlantIds[RAND.nextInt(unlockedPlantIds.length)];
        }
    }

    public Content getContent() { return content; }

    @Override
    protected void onBreak(GameSession session, Vasebreaker minigame) {
        switch (content) {
            case ZOMBIE -> {
                String alias = zombieAliases[RAND.nextInt(zombieAliases.length)];
                Zombie zombie = ZombieFactory.create(alias, (int) position.y(), (int) position.x());
                zombie.setPosition(position);
                session.spawnZombie(zombie);
            }
            case PLANT -> minigame.dropSeedPacket(position, plantId);
            case EMPTY -> { /* nothing happens */ }
        }
    }
}
