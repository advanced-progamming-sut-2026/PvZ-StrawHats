package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import model.collections.plant.PlantTag;
import model.match_mechanisms.vector.Position;
import model.projectile.Projectile;
import model.projectile.hit.FireHit;
import model.projectile.hit.IceHit;
import model.projectile.hit.PoisonHit;
import model.utils.GameSession;

import java.util.ArrayList;

public class ModifyStrategy implements ActStrategy {
    @Override
    // ability values: 1 for pads(do nothing) 2 for (Torchwood) 3 for (hypnotism)
    // the hypno will handled in the zombie and plant in the future
    public void act(Plant user, GameSession session) {
        switch ((int) user.getAbilityValue()) {
            case 1:
                return;
            case 2:
                ArrayList<Projectile> targets = projectileThroughDetect(user, session);
                modifyTargets(user, targets);
                break;
        }
    }

    private ArrayList<Projectile> projectileThroughDetect(Plant user, GameSession session) {
        Position userPos = user.getPosition();
        ArrayList<Projectile> targets = new ArrayList<>();
        for (Projectile projectile : session.getProjectiles()) {
            Position projPos = projectile.getPosition();
            if (Math.abs(userPos.distanceTo(projPos)) < 0.7)
                targets.add(projectile);
        }
        return targets;
    }

    private void modifyTargets(Plant user, ArrayList<Projectile> targets) {
        if (user.getTags().contains(PlantTag.FIRE)) {
            for (Projectile projectile : targets) {
                if (projectile.getHitEffectStrategy() instanceof FireHit)
                    continue;
                projectile.setHitEffectStrategy(new FireHit(1));
            }
        } else if (user.getTags().contains(PlantTag.ICE)) {
            for (Projectile projectile : targets) {
                if (projectile.getHitEffectStrategy() instanceof IceHit)
                    continue;
                projectile.setHitEffectStrategy(new IceHit(1));
            }
        } else if (user.getTags().contains(PlantTag.POISON)) {
            for (Projectile projectile : targets) {
                if (projectile.getHitEffectStrategy() instanceof PoisonHit)
                    continue;
                projectile.setHitEffectStrategy(new PoisonHit(1));
            }
        }
    }
}
