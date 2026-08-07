package view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import view.general_screens.Modal;

class ConfirmModal extends Modal {

    ConfirmModal(String title, String message, String confirmLabel, Runnable onConfirm) {
        pad(24);
        add(new Label(title, skin, "title")).colspan(2).padBottom(10).row();
        Label messageLabel = new Label(message, skin, "muted");
        messageLabel.setWrap(true);
        add(messageLabel).colspan(2).width(400).padBottom(18).row();

        TextButton cancel = new TextButton("Cancel", skin, "secondary");
        TextButton confirm = new TextButton(confirmLabel, skin, "default");

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        confirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                onConfirm.run();
            }
        });

        add(cancel).width(160).padRight(8);
        add(confirm).width(160);
    }
}
