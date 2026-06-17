package model.collections;

import model.match_mechanisms.vector.Position;

public  abstract class Item {
    public Position position;

    private int HP;

    public void takeDamage(int damage){
        HP -= damage;
        HP = (HP < 0) ? 0 : HP;
    };

    public boolean isDead() {
        if (HP == 0)
            return true;
        else
            return false;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }
}
