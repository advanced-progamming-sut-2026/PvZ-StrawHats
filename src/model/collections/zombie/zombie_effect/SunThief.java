package model.collections.zombie.zombie_effect;

import model.collections.Faction;
import model.collections.item.GroundItem;
import model.collections.item.ItemType;
import model.collections.zombie.Zombie;
import model.pitches.Cell;
import service.GameClock;
import model.utils.GameSession;

public class SunThief implements ZombieEffectStatus {
    private static final double GRAB_PERIOD = 5.0;

    private final boolean directBankStealer;
    private final int lootLimit;
    private final double refundPercentage;
    private final double weaponChargeDuration;
    private final int beamAttackDamage;

    private int collectedSuns = 0;
    private boolean DispenseRefundOnDeath = false;
    private double stateClock = 0;
    private double tickAccumulator = 0;
    private boolean engagingInTheft = false;
    private boolean beamDischarged = false;

    private GroundItem designatedTarget;
    private double lockOnTimer = 0;

    public SunThief(boolean isBankThief, int maxSunsToSteal, double dropRatioOnDeath, double chargingTime, int laserDamage) {
        this.directBankStealer = isBankThief;
        this.lootLimit = maxSunsToSteal;
        this.refundPercentage = dropRatioOnDeath;
        this.weaponChargeDuration = chargingTime;
        this.beamAttackDamage = laserDamage;
    }

    @Override
    public void applyTickEffect(Zombie target, GameSession session) {
        if (!target.isAlive()) {
            if (!DispenseRefundOnDeath) {
                int refundVal = (int) (collectedSuns * refundPercentage);
                if (refundVal > 0) session.addSun(refundVal);
                DispenseRefundOnDeath = true;
            }
            return;
        }

        if (target.getFaction() == Faction.PLANTS) {
            if (directBankStealer && !beamDischarged) {
                scorchHostilesWithBeam(target, session);
                beamDischarged = true;
            }
            return;
        }

        if (directBankStealer) {
            handleVaultBreaker(target, session);
        } else {
            handleScavengerBehavior(session);
        }
    }

    private void handleScavengerBehavior(GameSession session) {
        if (collectedSuns >= lootLimit) return;

        if (designatedTarget != null && (!designatedTarget.isAlive() || designatedTarget.getItemType() != ItemType.SUN)) {
            designatedTarget = null;
            lockOnTimer = 0;
        }

        if (designatedTarget == null) {
            designatedTarget = scanForFallenSun(session);
            lockOnTimer = 0;
            if (designatedTarget == null) return;
        }

        lockOnTimer += GameClock.SECONDS_PER_TICK;

        if (lockOnTimer >= GRAB_PERIOD) {
            consumeGroundSun(designatedTarget);
            designatedTarget = null;
            lockOnTimer = 0;
        }
    }

    private GroundItem scanForFallenSun(GameSession session) {
        for (GroundItem groundLoot : session.getGroundItems()) {
            if (!groundLoot.isAlive() || groundLoot.getItemType() != ItemType.SUN) continue;
            if (groundLoot instanceof SunToken token && token.isFalling()) continue;
            return groundLoot;
        }
        return null;
    }

    private void consumeGroundSun(GroundItem sunItem) {
        int extractedAmount = 0;
        if (sunItem instanceof ProducedSun prodSun) {
            extractedAmount = prodSun.getValue();
        } else if (sunItem instanceof SunToken token) {
            extractedAmount = token.getValue();
        }

        if (extractedAmount > 0) {
            collectedSuns = Math.min(collectedSuns + extractedAmount, lootLimit);
        }
        sunItem.setAlive(false);
    }

    private void handleVaultBreaker(Zombie raider, GameSession session) {
        if (beamDischarged) return;

        if (!engagingInTheft) {
            if (detectImminentVegetation(raider, session)) engagingInTheft = true;
        } else {
            stateClock += GameClock.SECONDS_PER_TICK;
            tickAccumulator += GameClock.SECONDS_PER_TICK;

            if (tickAccumulator >= 1.0 && stateClock <= weaponChargeDuration) {
                tickAccumulator = 0;
                int stealRate = Math.min(25, session.getSunCount());
                if (stealRate > 0) {
                    session.spendSun(stealRate);
                    collectedSuns += stealRate;
                }
            }

            if (stateClock >= weaponChargeDuration) {
                dischargeBeamWeapons(raider, session);
                beamDischarged = true;
                engagingInTheft = false;
            }
        }
    }

    private boolean detectImminentVegetation(Zombie raider, GameSession session) {
        int r = (int) raider.getPosition().y();
        int c = (int) raider.getPosition().x();
        for (int step = 1; step <= 4; step++) {
            int scanCol = c - step;
            if (scanCol >= 0) {
                Cell cell = session.getLawn().getCell(r, scanCol);
                if (cell != null && cell.getPlant() != null && cell.getPlant().isAlive()) return true;
            }
        }
        return false;
    }

    private void dischargeBeamWeapons(Zombie raider, GameSession session) {
        int r = (int) raider.getPosition().y();
        int c = (int) raider.getPosition().x();
        for (int step = 1; step <= 4; step++) {
            int scanCol = c - step;
            if (scanCol >= 0) {
                Cell cell = session.getLawn().getCell(r, scanCol);
                if (cell != null && cell.getPlant() != null && cell.getPlant().isAlive()) {
                    cell.getPlant().takeDamage(beamAttackDamage, raider);
                }
            }
        }
    }

    private void scorchHostilesWithBeam(Zombie renegade, GameSession session) {
        int r = (int) renegade.getPosition().y();
        int c = (int) renegade.getPosition().x();
        session.getZombies().stream()
                .filter(z -> z.isAlive() && z.getFaction() == Faction.ZOMBIES && (int) z.getPosition().y() == r)
                .forEach(z -> {
                    double range = z.getPosition().x() - c;
                    if (range > 0 && range <= 4.0) z.takeDamage(beamAttackDamage);
                });
    }
}
