package model.pitches.obstacles;

import java.util.Random;

public class Grave implements Obstacle {
    public enum Reward { NONE, SUN, PLANT_FOOD }

    private static final Random RANDOM = new Random();
    private int hp = 700;
    private final Reward reward;

    public Grave() {
        this(Reward.NONE);
    }

    public Grave(Reward reward) {
        this.reward = reward == null ? Reward.NONE : reward;
    }

    public static Reward randomDarkAgeReward() {
        return RANDOM.nextBoolean() ? Reward.SUN : Reward.PLANT_FOOD;
    }

    public boolean takeDamage(int damage) {
        hp = Math.max(0, hp - Math.max(0, damage));
        return hp == 0;
    }

    public int getHp() { return hp; }
    public Reward getReward() { return reward; }

    @Override
    public boolean blocksPlanting() { return true; }

    @Override
    public String getName() { return "Grave"; }
}