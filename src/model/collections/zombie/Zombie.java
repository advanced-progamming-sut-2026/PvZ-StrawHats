package model.collections.zombie;

import model.collections.Faction;
import model.collections.Item;
import model.collections.armour.Armour;
import model.collections.armour.ZombieArmour;
import model.collections.zombie.zombie_move.MoveBehavior;
import model.match_mechanisms.Attack;
import model.match_mechanisms.vector.Position;

import com.ussr.pvz.model.App;
import com.ussr.pvz.model.board.structures.PushableStructure;
import com.ussr.pvz.model.entities.items.PlantFoodDrop;
import com.ussr.pvz.model.entities.zombies.attack.AttackBehavior;
import com.ussr.pvz.model.entities.zombies.defense.DefenseBehavior;
import com.ussr.pvz.model.entities.zombies.effect.EffectStatus;
import com.ussr.pvz.model.entities.zombies.move.HypnotizedMoveBehavior;
import com.ussr.pvz.model.entities.projectiles.move.ArcMove;
import model.pitches.Square;

import java.util.Random;

public class Zombie extends Item implements Attack {
    private static final Random RAND = new Random();

    private String name;
    private ZombieArmour armour;
    private boolean isFacingRight;
    private int speed;
    private boolean hasPlantFood;

    private MoveBehavior moveBehavior;
    private EffectStatus effectStatus;
    private DefenseBehavior defenseBehavior;
    private AttackBehavior attackBehavior;
    private PushableStructure pushedStructure;
    private int hp;
    private int maxHp;
    private double eatDps;
    private ZombieRace race;
    private ZombieState state = ZombieState.WALKING;
    private final boolean isGlowing;

    public enum Status { NORMAL, FREEZE, FIRED, POISONED, BUTTER, HYPNOTIZED }
    private Status status = Status.NORMAL;
    private VulnerabilityType vulnerabilityState = VulnerabilityType.FULLY_VULNERABLE;
    private Faction faction = Faction.ZOMBIES;

    public Zombie(String name, Position position, int HP, boolean isFacingRight, ZombieArmour armor, int speed) {
        super(position, HP);
        this.name = name;
        this.armour = armor;
        this.isFacingRight = isFacingRight;
        this.speed = speed;
        this.hp = HP;
        this.maxHp = HP;
        this.hasPlantFood = chanceToHavePlantFood();
        this.isGlowing = this.hasPlantFood;
    }

    public Zombie(String name, ZombieArmour armor, boolean canSpawnPlantFood) {
        super(null, 0);
        this.name = name;
        this.armour = armor;
        this.isGlowing = canSpawnPlantFood && RAND.nextInt(100) < 5;
    }

    public Zombie(String name, ZombieArmour armour) {
        this(name, armour, true);
    }

    public void handleMovement() {}

    public void handleAbility() {}

    public boolean chanceToHavePlantFood() {
        Random random = new Random();
        int chance = random.nextInt(100);
        return chance < 5;
    }

    @Override
    public void dealDamage(Item target) {
        // متد اینترفیس Attack کد خودتان
    }

    @Override
    public void takeDamage(int damage) {
        // این متد در کد دوم بدنه نداشت اما در بدنه اینترفیس Damageable وجود دارد
    }

    public void takeDamage(int damage, boolean isPoisonous) {
        // نکته: فیلد isAlive در کد دوم احتمالاً متعلق به GameEntity بوده، اینجا ارور می‌دهد تا خودتان با متد کلاس Item (مثل HP > 0) جایگزین کنید.
        if (!isAlive || this.vulnerabilityState == VulnerabilityType.INVULNERABLE) return;
        if (isPoisonous) {
            // Poison bypasses armor and defense calculations directly
            this.hp -= damage;
            if (this.hp <= 0) {
                this.hp = 0;
                this.isAlive = false;
                this.state = ZombieState.DEAD;
                // Handle plant food drops on death
                if (isGlowing) {
                    com.ussr.pvz.model.entities.items.PlantFoodDrop plantFoodDrop =
                            new com.ussr.pvz.model.entities.items.PlantFoodDrop(1);
                    plantFoodDrop.setPosition(this.getPosition());
                    util.GameSession session = App.getGameSession();
                    if (session != null) {
                        session.getItems().add(plantFoodDrop);
                    }
                }
            }
        } else {
            // Route standard damage back to the main pipeline
            takeDamage(damage, null);
        }
    }

    @Override
    public void tick() {
        if (!isAlive) return;
        util.GameSession session = App.getGameSession();
        if (session == null) return;
        if (effectStatus != null) effectStatus.effect(this, session);
        Item target = acquireTarget(session);
        if (target != null && target.isAlive()) {
            state = ZombieState.EATING;
            if (attackBehavior != null) attackBehavior.attack(this, session);
        } else {
            state = ZombieState.WALKING;
            if (moveBehavior != null) moveBehavior.move(this, session);
        }
    }

    public Item acquireTarget(util.GameSession session) {
        return faction.findTarget(this, session);
    }

    public void hypnotize() {
        if (faction == Faction.PLANTS || !isAlive) return;
        this.faction = Faction.PLANTS;

        // نکته مهم: در کد شما getSpeed خروجی int دارد اما کد دوم فرض کرده شیء است و متد .scale دارد.
        // این خط ارور خواهد داد تا مدل سیستم سرعت (عددی یا برداری) را خودتان یکسان‌سازی کنید.
        if (getSpeed() != null) {
            setSpeed(getSpeed().scale(-1));
        }
        this.moveBehavior = new HypnotizedMoveBehavior(this.moveBehavior);
    }

    public Faction getFaction() {
        return faction;
    }

    public boolean isHypnotized() {
        return faction == Faction.PLANTS;
    }

    public Square getCurrentCell(util.GameSession session) {
        if (getPosition() == null || session.getLawn() == null) return null;
        int col = (int) getPosition().x();
        int row = (int) getPosition().y();
        return session.getLawn().getCell(row, col);
    }

    public void takeDamage(int damage, Object damageSource) {
        if (!isAlive || this.vulnerabilityState == VulnerabilityType.INVULNERABLE) return;
        if (this.vulnerabilityState == VulnerabilityType.SUBMERGED) {
            if (damageSource instanceof model.projectile.Projectile p && !(p.getMoveStrategy() instanceof ArcMove)) {
                return;
            }
        }
        int actualDamage = damage;
        if (this.defenseBehavior != null) {
            actualDamage = this.defenseBehavior.handleDamage(this, damage, damageSource, App.getGameSession());
        }
        if (actualDamage > 0) {
            applyDamageCalculations(actualDamage);
        }
    }

    private void applyDamageCalculations(int damage) {
        int remaining = damage;
        if (armour != null && !armour.isDestroyed()) {
            remaining = armour.takeDamage(damage);
        }
        if (remaining > 0) {
            hp -= remaining;
            if (hp <= 0) {
                hp = 0;
                isAlive = false;
                state = ZombieState.DEAD;
                if (isGlowing) {
                    PlantFoodDrop plantFoodDrop = new PlantFoodDrop(1);
                    plantFoodDrop.setPosition(this.getPosition());
                    util.GameSession session = App.getGameSession();
                    if (session != null) {
                        session.getItems().add(plantFoodDrop);
                    }
                }
            }
        }
    }

    // مجموعه‌ای کامل از گترها و سترهای ادغام شده هر دو کد
    public VulnerabilityType getVulnerabilityState() { return vulnerabilityState; }
    public void setVulnerabilityState(VulnerabilityType state) { this.vulnerabilityState = state; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHp() { return hp; }
    public void setHp(int hp) {
        this.hp = hp;
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

    public ZombieState getZombieState() { return state; }

    public Armour getArmor() { return armour; }
    public void setArmor(ZombieArmour armor) { this.armour = armor; }

    public ZombieArmour getArmour() { return armour; }
    public void setArmour(ZombieArmour armour) { this.armour = armour; }

    public MoveBehavior getMoveBehavior() { return moveBehavior; }
    public void setMoveBehavior(MoveBehavior moveBehavior) { this.moveBehavior = moveBehavior; }

    public AttackBehavior getAttackBehavior() { return attackBehavior; }
    public void setMoveBehavior(AttackBehavior attackBehavior) { this.attackBehavior = attackBehavior; }

    public DefenseBehavior getDefenseBehavior() { return defenseBehavior; }
    public void setDefenseBehavior(DefenseBehavior defenseBehavior) { this.defenseBehavior = defenseBehavior; }

    public EffectStatus getEffectStatus() { return effectStatus; }
    public void setEffectStatus(EffectStatus effectStatus) { this.effectStatus = effectStatus; }

    public boolean isGlowing() { return isGlowing; }
    public String getAlias() { return name; }

    public Status getStatus() { return this.status; }
    public void setStatus(Status status) { this.status = status; }

    public void setFaction(Faction faction) { this.faction = faction; }

    public PushableStructure getPushedStructure() { return pushedStructure; }
    public void setPushedStructure(PushableStructure pushedStructure) { this.pushedStructure = pushedStructure; }

    public boolean isFacingRight() { return isFacingRight; }
    public void setFacingRight(boolean facingRight) { isFacingRight = facingRight; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public boolean hasPlantFood() { return hasPlantFood; }
    public void setHasPlantFood(boolean hasPlantFood) { this.hasPlantFood = hasPlantFood; }
}