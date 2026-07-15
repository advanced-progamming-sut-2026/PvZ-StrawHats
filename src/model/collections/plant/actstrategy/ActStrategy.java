package model.collections.plant.actstrategy;

import model.collections.plant.Plant;
import model.utils.GameSession;

public interface ActStrategy {
    public void act(Plant user, GameSession session);
}