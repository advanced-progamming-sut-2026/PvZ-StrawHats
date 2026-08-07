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

import java.util.Map;
import java.util.WeakHashMap;

public final class Toast extends Table {

    private static final float DEFAULT_DURATION = 3f;
    private static final float FADE_TIME = 0.4f;

    // Tracks the one currently-visible toast per stage, so a new message can replace it
    // instantly instead of being silently dropped while the old one is still fading.
    private static final Map<Stage, Toast> activeByStage = new WeakHashMap<>();

    private Toast(String message, float duration, Stage stage) {
        super();
        pad(10, 16, 10, 16);

        Label label = new Label(message, GameAssetManager.get().getSkin(), "main");
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
                Actions.run(() -> activeByStage.remove(stage, this))
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
        if (stage == null || message == null) {
            return;
        }
        Toast previous = activeByStage.remove(stage);
        if (previous != null) {
            previous.remove();
        }
        Toast toast = new Toast("\u2726 " + message, DEFAULT_DURATION, stage);
        toast.pack();
        toast.setPosition(stage.getWidth() - toast.getWidth() - 24f, 24f);
        stage.addActor(toast);
        activeByStage.put(stage, toast);
    }
}