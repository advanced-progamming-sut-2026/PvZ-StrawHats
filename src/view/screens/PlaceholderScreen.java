package view.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import controller.menus.Menu;

/**
 * Stand-in for every menu that doesn't have real graphics yet (everything past Login/Signup,
 * for now). Shows the same command reference {@code Menu.showMenu()} printed in the console,
 * plus a text field wired straight to {@code runCommand()} - so the app stays fully usable while
 * the rest of the screens get built one at a time, instead of getting stuck after login.
 */
public class PlaceholderScreen extends AuthScreen {

    private final Menu menu;

    public PlaceholderScreen(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void show() {
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        TextField commandField = field(false);
        commandField.setTextFieldFilter(null); // commands legitimately contain spaces, unlike the auth fields

        Label info = new Label(menu.showMenu(), skin, "muted");
        info.setWrap(true);

        Table card = new Table();
        card.pad(24).defaults().pad(6);
        card.add(new Label(menu.getName(), skin, "title")).colspan(2).padBottom(12).row();
        card.add(new Label("Graphics for this menu haven't been built yet - the same commands from phase 1 still work here.", skin))
                .colspan(2).width(480).padBottom(8).row();
        card.add(new ScrollPane(info)).colspan(2).width(480).height(220).padBottom(12).row();
        card.add(new Label("Command", skin)).left();
        card.add(commandField).width(300).row();
        card.add(primaryButton("Run", () -> runCommand(commandField.getText())))
                .colspan(2).padTop(12).width(220).row();

        rootTable.add(card);
    }
}
