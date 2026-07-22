package model.projectile;

import model.collections.Item;
import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.projectile.hit.HitEffectStrategy;

public class Projectile extends Item {
    private int damage;
    private Item target;
    private Plant sourcePlant;
    private boolean isStunning;

    private MoveStrategy moveStrategy;
    private HitEffectStrategy hitEffectStrategy;

    public Projectile(Position position, Position velocity, Zombie zombie, int damage, MoveStrategy moveStrategy, HitEffectStrategy hitEffectStrategy) {
        this((Item) zombie, position, velocity, damage, moveStrategy, hitEffectStrategy);
    }

    public Projectile(Plant sourcePlant, Position position, Position velocity, Zombie zombie, int damage,
                      MoveStrategy moveStrategy, HitEffectStrategy hitEffectStrategy) {
        this(position, velocity, zombie, damage, moveStrategy, hitEffectStrategy);
        this.sourcePlant = sourcePlant;
    }

    public Projectile(Item target, Position position, Position velocity, int damage, MoveStrategy moveStrategy, HitEffectStrategy hitEffectStrategy) {
        super(position, 1);
        this.setPosition(position);
        this.setSpeed(velocity);
        this.target = target;
        this.damage = damage;
        this.moveStrategy = moveStrategy;
        this.hitEffectStrategy = hitEffectStrategy;
        this.isStunning = false;
    }

    public void setStunning(boolean isStunning) {
        this.isStunning = isStunning;
    }

    @Override
    public void tick() {
        if (!isAlive) return;
        if (target == null || !target.isAlive()) {
            setAlive(false);
            return;
        }

        Position previousPosition = getPosition();

        if (moveStrategy != null) {
            moveStrategy.move(this);
        }

        boolean hasHitTarget = checkCollision(target, previousPosition);

        if (hasHitTarget) {
            if (target instanceof Zombie zombie) {
                zombie.takeDamage(damage, this);
            } else {
                target.takeDamage(damage);
            }

            if (hitEffectStrategy != null && target instanceof Zombie zombie) {
                hitEffectStrategy.apply(zombie);
            }
            this.isAlive = false;
        }
    }

    private boolean checkCollision(Item target, Position previousPosition) {
        if (target == null || !target.isAlive()) return false;
        Position targetPos = resolveTargetPosition(target);
        Position currentPosition = getPosition();
        if (targetPos == null || previousPosition == null || currentPosition == null) return false;

        Position movement = currentPosition.sub(previousPosition);
        double lengthSquared = movement.dot(movement);
        if (lengthSquared == 0) return currentPosition.distanceTo(targetPos) <= 0.5;

        double projection = targetPos.sub(previousPosition).dot(movement) / lengthSquared;
        double clamped = Math.max(0, Math.min(1, projection));
        Position closestPoint = previousPosition.add(movement.scale(clamped));
        return closestPoint.distanceTo(targetPos) <= 0.5;
    }

    private Position resolveTargetPosition(Item target) {
        if (target instanceof Zombie zombie) {
            return zombie.getPosition();
        }
        if (target instanceof Plant plant) {
            var loc = plant.getLocation();
            return loc == null ? null : Position.of(loc.x(), loc.y());
        }
        return null;
    }

    public void setHitEffectStrategy(HitEffectStrategy strategy) {
        this.hitEffectStrategy = strategy;
    }

    public HitEffectStrategy getHitEffectStrategy() {
        return this.hitEffectStrategy;
    }

    public Object getMoveStrategy() {
        return moveStrategy;
    }

    public int getDamage() {
        return damage;
    }

    public Plant getSourcePlant() {
        return sourcePlant;
    }

}
