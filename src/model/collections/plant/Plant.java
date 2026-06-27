package model.collections.plant;

import model.collections.Item;
import model.match_mechanisms.Attack;
import model.match_mechanisms.Pluck;
import model.match_mechanisms.vector.Position;

public abstract class Plant extends Item implements Pluck, Attack  {
    private String name;

    public Plant(Position position, int HP) {
        super(position, HP);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof Plant plant))
            return false;

        return this.getName().equals(plant.getName());
    }
}
