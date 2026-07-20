package model.match.main.levels.special_levels;

import model.match.main.levels.Level;
import model.utils.GameSession;
import view.GeneralPrinter;

public class IntroductionLevel extends Level {
    @Override
    public void initSpecial(GameSession session) {
        introductionHandle();
    }

    public void introductionHandle() {
        GeneralPrinter.print("Welcome to your lawn! This level will walk you through the basics.");
    }
}
