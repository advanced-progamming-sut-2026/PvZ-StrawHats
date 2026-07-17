package model.match.mini_games.vasebreaker.vase;

import model.match.mini_games.vasebreaker.Vasebreaker;
import model.match_mechanisms.vector.Position;
import model.utils.GameSession;

public abstract class Vase {
    protected final Position position;
    private boolean broken = false;

    protected Vase(Position position) {
        this.position = position;
    }

    public Position getPosition() { return position; }
    public boolean isBroken() { return broken; }

    /**
     * Breaks the vase (no-op if already broken) and releases whatever was inside it.
     */
    public final void breakVase(GameSession session, Vasebreaker minigame) {
        if (broken) return;
        broken = true;
        onBreak(session, minigame);
    }

    protected abstract void onBreak(GameSession session, Vasebreaker minigame);
}
