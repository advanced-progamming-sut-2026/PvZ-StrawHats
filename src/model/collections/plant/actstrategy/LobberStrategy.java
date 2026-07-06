package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.projectile.Projectile;
import util.GameSession;

public class LobberStrategy implements ActStrategy {
    private static final double GRAVITY = 15.0;
    private static final double HORIZONTAL_SPEED = 4.0;

    @Override
    public void act(Plant user, GameSession session) {
        if (user.getIntervalTimer() > 0) return;

        user.setInternalTimer(user.getActionInterval());

        Zombie target = findNearestInLane(user, session);
        if (target == null) return;
        Position startPos = user.getPosition();
        Position targetPos = target.getPosition();
        double distanceX = targetPos.x() - startPos.x();
        double timeOfFlight = distanceX / HORIZONTAL_SPEED;
        double initialVelocityY = -0.5 * GRAVITY * timeOfFlight;

        Position initialVelocity = new Position(HORIZONTAL_SPEED, initialVelocityY);
        HitEffectStrategy hitEffect = buildHitEffect(user);
        session.getProjectiles().add(new Projectile(
                user.getPosition(),
                initialVelocity, target,
                user.getDamage(),
                new ArcMove(GRAVITY),
                hitEffect
        ));
    }


    private HitEffectStrategy buildHitEffect(Plant user) {
        int areaLength = user.getTags().contains(PlantTag.AOE) ? 3 : 1;
        if (user.getTags().contains(PlantTag.FIRE)) return new FireHit(areaLength);
        if (user.getTags().contains(PlantTag.ICE)) return new IceHit(areaLength);
        if (user.getTags().contains(PlantTag.POISON)) return new PoisonHit(areaLength);
        return new NormalHit(areaLength);
    }


    private Zombie findNearestInLane(Plant user, GameSession session) {
        double plantRow = user.getPosition().y();
        Zombie nearest = null;
        double minX = Double.MAX_VALUE;

        for (Zombie zombie : session.getZombies()) {
            if (zombie == null || !zombie.isAlive()) continue;
            Position zp = zombie.getPosition();

            if (Math.abs(zp.y() - plantRow) < 0.5 && zp.x() > user.getPosition().x()) {
                if (zp.x() < minX) {
                    minX = zp.x();
                    nearest = zombie;
                }
            }
        }
        return nearest;
    }
}
