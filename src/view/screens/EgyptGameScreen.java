package view.screens;

import model.utils.GameSession;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.GameScreen;

/**
 * Ancient Egypt gameplay entry point.  The actual gameplay machinery lives in
 * GameScreen so the model/input/rendering path is identical to the other
 * seasons; this class only selects the Egypt map/visual context.
 */
public class EgyptGameScreen extends GameScreen {
    private static final String EGYPT_BACKGROUND = "images/chapters/egypt/egypt_gameplay/map.png";

    @Override
    protected String getGameplayBackgroundPath() {
        return "assets/images/chapters/egypt/egypt_gameplay/map.png";
    }

    @Override
    public void show() {
        super.show();
        AudioManager.get().playMusic(AudioEnum.EGYPT_MUSIC, true);
        GameSession session = GameSession.peekInstance();
        if (session != null && session.getLevel() != null) {
            // The four current Egypt stages are represented by the normal
            // gameplay renderer plus their model-defined special behaviour.
            // No separate zombie-pool HUD is added here.
        }
    }
}
