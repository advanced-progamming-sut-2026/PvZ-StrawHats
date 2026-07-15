package model.collections.zombie.zombie_pushing_item;

import model.collections.zombie.PushableType;
import model.match_mechanisms.vector.Position;

public class PushableStructure {
    private final PushableType type;
    private Position position;
    private int hp = 500;

    public PushableStructure(PushableType type, Position position) {
        this.type = type;
        this.position = position;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public PushableType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
}