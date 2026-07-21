package model.collections.item;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;

import java.util.Random;

public class GroundSun extends GroundItem {
    private static final Random RANDOM = new Random();

    public enum SunDropType {
        REGULAR(80, 25),
        SPECIAL(15, 100),
        RADIOACTIVE(5, 25);

        private final int probability;
        private final int value;

        SunDropType(int probability, int value) {
            this.probability = probability;
            this.value = value;
        }

        public int getProbability() {
            return probability;
        }

        public int getValue() {
            return value;
        }

        public static SunDropType rollRandom() {
            int roll = RANDOM.nextInt(100);
            int cumulative = 0;
            for (SunDropType type : values()) {
                cumulative += type.probability;
                if (roll < cumulative) return type;
            }
            return REGULAR;
        }
    }

    private final SunDropType dropType;
    private final int sunValue;

    public GroundSun(Position position, int sunValue) {
        super(ItemType.SUN, position, 15, 0.6);
        this.dropType = SunDropType.REGULAR;
        this.sunValue = sunValue;
    }

    private GroundSun(Position position, SunDropType dropType) {
        super(ItemType.SUN, position, 12, 0.6);
        this.dropType = dropType;
        this.sunValue = dropType.getValue();
    }

    public static GroundSun fallFromSky(Position position) {
        return new GroundSun(position, SunDropType.rollRandom());
    }

    @Override
    public void applyRewards(GameSession session, UserState state) {
        session.addSun(sunValue);
        if (dropType == SunDropType.RADIOACTIVE) {
            explodeRadioactive(session);
        }
    }

    private void explodeRadioactive(GameSession session) {
        Position center = getPosition();
        if (center == null) return;

        for (Zombie zombie : session.getZombies()) {
            if (!zombie.isAlive() || zombie.getPosition() == null) continue;
            if (Math.abs(zombie.getPosition().y() - center.y()) <= 2
                    && Math.abs(zombie.getPosition().x() - center.x()) <= 2) {
                zombie.takeDamage(150, (Object) null);
            }
        }

        for (Plant plant : session.getPlants()) {
            if (!plant.isAlive() || plant.getPosition() == null) continue;
            if (Math.abs(plant.getPosition().y() - center.y()) <= 1
                    && Math.abs(plant.getPosition().x() - center.x()) <= 1) {
                plant.takeDamage(80, null);
            }
        }
    }

    public SunDropType getDropType() {
        return dropType;
    }

    public int getSunValue() {
        return sunValue;
    }
}