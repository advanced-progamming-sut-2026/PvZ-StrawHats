package view.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import controller.assets.GameAssetManager;
import controller.assets.ScreenManager;
import model.App;
import model.game_exceptions.GameException;
import view.GeneralPrinter;
import view.general_screens.BaseScreen;
import view.general_screens.Toast;

import java.util.function.Consumer;

/**
 * Shared plumbing for the graphical authentication screens (Login/Signup).
 * {@link #runCommand(String)} dispatches straight to {@code App.currentMenu.handleCommand(String)}
 * - the exact same controller entry point the phase-1 console {@code AppView} used - so every
 * validation rule still lives only in {@code controller.menus.authentication.*}. Concrete
 * screens here only decide which fields/buttons to show for the current step.
 */
public abstract class AuthScreen extends BaseScreen {

    private final Consumer<String> printerListener = this::onMessage;

    @Override
    public void setSkin() {
        this.skin = GameAssetManager.get().getSkin();
    }

    @Override
    public void show() {
        super.show();
        GeneralPrinter.addListener(printerListener);
    }

    @Override
    public void hide() {
        GeneralPrinter.removeListener(printerListener);
        super.hide();
    }

    @Override
    public void initParticles() {
        // No decorative art shipped yet. ParticleCreator already no-ops safely if the paths
        // it's given don't resolve, so there's nothing to wire up until an artist adds some.
    }

    private void onMessage(String message) {
        if (stage != null) {
            Toast.show(stage, message);
        }
    }

    /**
     * Runs a phase-1-style command string (e.g. {@code "login -u bob -p Secret1!"}) through the
     * real controller, keeps {@link ScreenManager} in sync with whatever
     * {@code App.currentMenu} became, and - only if we're still the screen showing - lets the
     * subclass react via {@link #onAfterCommand()}.
     */
    protected void runCommand(String command) {
        try {
            App.currentMenu.handleCommand(command);
        } catch (GameException e) {
            GeneralPrinter.print("[Error] " + e.getMessage());
        } catch (Exception e) {
            GeneralPrinter.print(String.valueOf(e.getMessage()));
        }
        ScreenManager.syncWithCurrentMenu();
        if (ScreenManager.getScreen() == this) {
            onAfterCommand();
        }
    }

    /** Called after every runCommand() that didn't switch to a different screen; override to move between sub-steps. */
    protected void onAfterCommand() {
    }

    // ---- widget helpers (all styled via the shared skin - see GameAssetManager.buildFallbackSkin) ----

    protected TextField field(boolean masked) {
        TextField textField = new TextField("", skin);
        textField.setTextFieldFilter((tf, c) -> c != ' ');
        if (masked) {
            textField.setPasswordCharacter('*');
            textField.setPasswordMode(true);
        }
        return textField;
    }

    protected void addRow(Table card, String labelText, Actor field) {
        card.add(new Label(labelText, skin)).left();
        card.add(field).width(300).left().row();
    }

    protected TextButton primaryButton(String text, Runnable action) {
        return styledButton(new TextButton(text, skin), action);
    }

    protected TextButton secondaryButton(String text, Runnable action) {
        return styledButton(new TextButton(text, skin, "secondary"), action);
    }

    private TextButton styledButton(TextButton button, Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }
}
