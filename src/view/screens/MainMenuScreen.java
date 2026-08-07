package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import controller.NewsManager;
import model.user_data.User;

public class MainMenuScreen extends AuthScreen {

    @Override
    public void show() {
        setBackground("assets/images/backg/PVZIOS_newtitle.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        User user = User.currentUser;
        String nickname = user != null ? user.nickname : "Player";

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(CARD_PAD).defaults().space(SPACE_SM);
        addTitleImage(card);
        card.add(new Label("Welcome back, " + nickname + "!", skin, "title"))
                .colspan(2).padBottom(SPACE_XS).row();

        if (user != null) {
            String stats = "Coins: " + user.userState.coins + "     Diamonds: " + user.userState.diamonds;
            card.add(new Label(stats, skin, "muted")).colspan(2).padBottom(SPACE_LG).row();
        }

        String newsLabel = NewsManager.hasUnreadNews() ? "News  (unread)" : "News";
        float gridButtonWidth = (BUTTON_WIDTH - SPACE_SM) / 2f;

        card.add(primaryButton("Game", () -> runCommand("menu enter game"))).width(gridButtonWidth);
        card.add(primaryButton("Travel Log", () -> runCommand("menu enter travellog"))).width(gridButtonWidth).row();

        card.add(primaryButton("Collection", () -> runCommand("menu enter collection"))).width(gridButtonWidth);
        card.add(primaryButton(newsLabel, () -> runCommand("menu enter news"))).width(gridButtonWidth).row();

        card.add(secondaryButton("Profile", () -> runCommand("menu enter profile"))).width(gridButtonWidth);
        card.add(secondaryButton("Settings", () -> runCommand("menu enter settings"))).width(gridButtonWidth).row();

        card.add(secondaryButton("Network", () -> runCommand("menu enter network")))
                .colspan(2).padTop(SPACE_MD).width(BUTTON_WIDTH).row();

        card.add(secondaryButton("Log out", this::confirmLogout))
                .colspan(2).padTop(SPACE_XL).width(BUTTON_WIDTH).row();

        rootTable.add(card);
    }

    private void confirmLogout() {
        new ConfirmModal("Log out?", "You'll need your password to sign back in.", "Log out",
                () -> runCommand("menu logout")).show();
    }
}
