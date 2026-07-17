package model.match.mini_games;

import model.match.Match;

public abstract class MiniGameMode extends Match {
    /** 1, 2, or 3 — each minigame has three levels of increasing difficulty per spec. */
    protected int difficulty = 1;

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = Math.max(1, Math.min(3, difficulty)); }
}
