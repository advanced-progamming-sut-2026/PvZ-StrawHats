package view.screens;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import model.match.main.levels.Level;
import model.utils.GameSession;
import view.general_screens.GameScreen;

/**
 * Dedicated Ancient Egypt gameplay screen.
 *
 * GameScreen owns the simulation, board and special-level rendering. This class
 * supplies the Egypt-specific presentation layer and is selected by ScreenManager
 * only for Egypt matches.
 */
public class EgyptGameScreen extends GameScreen {

    private Table egyptBanner;

    @Override
    public void show() {
        super.show();

        egyptBanner = new Table();
        egyptBanner.setTouchable(Touchable.disabled);
        egyptBanner.setBackground(skin.getDrawable("card-background"));

        Level level = GameSession.peekInstance() == null
                ? null : GameSession.peekInstance().getLevel();

        String title = level == null ? "ANCIENT EGYPT" : level.getName();
        Label label = new Label("☥  ANCIENT EGYPT  •  " + title + "  ☥", skin, "main");
        label.setAlignment(com.badlogic.gdx.utils.Align.center);

        egyptBanner.add(label).padLeft(16).padRight(16).padTop(5).padBottom(5);
        rootStack.add(egyptBanner);

        positionBanner();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        positionBanner();
    }

    private void positionBanner() {
        if (egyptBanner == null) return;
        egyptBanner.setSize(Math.min(760f, stage.getViewport().getWorldWidth() * 0.60f), 42f);
        egyptBanner.setPosition(
                (stage.getViewport().getWorldWidth() - egyptBanner.getWidth()) / 2f,
                stage.getViewport().getWorldHeight() - 52f);
    }

    @Override
    public void dispose() {
        egyptBanner = null;
        super.dispose();
    }
}
