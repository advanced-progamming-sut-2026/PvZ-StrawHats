package model.collections.zombie;

import model.collections.Faction;
import model.collections.Item;
import model.collections.armour.Armour;
import model.collections.plant.Plant;
import model.match_mechanisms.Attack;
import model.match_mechanisms.vector.Position;
import util.GameSession;

import java.util.Random;

public class Zombie extends Item implements Attack {
    private static final Random RAND = new Random();

    private String name;
    private Armour armour;
    private boolean isFacingRight;
    private boolean hasPlantFood;
    private boolean plantFoodPending;

    private int maxHp;
    private double eatDps;
    private ZombieRace race;
    private ZombieState zombieState = ZombieState.WALKING;
    private final boolean isGlowing;

    public enum Status { NORMAL, FREEZE, FIRED, POISONED, BUTTER, HYPNOTIZED }
    private Status status = Status.NORMAL;
    private VulnerabilityType vulnerabilityState = VulnerabilityType.FULLY_VULNERABLE;
    private Faction faction = Faction.ZOMBIES;

    public Zombie(String name, Position position, int HP, boolean isFacingRight, Armour armour, int speed) {
        super(position, HP);
        this.name = name;
        this.armour = armour;
        this.isFacingRight = isFacingRight;
        this.maxHp = HP;
        setPosition(position);
        setSpeed(new Position(isFacingRight ? speed : -speed, 0));
        this.hasPlantFood = chanceToHavePlantFood();
        this.isGlowing = this.hasPlantFood;
    }

    public Zombie(String name, Armour armour, boolean canSpawnPlantFood) {
        super(null, 1);
        this.name = name;
        this.armour = armour;
        this.isGlowing = canSpawnPlantFood && RAND.nextInt(100) < 5;
    }

    public Zombie(String name, Armour armour) {
        this(name, armour, true);
    }

    public boolean chanceToHavePlantFood() {
        return RAND.nextInt(100) < 5;
    }

    @Override
    public void dealDamage(Item target) {
        if (target == null || !target.isAlive()) return;
        int damage = (int) Math.round(eatDps * service.GameClock.SECONDS_PER_TICK);
        if (damage <= 0) return;

        if (target instanceof Plant plant) {
            plant.takeDamage(damage, this);
        } else if (target instanceof Zombie zombie) {
            zombie.takeDamage(damage, this);
        } else {
            target.takeDamage(damage);
        }
    }

    @Override
    public void takeDamage(int damage) {
        takeDamage(damage, null);
    }

    public void takeDamage(int damage, Object damageSource) {
        if (!isAlive() || this.vulnerabilityState == VulnerabilityType.INVULNERABLE) return;
        if (this.vulnerabilityState == VulnerabilityType.SUBMERGED
                && damageSource instanceof model.projectile.Projectile p
                && !(p.getMoveStrategy() instanceof model.projectile.ArcMove)) {
            return;
        }

        int remaining = (armour != null && armour.getHP() > 0) ? armour.absorbDamage(damage) : damage;
        if (remaining <= 0) return;

        int newHp = Math.max(0, getHP() - remaining);
        setHP(newHp);

        if (newHp <= 0) {
            zombieState = ZombieState.DEAD;
            if (isGlowing) plantFoodPending = true;
        }
    }

    @Override
    public void tick() {
    }

    public void tick(double deltaTimeSeconds, GameSession session) {
        if (!isAlive()) return;

        Item target = acquireTarget(session);
        if (target != null && target.isAlive()) {
            zombieState = ZombieState.EATING;
            dealDamage(target);
        } else {
            zombieState = ZombieState.WALKING;
            move(deltaTimeSeconds);
        }
    }

    private void move(double deltaTimeSeconds) {
        Position pos = getPosition();
        Position vel = getSpeed();
        if (pos == null || vel == null) return;

        double speedMultiplier = (status == Status.FREEZE) ? 0.5 : (status == Status.BUTTER) ? 0 : 1.0;
        setPosition(new Position(
                pos.x() + vel.x() * deltaTimeSeconds * speedMultiplier,
                pos.y() + vel.y() * deltaTimeSeconds * speedMultiplier
        ));
    }

    public Item acquireTarget(GameSession session) {
        return faction.findTarget(this, session);
    }

    public void hypnotize() {
        if (faction == Faction.PLANTS || !isAlive()) return;
        this.faction = Faction.PLANTS;

        Position speed = getSpeed();
        if (speed != null) {
            setSpeed(new Position(-speed.x(), -speed.y()));
        }
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public boolean isHypnotized() {
        return faction == Faction.PLANTS;
    }

    public boolean isPlantFoodPending() {
        return plantFoodPending;
    }

    public void clearPlantFoodPending() {
        plantFoodPending = false;
    }

    public VulnerabilityType getVulnerabilityState() { return vulnerabilityState; }
    public void setVulnerabilityState(VulnerabilityType state) { this.vulnerabilityState = state; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHp() { return getHP(); }
    public void setHp(int hp) {
        setHP(hp);
        if (this.maxHp <= 0) {
            this.maxHp = hp;
        }
    }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public double getEatDps() { return eatDps; }
    public void setEatDps(double eatDps) { this.eatDps = eatDps; }

    public ZombieRace getRace() { return race; }
    public void setRace(ZombieRace race) { this.race = race; }

    public ZombieState getZombieState() { return zombieState; }

    public Armour getArmor() { return armour; }
    public void setArmor(Armour armour) { this.armour = armour; }

    public Armour getArmour() { return armour; }
    public void setArmour(Armour armour) { this.armour = armour; }

    public boolean isGlowing() { return isGlowing; }
    public String getAlias() { return name; }

    public Status getStatus() { return this.status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isFacingRight() { return isFacingRight; }
    public void setFacingRight(boolean facingRight) { isFacingRight = facingRight; }

    public boolean hasPlantFood() { return hasPlantFood; }
    public void setHasPlantFood(boolean hasPlantFood) { this.hasPlantFood = hasPlantFood; }
}
