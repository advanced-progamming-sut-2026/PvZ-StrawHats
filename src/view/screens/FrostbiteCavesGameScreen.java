package view.screens;

import model.utils.GameSession;
import view.general_screens.GameScreen;

public class FrostbiteCavesGameScreen extends GameScreen {
    @Override
    protected String getGameplayBackgroundPath() {
        return "assets/images/chapters/frostbite_cave/gameplay/texture.png";
    }

    @Override
    public void show() {
        super.show();
        GameSession session = GameSession.peekInstance();
        if (session != null && session.getLevel() != null) {
            // The four current Egypt stages are represented by the normal
            // gameplay renderer plus their model-defined special behaviour.
            // No separate zombie-pool HUD is added here.
        }
    }
}
