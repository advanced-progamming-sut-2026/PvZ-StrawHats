package view.general_screens;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import controller.assets.GameAssetManager;
import controller.ScreenManager;

public class Modal extends Table {

    private static final float MIN_WIDTH = 380f;
    private static final float MAX_WIDTH = 620f;
    private static final float MAX_HEIGHT = 560f;

    protected final Skin skin;
    protected final Table content;
    private final Table wrapperTable;

    public Modal() {
        BaseScreen activeScreen = ScreenManager.getScreen();
        skin = (activeScreen != null && activeScreen.skin != null)
                ? activeScreen.skin
                : GameAssetManager.get().getSkin();

        this.setFillParent(true);

        Image scrim = new Image(scrimDrawable());

        content = new Table();
        content.setBackground(skin.getDrawable("modal-background"));
        content.pad(28).defaults().pad(6);

        wrapperTable = new Table();
        wrapperTable.center();
        wrapperTable.add(content).minWidth(MIN_WIDTH).maxWidth(MAX_WIDTH).maxHeight(MAX_HEIGHT);

        Stack stack = new Stack();
        stack.add(scrim);
        stack.add(wrapperTable);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == Modal.this) {
                    hide();
                }
            }
        });

        this.add(stack).grow();
    }

    private static TextureRegionDrawable scrimDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.55f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    public void show() {
        ScreenManager.getScreen().getModalStack().add(wrapperTable);
    }

    public void hide() {
        wrapperTable.remove();
    }
}