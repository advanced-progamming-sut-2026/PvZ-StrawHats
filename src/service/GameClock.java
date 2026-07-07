package service;

import model.collections.Item;
import model.match_mechanisms.Time;

import java.util.ArrayList;
import java.util.List;

public class GameClock {
    public static final double SECONDS_PER_TICK = 0.1;

    private int tick = 0;
    private final List<Time> entities = new ArrayList<>();

    public void addEntity(Item entity) {
        Item.add(entity);
    }

    public void removeEntity(Item entity) {
        entities.remove(entity);
    }

    public void tick() {
        tick++;
        entities.forEach(Tickable::tick);
    }

    public int getTicks() {
        return tick;
    }

    public double getElapsedSeconds() {
        return tick * SECONDS_PER_TICK;
    }

    public void reset() {
        tick = 0;
        entities.clear();
    }
}
