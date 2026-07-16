package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.utils.GameSession;

public class IntroductionLevel extends Level {
    @Override
    public void initSpecial(GameSession session) {
        introductionHandle();
    }

    public void introductionHandle() {
        System.out.println("Welcome to your lawn! This level will walk you through the basics.");
    }
}
