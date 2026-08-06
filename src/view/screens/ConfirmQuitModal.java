package view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import view.general_screens.Modal;

/** "Are you sure?" popup for Signup's Quit button, which otherwise calls System.exit(0) with no warning (phase-1 behavior). */
class ConfirmQuitModal extends Modal {

    ConfirmQuitModal(Runnable onConfirm) {
        pad(24);
        add(new Label("Quit Plants vs. Zombies?", skin, "title")).colspan(2).padBottom(12).row();
        add(new Label("Any unsaved progress will be lost.", skin, "muted")).colspan(2).padBottom(16).row();

        TextButton cancel = new TextButton("Cancel", skin, "secondary");
        TextButton quit = new TextButton("Quit", skin);

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onConfirm.run();
            }
        });

        add(cancel).width(160).padRight(8);
        add(quit).width(160);
    }
}
