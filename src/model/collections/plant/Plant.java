package model.collections.plant;

import model.collections.Item;
import model.match_mechanisms.Attack;
import model.match_mechanisms.Pluck;

public abstract class Plant implements Pluck, Attack {
    private String name;

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
