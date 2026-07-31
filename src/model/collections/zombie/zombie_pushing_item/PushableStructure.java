package model.collections.zombie.zombie_pushing_item;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.ZombieFactory;
import model.match_mechanisms.vector.Position;
import model.pitches.obstacles.PushableType;
import model.utils.GameSession;

public class PushableStructure {
    private final PushableType type;
    private Position position;
    private int hp;
    private Zombie owner;
    private boolean destructionHandled = false;

    public PushableStructure(PushableType type, Position position) {
        this.type = type;
        this.position = position;
        this.hp = switch (type) {
            case ICE_BLOCK -> 600;
            case ARCADE_CABINET, BARREL -> 1100;
        };
    }

    public boolean isAlive() { return hp > 0; }
    public PushableType getType() { return type; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, hp); }
    public Zombie getOwner() { return owner; }
    public void setOwner(Zombie owner) { this.owner = owner; }

    public void takeDamage(int damage, Plant source, GameSession session) {
        if (!isAlive() || damage <= 0) return;
        hp = Math.max(0, hp - damage);
        if (hp == 0) onDestroyed(session);
    }

    private void onDestroyed(GameSession session) {
        if (destructionHandled) return;
        destructionHandled = true;
        if (type != PushableType.BARREL || session == null || position == null) return;

        int row = (int) Math.round(position.y());
        int col = Math.max(0, Math.min(session.getCols() - 1, (int) Math.round(position.x())));
        for (int i = 0; i < 2; i++) {
            Zombie imp = ZombieFactory.create("ZombieImp", row, Math.min(session.getCols() - 1, col + i));
            session.spawnZombie(imp);
        }
    }
}
