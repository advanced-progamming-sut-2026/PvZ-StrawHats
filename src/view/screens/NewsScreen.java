package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import model.news.News;
import model.user_data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphical version of the terminal News Menu. Keeps the exact same two actions
 * ("menu news show-unread" / "menu news show-all") behind buttons instead of typed
 * commands - both still run through NewsMenu, so marking-as-read and saving behave
 * identically to phase 1.
 */
public class NewsScreen extends AuthScreen {

    private List<String> resultLines = null;
    private String emptyMessage = null;

    @Override
    public void show() {
        setBackground("assets/images/backg/newsmenu_background.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(40).defaults().pad(6);
        addTitleImage(card);
        card.add(new Label("News", skin, "title")).colspan(2).padBottom(6).row();

        User user = User.currentUser;
        if (user == null) {
            card.add(new Label("No user is currently logged in.", skin, "muted")).colspan(2).row();
            rootTable.add(card);
            return;
        }

        String badge = user.userState.hasUnreadNews() ? "You have unread news." : "You're all caught up.";
        card.add(new Label(badge, skin, "muted")).colspan(2).padBottom(14).row();

        card.add(primaryButton("Show Unread", this::handleShowUnread)).width(220).padRight(6);
        card.add(secondaryButton("Show All", this::handleShowAll)).width(220).row();

        Table list = new Table();
        list.top();

        if (resultLines == null) {
            list.add(new Label("Pick an option above to view your news.", skin, "muted")).left().row();
        } else if (resultLines.isEmpty()) {
            list.add(new Label(emptyMessage, skin, "muted")).left().row();
        } else {
            for (String line : resultLines) {
                Label label = new Label(line, skin, line.startsWith("[NEW]") ? "main" : "muted");
                label.setWrap(true);
                list.add(label).width(460).left().padBottom(8).row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFadeScrollBars(false);
        card.add(scrollPane).colspan(2).width(480).height(240).padTop(14).row();

        card.add(secondaryButton("Back to Main Menu", () -> runCommand("menu exit")))
                .colspan(2).padTop(16).width(360).row();

        rootTable.add(card);
    }

    private void handleShowUnread() {
        List<String> lines = new ArrayList<>();
        for (News news : User.currentUser.userState.news) {
            if (!news.isRead()) {
                lines.add("[NEW] " + news.getText());
            }
        }
        resultLines = lines;
        emptyMessage = "No unread news.";
        runCommand("menu news show-unread");
    }

    private void handleShowAll() {
        List<String> lines = new ArrayList<>();
        for (News news : User.currentUser.userState.news) {
            String status = news.isRead() ? "[READ]" : "[NEW]";
            lines.add(status + " " + news.getText());
        }
        resultLines = lines;
        emptyMessage = "No news available.";
        runCommand("menu news show-all");
    }

    @Override
    protected void onAfterCommand() {
        build();
    }
}
