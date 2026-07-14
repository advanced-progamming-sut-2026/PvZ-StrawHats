package model.collections.armour;

public abstract class Armour {
    private int HP;
    private ArmourStage stage;

    protected Armour(int HP) {
        this.HP = HP;
        this.stage = ArmourStage.INTACT;
    }

    public abstract int absorbDamage(int damage);

    public void changeState() {
        if (HP <= 0) {
            stage = ArmourStage.BROKEN;
        }
    }

    public int getHP() { return HP; }
    public void setHP(int HP) {
        this.HP = HP;
        changeState();
    }


    public ArmourStage getStage() { return stage; }
    public void setStage(ArmourStage stage) { this.stage = stage; }
}