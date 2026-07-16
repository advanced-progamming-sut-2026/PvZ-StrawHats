package model.pitches;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.zombie_pushing_item.PushableStructure;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final int row;
    private final int col;

    private Plant plant;
    private Obstacle obstacle;
    private PushableStructure structure;
    private final List<Zombie> zombies = new ArrayList<>();

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public Plant getPlant() { return plant; }
    public void setPlant(Plant plant) { this.plant = plant; }
    public boolean hasPlant() { return plant != null && plant.isAlive(); }

    public Obstacle getObstacle() { return obstacle; }
    public void setObstacle(Obstacle obstacle) { this.obstacle = obstacle; }

    public PushableStructure getStructure() { return structure; }
    public void setStructure(PushableStructure structure) { this.structure = structure; }

    public List<Zombie> getZombies() { return zombies; }
    public void addZombie(Zombie zombie) { if (zombie != null) zombies.add(zombie); }
    public void removeZombie(Zombie zombie) { zombies.remove(zombie); }
    public void clearZombies() { zombies.clear(); }
}