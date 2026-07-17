package model.match.mini_games.izombie;

import model.match_mechanisms.vector.Position;

public class Brain {
    private final Position position;
    private boolean eaten = false;

    public Brain(Position position) {
        this.position = position;
    }

    public Position getPosition() { return position; }
    public boolean isEaten() { return eaten; }
    public void markEaten() { eaten = true; }
}
