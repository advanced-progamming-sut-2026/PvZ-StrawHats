package model.collections.armour;

public abstract class Armour {
    private int HP;
    private ArmourStage stage;

    protected Armour(int HP) {
        this.HP = HP;
        stage = ArmourStage.INTACT;
    }

    public abstract void reduceHP();

    public abstract void changeState(); //by checking HP


    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public ArmourStage getStage() {
        return stage;
    }

    public void setStage(ArmourStage stage) {
        this.stage = stage;
    }

}
