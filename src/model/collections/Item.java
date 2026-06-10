package model.collections;

import model.match_mechanisms.vector.Position;

public  abstract class Item {

    public Position position;

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    private int HP;
    public void takeDamage(){};
}
