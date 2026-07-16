package model.projectile.zombie_projectile;

import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.utils.GameSession;

public class ZombiePeaProjectile extends ZombieProjectile {
    private final int damage;

    public ZombiePeaProjectile(Position startPosition, int damage, GameSession session) {
        super(startPosition, new Position(-1, startPosition.y()), (startPosition.x() + 1) / 4.0, "PeashooterZombie", session);
        this.damage = damage;
    }

    @Override
    protected void updateFlightPath(double progress) {
        double currentX = startPosition.x() + (targetPosition.x() - startPosition.x()) * progress;
        this.setPosition(new Position(currentX, startPosition.y()));

        if (session == null || session.getEnvironment() == null) return;

        int row = (int) startPosition.y();
        int col = (int) Math.round(currentX);

        if (col >= 0 && col < session.getEnvironment().getCols()) {
            Cell cell = session.getEnvironment().getCell(row, col);
            if (cell != null && cell.getPlant() != null && cell.getPlant().isAlive()) {
                cell.getPlant().takeDamage(damage, null);
                this.setAlive(false);
            }
        }
    }

    @Override
    protected void onDestinationReached(GameSession session) {
        // گلوله نخود بدون برخورد به چیزی از زمین بازی خارج شد. شاید بعدا این رو باید یکمی تغییر بدیم فعلا ولی کاری نمیکنه خالیه
    }
}
