package model.collections.zombie;

import model.collections.Faction;
import model.collections.Item;
import model.collections.armour.Armour;
import model.collections.plant.Plant;
import model.collections.zombie.zombie_attack.AttackBehavior;
import model.collections.zombie.zombie_defense.DefenseBehavior;
import model.collections.zombie.zombie_effect.ZombieEffectStatus;
import model.collections.zombie.zombie_move.MoveBehavior;
import model.collections.zombie.zombie_pushing_item.PushableStructure;
import model.match_mechanisms.Attack;
import model.match_mechanisms.vector.Position;
import util.GameSession;
import model.projectile.Projectile;
import model.projectile.ArcMove;
import model.collections.zombie.zombie_move.*;

import java.util.Random;
import java.util.List;

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

    private MoveBehavior moveBehavior;
    private AttackBehavior attackBehavior;
    private DefenseBehavior defenseBehavior;
    private ZombieEffectStatus zombieEffectStatus;

    private PushableStructure pushedStructure;
    private int pushableRespawnsRemaining = 0;

    private List<String> damageWhileSubmerged;
    private List<String> damageWhileSubmergedPlantfoodOnly;

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

    public void takeDamage(int damage, boolean isPoisonous) {
        if (!isAlive() || this.vulnerabilityState == VulnerabilityType.INVULNERABLE) return;

        if (isPoisonous) {
            int newHp = Math.max(0, getHP() - damage);
            setHP(newHp);

            if (newHp <= 0) {
                zombieState = ZombieState.DEAD;
                if (isGlowing) plantFoodPending = true;

                GameSession session = GameSession.getInstance();
                if (session != null) {
                    session.notifyZombieDied(this, "Poison");
                }
            }
        } else {
            takeDamage(damage, null);
        }
    }

    public void takeDamage(int damage, Object damageSource) {
        if (!isAlive() || this.vulnerabilityState == VulnerabilityType.INVULNERABLE) return;

        if (this.vulnerabilityState == VulnerabilityType.SUBMERGED) {
            boolean allowDamage = false;

            if (damageSource instanceof Plant plant) {
                String plantName = plant.getName().toLowerCase().replace("-", "").replace(" ", "");

                if (damageWhileSubmerged != null && damageWhileSubmerged.contains(plantName)) {
                    allowDamage = true;
                }
                //    else if (plant.getPlantFoodTimer() > 0 && damageWhileSubmergedPlantfoodOnly != null && damageWhileSubmergedPlantfoodOnly.contains(plantName)) {
                //    allowDamage = true;
                //  }
            } else if (damageSource instanceof Projectile p) {
                if (p.getMoveStrategy() instanceof ArcMove) {
                    allowDamage = true;
                }
            }

            if (!allowDamage) return;
        }

        int actualDamage = damage;
        if (this.defenseBehavior != null) {
            actualDamage = this.defenseBehavior.handleDamage(this, damage, damageSource, GameSession.getInstance());
        }

        if (actualDamage > 0) {
            applyDamageCalculations(actualDamage, damageSource);
        }
    }

    private void applyDamageCalculations(int damage, Object damageSource) {
        int remaining = (armour != null && armour.getHP() > 0) ? armour.absorbDamage(damage) : damage;
        if (remaining <= 0) return;

        int newHp = Math.max(0, getHP() - remaining);
        setHP(newHp);

        if (newHp <= 0) {
            zombieState = ZombieState.DEAD;
            if (isGlowing) {
                plantFoodPending = true;
            }

            GameSession session = GameSession.getInstance();
            if (session != null) {
                String killerName = resolveKillerName(damageSource);
                session.notifyZombieDied(this, killerName);
            }
        }
    }

    private String resolveKillerName(Object damageSource) {
        if (damageSource instanceof Plant plant) {
            return plant.getName();
        }
        return "Unknown";
    }

    @Override
    public void tick() {
        // سازگاری با اینترفیس آیتم
    }

    public void tick(double deltaTimeSeconds, GameSession session) {
        if (!isAlive()) return;

        ZombieFactory.respawnPushedStructureIfNeeded(this);

        if (zombieEffectStatus != null) {
            zombieEffectStatus.applyTickEffect(this, session);
        }

        Item target = acquireTarget(session);
        if (target != null && target.isAlive()) {
            zombieState = ZombieState.EATING;
            if (attackBehavior != null) {
                attackBehavior.attack(this, session);
            } else {
                dealDamage(target);
            }
        } else {
            zombieState = ZombieState.WALKING;
            if (moveBehavior != null) {
                moveBehavior.move(this, deltaTimeSeconds, session);
            } else {
                move(deltaTimeSeconds);
            }
        }
    }

    public void move(double deltaTimeSeconds) {
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

        if (this.moveBehavior != null) {
            this.moveBehavior = new HypnotizedMoveBehavior(this.moveBehavior);
        }
    }

    public MoveBehavior getMoveBehavior() { return moveBehavior; }
    public void setMoveBehavior(MoveBehavior moveBehavior) { this.moveBehavior = moveBehavior; }

    public AttackBehavior getAttackBehavior() { return attackBehavior; }
    public void setAttackBehavior(AttackBehavior attackBehavior) { this.attackBehavior = attackBehavior; }

    public DefenseBehavior getDefenseBehavior() { return defenseBehavior; }
    public void setDefenseBehavior(DefenseBehavior defenseBehavior) { this.defenseBehavior = defenseBehavior; }

    public ZombieEffectStatus getEffectStatus() { return zombieEffectStatus; }
    public void setEffectStatus(ZombieEffectStatus zombieEffectStatus) { this.zombieEffectStatus = zombieEffectStatus; }

    public Faction getFaction() { return faction; }
    public void setFaction(Faction faction) { this.faction = faction; }
    public boolean isHypnotized() { return faction == Faction.PLANTS; }
    public boolean isPlantFoodPending() { return plantFoodPending; }
    public void clearPlantFoodPending() { plantFoodPending = false; }
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

    public PushableStructure getPushedStructure() { return pushedStructure; }
    public void setPushedStructure(PushableStructure pushedStructure) { this.pushedStructure = pushedStructure; }
    public int getPushableRespawnsRemaining() { return pushableRespawnsRemaining; }
    public void setPushableRespawnsRemaining(int remaining) { this.pushableRespawnsRemaining = remaining; }

    public void setDamageWhileSubmerged(List<String> list) { this.damageWhileSubmerged = list; }
    public void setDamageWhileSubmergedPlantfoodOnly(List<String> list) { this.damageWhileSubmergedPlantfoodOnly = list; }
}