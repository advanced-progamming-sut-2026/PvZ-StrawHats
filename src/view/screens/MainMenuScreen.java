package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import controller.NewsManager;
import model.user_data.User;

public class MainMenuScreen extends AuthScreen {

    @Override
    public void show() {
        setBackground("assets/images/backg/mainmenu_background.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        User user = User.currentUser;
        String nickname = user != null ? user.nickname : "Player";

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(40).defaults().pad(6);
        addTitleImage(card);
        card.add(new Label("Welcome back, " + nickname + "!", skin, "title"))
                .colspan(2).padBottom(6).row();

        if (user != null) {
            String stats = "Coins: " + user.userState.coins + "     Diamonds: " + user.userState.diamonds;
            card.add(new Label(stats, skin, "muted")).colspan(2).padBottom(18).row();
        }

        String newsLabel = NewsManager.hasUnreadNews() ? "News  (unread)" : "News";

        card.add(primaryButton("Game", () -> runCommand("menu enter game"))).colspan(2).width(360).row();
        card.add(primaryButton("Travel Log", () -> runCommand("menu enter travellog"))).colspan(2).width(360).row();
        card.add(primaryButton("Collection", () -> runCommand("menu enter collection"))).colspan(2).width(360).row();
        card.add(primaryButton(newsLabel, () -> runCommand("menu enter news"))).colspan(2).width(360).row();

        card.add(secondaryButton("Profile", () -> runCommand("menu enter profile"))).colspan(2).padTop(10).width(360).row();
        card.add(secondaryButton("Settings", () -> runCommand("menu enter settings"))).colspan(2).width(360).row();
        card.add(secondaryButton("Network", () -> runCommand("menu enter network"))).colspan(2).width(360).row();

        card.add(secondaryButton("Log out", this::confirmLogout)).colspan(2).padTop(14).width(360).row();

        rootTable.add(card);
    }

    private void confirmLogout() {
        new ConfirmModal("Log out?", "You'll need your password to sign back in.", "Log out",
                () -> runCommand("menu logout")).show();
    }
}
