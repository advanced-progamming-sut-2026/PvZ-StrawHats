package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import model.user_data.User;

public class ProfileMenuScreen extends AuthScreen {

    @Override
    public void show() {
        setBackground("assets/images/backg/PVZIOS_newtitle.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        User user = User.currentUser;
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(CARD_PAD).defaults().space(SPACE_SM);
        addTitleImage(card);
        card.add(new Label("Profile", skin, "title")).colspan(2).padBottom(SPACE_SM).row();

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
        card.add(statsLabel).colspan(2).width(400).padBottom(SPACE_LG).row();

        // 3 editable rows + a password section is too tall for one screen at once, so it
        // scrolls inside a fixed-height window; only "Back to Main Menu" stays fixed below it.
        Table fields = new Table();
        fields.defaults().space(SPACE_SM);

        TextField username = field(false);
        username.setText(user.username);
        addEditableRow(fields, "Username", username,
                () -> runCommand("menu profile change-username -u " + username.getText()));

        TextField nickname = field(false);
        nickname.setText(user.nickname);
        addEditableRow(fields, "Nickname", nickname,
                () -> runCommand("menu profile change-nickname -u " + nickname.getText()));

        TextField email = field(false);
        email.setText(user.email);
        addEditableRow(fields, "Email", email,
                () -> runCommand("menu profile change-email -e " + email.getText()));

        fields.add(new Label("Change password", skin, "title"))
                .colspan(2).padTop(SPACE_XL).padBottom(SPACE_SM).left().row();
        TextField oldPassword = field(true);
        TextField newPassword = field(true);
        addRow(fields, "Current password", oldPassword);
        addRow(fields, "New password", newPassword);
        fields.add(primaryButton("Update password", () ->
                runCommand("menu profile change-password -p " + newPassword.getText()
                        + " -o " + oldPassword.getText())))
                .colspan(2).padTop(SPACE_MD).width(BUTTON_WIDTH).row();

        card.add(scrollable(fields)).colspan(2).width(420).height(330).padBottom(SPACE_MD).row();

        card.add(secondaryButton("Back to Main Menu", () -> runCommand("menu exit")))
                .colspan(2).width(BUTTON_WIDTH).row();

        rootTable.add(card);
    }

    private void addEditableRow(Table table, String labelText, TextField textField, Runnable onSave) {
        table.add(new Label(labelText, skin, "main")).width(LABEL_WIDTH).left();
        Table row = new Table();
        row.add(textField).width(160).left();
        row.add(secondaryButton("Save", onSave)).width(70).padLeft(SPACE_XS);
        table.add(row).left().row();
    }

    @Override
    protected void onAfterCommand() {
        // Same menu, new values (or a rejected change) - rebuild so fields reflect User.currentUser.
        build();
    }
}
