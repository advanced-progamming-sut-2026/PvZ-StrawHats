package view.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import model.user_data.User;
import model.utils.GameSettings;
import view.GeneralPrinter;

public class SettingsScreen extends AuthScreen {

    private static final int MIN_DIFFICULTY = 1;
    private static final int MAX_DIFFICULTY = 5;
    private static final int MIN_GAME_SPEED = 1;
    private static final int MAX_GAME_SPEED = 3;

    @Override
    public void show() {
        setBackground("assets/images/backg/sddefault.jpg");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(40).defaults().pad(8);
        addTitleImage(card);
        card.add(new Label("Settings", skin, "title")).colspan(2).padBottom(18).row();

        card.add(new Label("Difficulty", skin, "main")).left();
        card.add(difficultyRow()).left().row();

        card.add(new Label("Game speed", skin, "main")).left();
        card.add(gameSpeedRow()).left().row();

        card.add(gridToggle()).colspan(2).left().padTop(4).row();
        card.add(debugToggle()).colspan(2).left().padTop(4).row();

        card.add(secondaryButton("Back", () -> runCommand("menu exit")))
                .colspan(2).padTop(16).width(360).row();

        rootTable.add(card);
    }

    private Table difficultyRow() {
        Table row = new Table();
        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        int current = User.currentUser.userState.difficultyLevel;

        for (int level = MIN_DIFFICULTY; level <= MAX_DIFFICULTY; level++) {
            TextButton button = new TextButton(String.valueOf(level), skin, "gender-button");
            button.setChecked(level == current);
            int chosenLevel = level;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (chosenLevel != User.currentUser.userState.difficultyLevel) {
                        runCommand("menu settings change-difficulty -l " + chosenLevel);
                    }
                }
            });
            group.add(button);
            row.add(button).pad(4).width(56);
        }
        return row;
    }

    private Table gameSpeedRow() {
        Table row = new Table();
        ButtonGroup<TextButton> group = new ButtonGroup<>();
        group.setMinCheckCount(1);
        group.setMaxCheckCount(1);
        int current = GameSettings.get().getGameSpeed();

        for (int speed = MIN_GAME_SPEED; speed <= MAX_GAME_SPEED; speed++) {
            TextButton button = new TextButton(speed + "x", skin, "gender-button");
            button.setChecked(speed == current);
            int chosenSpeed = speed;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameSettings.get().setGameSpeed(chosenSpeed);
                    GameSettings.get().save();
                    GeneralPrinter.print("Game speed set to " + chosenSpeed + "x.");
                }
            });
            group.add(button);
            row.add(button).pad(4).width(70);
        }
        return row;
    }

    private CheckBox gridToggle() {
        CheckBox showGrid = new CheckBox(" Show lawn grid lines", skin, "main");
        showGrid.setChecked(GameSettings.get().isShowGrid());
        showGrid.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings.get().setShowGrid(showGrid.isChecked());
                GameSettings.get().save();
                GeneralPrinter.print("Lawn grid " + (showGrid.isChecked() ? "shown." : "hidden."));
            }
        });
        return showGrid;
    }

    private CheckBox debugToggle() {
        CheckBox debugMode = new CheckBox(" Debug mode (cheat buttons in-game)", skin, "main");
        debugMode.setChecked(GameSettings.get().isDebugMode());
        debugMode.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings.get().setDebugMode(debugMode.isChecked());
                GameSettings.get().save();
                GeneralPrinter.print("Debug mode " + (debugMode.isChecked() ? "enabled." : "disabled."));
            }
        });
        return debugMode;
    }
}
