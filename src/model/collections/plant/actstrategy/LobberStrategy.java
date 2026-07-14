package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.projectile.ArcMove;
import model.projectile.Projectile;
import model.projectile.hit.FireHit;
import model.projectile.hit.HitEffectStrategy;
import model.projectile.hit.IceHit;
import model.projectile.hit.NormalHit;
import model.projectile.hit.PierceHit;
import model.projectile.hit.PoisonHit;
import util.GameSession;

public class LobberStrategy implements ActStrategy {
    private static final double GRAVITY = 15.0;
    private static final double HORIZONTAL_SPEED = 4.0;
    private static final double MIN_DISTANCE_X = 0.1;

    @Override
    public void act(Plant user, GameSession session) {
        if (user.getIntervalTimer() > 0) return;

        Zombie target = findNearestInLane(user, session);
        if (target == null) return;

        Position startPos = user.getPosition();
        Position targetPos = target.getPosition();
        if (targetPos == null) return;

        double distanceX = targetPos.x() - startPos.x();
        if (distanceX < MIN_DISTANCE_X) return;

        double distanceY = targetPos.y() - startPos.y();
        double timeOfFlight = distanceX / HORIZONTAL_SPEED;
        double initialVelocityY = (distanceY - 0.5 * GRAVITY * timeOfFlight * timeOfFlight) / timeOfFlight;

        Position initialVelocity = new Position(HORIZONTAL_SPEED, initialVelocityY);
        HitEffectStrategy hitEffect = buildHitEffect(user);

        session.getProjectiles().add(new Projectile(
                startPos,
                initialVelocity,
                target,
                user.getDamage(),
                new ArcMove(GRAVITY),
                hitEffect
        ));

        user.setInternalTimer(user.getActionInterval());
    }

    private HitEffectStrategy buildHitEffect(Plant user) {
        int areaLength = user.getTags().contains(PlantTag.AOE) ? 3 : 1;
        if (user.getTags().contains(PlantTag.FIRE)) return new FireHit(areaLength);
        if (user.getTags().contains(PlantTag.ICE)) return new IceHit(areaLength);
        if (user.getTags().contains(PlantTag.POISON)) return new PoisonHit(areaLength);
        if (user.getTags().contains(PlantTag.PIERCE)) return new PierceHit(-1);
        return new NormalHit(areaLength);
    }

    private Zombie findNearestInLane(Plant user, GameSession session) {
        double plantRow = user.getPosition().y();
        double plantCol = user.getPosition().x();
        Zombie nearest = null;
        double minX = Double.MAX_VALUE;

        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive()) continue;
            Position zp = zombie.getPosition();
            if (zp == null) continue;

            if (Math.abs(zp.y() - plantRow) < 0.5 && zp.x() > plantCol) {
                if (zp.x() < minX) {
                    minX = zp.x();
                    nearest = zombie;
                }
            }
        }
        return nearest;
    }
}
