package view.general_screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import controller.assets.GameAssetManager;

public final class Toast extends Table {

    private static final float DEFAULT_DURATION = 3f;
    private static final float FADE_TIME = 0.4f;
    private static volatile boolean busy = false;

    private Toast(String message, float duration) {
        super();
        pad(10, 16, 10, 16);

        Label label = new Label(message, GameAssetManager.get().getSkin(), "default");
        label.setColor(Color.WHITE);
        label.setWrap(true);
        add(label).width(BaseScreen.SCREEN_WIDTH / 4f);

        setBackground(backgroundDrawable());

        getColor().a = 0f;
        addAction(Actions.sequence(
                Actions.fadeIn(FADE_TIME),
                Actions.delay(duration),
                Actions.fadeOut(FADE_TIME),
                Actions.removeActor(),
                Actions.run(() -> busy = false)
        ));
    }

    private static TextureRegionDrawable backgroundDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.75f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    public static void show(Stage stage, String message) {
        if (stage == null || message == null || busy) {
            return;
        }
        busy = true;
        Toast toast = new Toast("\u2726 " + message, DEFAULT_DURATION);
        toast.pack();
        toast.setPosition(stage.getWidth() - toast.getWidth() - 24f, 24f);
        stage.addActor(toast);
    }
}