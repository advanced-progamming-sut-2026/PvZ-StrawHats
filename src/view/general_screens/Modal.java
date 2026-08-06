package view.general_screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import controller.assets.GameAssetManager;
import controller.assets.ScreenManager;


/** for popups (pause menu, purchase confirmation,
 * Clicking outside the modal's own content closes it; clicking inside doesn't do that bro. */
public class Modal extends Table {
    protected Skin skin;
    private final Table wrapperTable;

    public Modal() {
        skin = GameAssetManager.get().getSkin();
        wrapperTable = new Table();
        wrapperTable.setTouchable(Touchable.enabled);
        setTouchable(Touchable.enabled); // avoid getting confused with the wrapper's clicked()
        wrapperTable.add(this);

        wrapperTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == wrapperTable) {
                    hide(); // click landed outside the modal's own content
                }
            }
        });
    }

    public void show() {
        ScreenManager.getScreen().getModalStack().add(wrapperTable);
    }

    public void hide() {
        wrapperTable.remove();
    }
}
