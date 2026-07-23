package model.match.mini_games;

import model.match.Match;
import model.user_data.User;
import model.utils.GameSession;

public abstract class MiniGameMode extends Match {
    /** 1, 2, or 3 — each minigame has three levels of increasing difficulty per spec. */
    protected int difficulty = 1;

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = Math.max(1, Math.min(3, difficulty)); }

    public String getGameMode() { return "Mini-game"; }

    public String getStageDetails() {
        return "Game mode: " + getGameMode() + " | Level: " + getDifficulty();
    }

    protected void configureSession(GameSession session) {
        int playerDifficulty = 3;
        if (User.currentUser != null && User.currentUser.userState != null) {
            playerDifficulty = User.currentUser.userState.difficultyLevel;
        }
        session.setDifficultyLevel(playerDifficulty);
    }
}
