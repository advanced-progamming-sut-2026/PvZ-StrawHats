package model.collections.plant;

import model.collections.Item;
import model.collections.plant.actstrategy.ActStrategy;
import model.match_mechanisms.Attack;
import model.match_mechanisms.Pluck;
import model.match_mechanisms.vector.Position;
import model.collections.zombie.Zombie;
import model.collections.armour.PlantArmour;
import com.ussr.pvz.model.entities.plants.actstrategy.ActStrategy;
import com.ussr.pvz.model.entities.plants.PlantFoodType;
import com.ussr.pvz.model.engine.GameSession;
import com.ussr.pvz.model.util.Vec2;
import util.GameSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Plant extends Item implements Pluck, Attack {
    private int id;
    private String name;
    private int level = 1;
    private int recharge;
    private double actionInterval;
    private int cost;
    private final ArrayList<PlantTag> tags = new ArrayList<>();
    private int damage;
    private PlantType type;
    private Plant bottom = null;
    private int stackNumber = 1;

    private ModifiableStat hpStat;
    private ModifiableStat actionIntervalStat;
    private ActStrategy actStrategy;
    private com.ussr.pvz.model.entities.plants.PlantFoodEffect plantFoodEffect;
    private PlantFoodType plantFoodType;

    private double internalTimer = 0.0;
    private double abilityValue;
    private int chillLevel = 0;
    private GrowthTracker growthTracker;
    private double plantFoodTimer = 0.0;

    private PlantArmour armor;

    public enum PlantState { ACTIVE, INCAPACITATED, PREPPING, DYING }
    private PlantState state = PlantState.ACTIVE;
    private final List<String> rawUpgrades = new ArrayList<>();
    private List<Position> shootingVectors = new ArrayList<>();

    public Plant(String name, Position position, int HP) {
        super(position, HP);
        this.name = name;
        this.hpStat = new ModifiableStat(HP);
    }

    public double getIntervalTimer() { return this.internalTimer; }
    public void setInternalTimer(double internalTimer) { this.internalTimer = internalTimer; }

    public Position getPosition() {
        return new Position(getLocation().x(), getLocation().y());
    }

    public Position getLocation() {
        return (Position) super.getPosition();
    }

    public void tick(double deltaTimeSeconds, util.GameSession session) {
        if (state == PlantState.INCAPACITATED) return;

        if (hpStat != null) hpStat.update((float) deltaTimeSeconds);
        if (actionIntervalStat != null) actionIntervalStat.update((float) deltaTimeSeconds);
        if (growthTracker != null) growthTracker.update(deltaTimeSeconds);

        if (plantFoodTimer > 0) {
            plantFoodTimer -= deltaTimeSeconds;
            if (plantFoodEffect != null) {
                plantFoodEffect.tickDurationEffect(this, deltaTimeSeconds);
            }
            return;
        }

        if (actStrategy == null) return;
        internalTimer -= deltaTimeSeconds;

        actStrategy.act(this, session);
    }

    public void takeDamage(int damageAmount, Zombie dealer) {
        int remainingDamage = damageAmount;

        if (this.armor != null && !this.armor.isDestroyed()) {
            remainingDamage = this.armor.absorbDamage(remainingDamage);
            this.armor.handleReflection(dealer, this);

            if (this.armor.isDestroyed()) {
                if (this.armor.isExplodeOnBreak()) {
                    executeArmorExplosion();
                }
                this.armor = null;
            }
        }

        if (remainingDamage > 0) {
            int newHp = getHP() - remainingDamage;
            if (newHp <= 0) {
                setHP(0);
                this.state = PlantState.DYING;
            } else {
                setHP(newHp);
            }
        }
    }

    private void executeArmorExplosion() {}

    @Override public void dealDamage(Item target) { if (target != null) target.setHP(target.getHP() - getDamage()); }
    public void activatePlant() { if (this.plantFoodEffect != null) this.plantFoodTimer = 5.0; }

    // گترها و سترها
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getRecharge() { return recharge; }
    public void setRecharge(int recharge) { this.recharge = recharge; }
    public double getActionInterval() { return actionIntervalStat != null ? actionIntervalStat.getValue() : actionInterval; }
    public void setActionInterval(double actionInterval) {
        this.actionInterval = actionInterval;
        if (this.actionIntervalStat == null) this.actionIntervalStat = new ModifiableStat((float) actionInterval);
        else this.actionIntervalStat.setBaseValue((float) actionInterval);
    }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getDamage() {
        if (growthTracker != null) {
            Double staged = growthTracker.getStageValue("damage");
            if (staged != null) return staged.intValue();
        }
        return this.damage;
    }
    public void setDamage(int damage) { this.damage = damage; }
    public PlantType getType() { return type; }
    public void setType(PlantType type) { this.type = type; }
    public ArrayList<PlantTag> getTags() { return tags; }
    public List<String> getRawUpgrades() { return rawUpgrades; }
    public void setActStrategy(ActStrategy actStrategy) { this.actStrategy = actStrategy; }
    public void setPlantFoodEffect(com.ussr.pvz.model.entities.plants.PlantFoodEffect plantFoodEffect) { this.plantFoodEffect = plantFoodEffect; }
    public void setPlantFoodType(PlantFoodType plantFoodType) { this.plantFoodType = plantFoodType; }
    public void setAbilityValue(double value) { this.abilityValue = value; }
    public double getAbilityValue() { return this.abilityValue; }
    public void setWrampUp(List<Map<String, Object>> wrampUp) { this.growthTracker = (wrampUp != null && !wrampUp.isEmpty()) ? new GrowthTracker(wrampUp) : null; }
    public List<Position> getShootingVectors() { return shootingVectors; }
    public void setShootingVectors(List<Position> shootingVectors) { this.shootingVectors = shootingVectors; }
    public PlantArmour getArmor() { return armor; }
    public void setArmor(PlantArmour armor) { this.armor = armor; }
}