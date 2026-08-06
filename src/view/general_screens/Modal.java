package view.general_screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import controller.assets.GameAssetManager;
import controller.assets.ScreenManager;

public class Modal extends Table {
    protected Skin skin;
    private final Table wrapperTable;

    public Modal() {
        skin = GameAssetManager.get().getSkin();
        this.setFillParent(true);
        wrapperTable = new Table();
        wrapperTable.setFillParent(true);

        Table content = new Table();
        content.setBackground(skin.getDrawable("modal-background"));
        content.pad(30);

        wrapperTable.center();
        wrapperTable.add(content).width(500).height(300);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == Modal.this) hide();
            }
        });

        this.add(wrapperTable);
    }

    public void show() {
        ScreenManager.getScreen().getModalStack().add(wrapperTable);
    }

    public void hide() {
        wrapperTable.remove();
    }
}