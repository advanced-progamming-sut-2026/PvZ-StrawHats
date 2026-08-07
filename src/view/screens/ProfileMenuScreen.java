package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import model.user_data.User;

public class ProfileMenuScreen extends AuthScreen {

    @Override
    public void show() {
        setBackground("assets/images/backg/profilemenu_background.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        User user = User.currentUser;
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(40).defaults().pad(6);
        addTitleImage(card);
        card.add(new Label("Profile", skin, "title")).colspan(2).padBottom(12).row();

        if (user == null) {
            card.add(new Label("No user is currently logged in.", skin, "muted")).colspan(2).row();
            rootTable.add(card);
            return;
        }

        String stats = "Games played: " + user.userState.gamesPlayed
                + "     High score: " + user.userState.highScore + "\n"
                + "Levels passed: " + user.userState.lastLevel
                + "     Coins: " + user.userState.coins
                + "     Diamonds: " + user.userState.diamonds;
        Label statsLabel = new Label(stats, skin, "muted");
        statsLabel.setWrap(true);
        card.add(statsLabel).colspan(2).width(460).padBottom(16).row();

        TextField username = field(false);
        username.setText(user.username);
        addEditableRow(card, "Username", username,
                () -> runCommand("menu profile change-username -u " + username.getText()));

        TextField nickname = field(false);
        nickname.setText(user.nickname);
        addEditableRow(card, "Nickname", nickname,
                () -> runCommand("menu profile change-nickname -u " + nickname.getText()));

        TextField email = field(false);
        email.setText(user.email);
        addEditableRow(card, "Email", email,
                () -> runCommand("menu profile change-email -e " + email.getText()));

        card.add(new Label("Change password", skin, "title")).colspan(2).padTop(18).padBottom(6).row();
        TextField oldPassword = field(true);
        TextField newPassword = field(true);
        addRow(card, "Current password", oldPassword);
        addRow(card, "New password", newPassword);
        card.add(primaryButton("Update password", () ->
                runCommand("menu profile change-password -p " + newPassword.getText()
                        + " -o " + oldPassword.getText())))
                .colspan(2).padTop(6).width(360).row();

        card.add(secondaryButton("Back to Main Menu", () -> runCommand("menu exit")))
                .colspan(2).padTop(16).width(360).row();

        rootTable.add(card);
    }

    private void addEditableRow(Table card, String labelText, TextField field, Runnable onSave) {
        card.add(new Label(labelText, skin, "main")).left();
        Table row = new Table();
        row.add(field).width(250).left();
        row.add(secondaryButton("Save", onSave)).padLeft(8);
        card.add(row).left().row();
    }

    @Override
    protected void onAfterCommand() {
        build();
    }
}
