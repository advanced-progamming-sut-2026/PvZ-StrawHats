package model.match.mini_games.vasebreaker;

import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.match.mini_games.MiniGameMode;
import model.match.mini_games.vasebreaker.vase.PlantVase;
import model.match.mini_games.vasebreaker.vase.RandomVase;
import model.match.mini_games.vasebreaker.vase.Vase;
import model.match.mini_games.vasebreaker.vase.ZombieVase;
import model.match_mechanisms.vector.Position;
import model.pitches.Environment;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Break every vase on the lawn without letting a zombie that was hiding
 * inside one of them reach the house. No sun falls from the sky here, and
 * the player never chooses plants directly — vases are the only source of
 * both plants and zombies.
 */
public class Vasebreaker extends MiniGameMode {
    private static final double DROPPED_SEED_LIFETIME_SECONDS = 10.0;
    private static final Random RAND = new Random();

    private final GameSession session;
    private final List<Vase> vases = new ArrayList<>();
    private final List<DroppedSeedPacket> droppedPackets = new ArrayList<>();

    public Vasebreaker(int difficulty, int[] unlockedPlantIds) {
        setDifficulty(difficulty);
        this.session = new GameSession(5, 9);
        layoutVases(unlockedPlantIds);
    }

    private void layoutVases(int[] unlockedPlantIds) {
        int vaseCount = 6 + (getDifficulty() - 1) * 4; // 6, 10, 14
        int gargantuarVases = getDifficulty() >= 3 ? 1 : 0;
        int plantVases = 2 + getDifficulty();

        Environment env = session.getEnvironment();
        List<Position> spots = candidateSpots(env);
        java.util.Collections.shuffle(spots, RAND);

        int placed = 0;
        for (Position spot : spots) {
            if (placed >= vaseCount) break;
            vases.add(nextVase(placed, plantVases, gargantuarVases, spot, unlockedPlantIds));
            placed++;
        }
    }

    private Vase nextVase(int index, int plantVases, int gargantuarVases, Position spot, int[] unlockedPlantIds) {
        if (index < gargantuarVases) return new ZombieVase(spot);
        if (index < gargantuarVases + plantVases) {
            int plantId = unlockedPlantIds[RAND.nextInt(unlockedPlantIds.length)];
            return new PlantVase(spot, plantId);
        }
        return new RandomVase(spot, unlockedPlantIds);
    }

    private List<Position> candidateSpots(Environment env) {
        List<Position> spots = new ArrayList<>();
        for (int row = 0; row < env.getRows(); row++) {
            for (int col = 0; col < env.getCols(); col++) {
                spots.add(new Position(col, row));
            }
        }
        return spots;
    }

    public boolean breakVaseAt(int row, int col) {
        for (Vase vase : vases) {
            if (!vase.isBroken() && (int) vase.getPosition().y() == row && (int) vase.getPosition().x() == col) {
                vase.breakVase(session, this);
                return true;
            }
        }
        return false;
    }

    public void dropSeedPacket(Position position, int plantId) {
        droppedPackets.add(new DroppedSeedPacket(position, plantId));
    }

    public boolean collectSeedPacket(int row, int col) {
        for (DroppedSeedPacket packet : droppedPackets) {
            if (!packet.collected && (int) packet.position.y() == row && (int) packet.position.x() == col) {
                packet.collected = true;
                Plant plant = PlantFactory.createPlant(packet.plantId, 1, packet.position);
                return session.plantAt(row, col, plant);
            }
        }
        return false;
    }

    public void tick(double deltaSeconds) {
        session.tick();
        droppedPackets.forEach(packet -> packet.timeLeft -= deltaSeconds);
        droppedPackets.removeIf(packet -> packet.collected || packet.timeLeft <= 0);
    }

    public boolean isWon() {
        return vases.stream().allMatch(Vase::isBroken) && !session.isGameOver();
    }

    public boolean isLost() {
        return session.isGameOver();
    }

    public GameSession getSession() { return session; }
    public List<Vase> getVases() { return vases; }
    public List<DroppedSeedPacket> getDroppedPackets() { return droppedPackets; }

    public static class DroppedSeedPacket {
        public final Position position;
        public final int plantId;
        public double timeLeft = DROPPED_SEED_LIFETIME_SECONDS;
        public boolean collected = false;

        DroppedSeedPacket(Position position, int plantId) {
            this.position = position;
            this.plantId = plantId;
        }
    }
}
