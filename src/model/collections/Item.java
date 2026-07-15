package model.collections;

import model.match_mechanisms.vector.Position;
import model.utils.state.ItemState;


public abstract class Item {
    public Position position;
    protected int HP;

    private Position externalPosition;
    private Position speed;
    protected boolean isAlive = true;
    private ItemState state;

    protected Item(Position position, int HP) {
        this.position = position;
        this.HP = HP;
        this.isAlive = HP > 0;
    }

    public abstract void tick();

    public void takeDamage(int damage) {
        if (!isAlive) return;

        HP -= damage;
        if (HP <= 0) {
            HP = 0;
            isAlive = false;
        }
    }

    public boolean isDead() {
        return !isAlive || HP == 0;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
        if (!isAlive) {
            this.HP = 0;
        }
    }

    public ItemState getState() {
        return this.state;
    }

    public void setState(ItemState state) {
        this.state = state;
    }

    // مدیریت موقعیت ثانویه/خارجی آیتم
    public Position getPosition() {
        return this.externalPosition;
    }

    public void setPosition(Position target) {
        this.externalPosition = target;
    }

    // مدیریت سرعت برداری حرکت آیتم
    public Position getSpeed() {
        return this.speed;
    }

    public void setSpeed(Position speed) {
        this.speed = speed;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
        this.isAlive = HP > 0;
    }
}