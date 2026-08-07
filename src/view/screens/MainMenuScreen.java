package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.NewsManager;
import model.user_data.User;

public class MainMenuScreen extends AuthScreen {

    private ScrollPane carouselPane;

    @Override
    public void show() {
        setBackground("assets/images/backg/mainmenu_background.png");
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();
        rootTable.setFillParent(true);

        Table topBar = new Table();

        Table topLeft = new Table();
        // Asset Path: Profile
        topLeft.add(createIconButton("assets/images/ui/reward4_bg.png", 72, 72, () -> runCommand("menu enter profile"))).padRight(16);

        // Asset Path: Settings
        topLeft.add(createIconButton("assets/images/ui/buttons_hud_settings_selected.png", 54, 54, () -> runCommand("menu enter settings"))).padRight(16);

        // Asset Path: News
        topLeft.add(createIconButtonWithLabel("assets/images/ui/buttons_hud_news_selected copy 2.png", 54, 54, "News", () -> runCommand("menu enter news")));

        Table topRight = new Table();
        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;

        // Asset Path: Coins
        topRight.add(createResourceWidget("assets/images/ui/buttons_coin_buy_normal.png", String.valueOf(coins))).padRight(15);
        // Asset Path: Diamonds
        topRight.add(createResourceWidget("assets/images/ui/buttons_premium_normal.png", String.valueOf(diamonds)));

        topBar.add(topLeft).left().expandX();
        topBar.add(topRight).right();

        Table centerTable = new Table();
        Table carouselContent = new Table();

        // Asset Paths: Game, Travel Log, Network Banners
        Actor gameBtn1 = createBannerCard("assets/images/ui/calendar_card_7day_tombtangled.png", "Game", () -> runCommand("menu enter game"));
        Actor travelBtn1 = createBannerCard("assets/images/ui/calendar_card_7day_bigwavebeach.png", "Travel Log", () -> runCommand("menu enter travellog"));
        Actor networkBtn1 = createBannerCard("assets/images/ui/calendar_card_7day_lunar_new_year.png", "Network", () -> runCommand("menu enter network"));

        Actor gameBtn2 = createBannerCard("assets/images/ui/calendar_card_7day_tombtangled.png", "Game", () -> runCommand("menu enter game"));
        Actor travelBtn2 = createBannerCard("assets/images/ui/calendar_card_7day_bigwavebeach.png", "Travel Log", () -> runCommand("menu enter travellog"));
        Actor networkBtn2 = createBannerCard("assets/images/ui/calendar_card_7day_lunar_new_year.png", "Network", () -> runCommand("menu enter network"));

        Actor gameBtn3 = createBannerCard("assets/images/ui/calendar_card_7day_tombtangled.png", "Game", () -> runCommand("menu enter game"));
        Actor travelBtn3 = createBannerCard("assets/images/ui/calendar_card_7day_bigwavebeach.png", "Travel Log", () -> runCommand("menu enter travellog"));
        Actor networkBtn3 = createBannerCard("assets/images/ui/calendar_card_7day_lunar_new_year.png", "Network", () -> runCommand("menu enter network"));

        carouselContent.add(gameBtn1).size(420, 260).pad(20);
        carouselContent.add(travelBtn1).size(420, 260).pad(20);
        carouselContent.add(networkBtn1).size(420, 260).pad(20);

        carouselContent.add(gameBtn2).size(420, 260).pad(20);
        carouselContent.add(travelBtn2).size(420, 260).pad(20);
        carouselContent.add(networkBtn2).size(420, 260).pad(20);

        carouselContent.add(gameBtn3).size(420, 260).pad(20);
        carouselContent.add(travelBtn3).size(420, 260).pad(20);
        carouselContent.add(networkBtn3).size(420, 260).pad(20);

        carouselPane = new ScrollPane(carouselContent);
        carouselPane.setOverscroll(false, false);
        carouselPane.setFlickScroll(true);
        carouselPane.setScrollingDisabled(false, true);

        centerTable.add(carouselPane).width(1150).height(300);

        Table bottomBar = new Table();
        TextButton logoutBtn = secondaryButton("Log out", this::confirmLogout);
        bottomBar.add(logoutBtn).width(160).height(45).left().expandX();

        rootTable.add(topBar).fillX().padTop(5).padLeft(15).padRight(15).row();
        rootTable.add(centerTable).expand().center().row();
        rootTable.add(bottomBar).fillX().padBottom(10).padLeft(15).row();

        Gdx.app.postRunnable(() -> {
            if (carouselPane != null) {
                float unitWidth = (420 + 40) * 3;
                carouselPane.setScrollX(unitWidth);
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (carouselPane != null) {
            float unitWidth = (420 + 40) * 3;
            float currentX = carouselPane.getScrollX();
            if (currentX < unitWidth * 0.5f) {
                carouselPane.setScrollX(currentX + unitWidth);
            } else if (currentX > unitWidth * 2.5f) {
                carouselPane.setScrollX(currentX - unitWidth);
            }
        }
    }

    private Actor createBannerCard(String path, String title, Runnable action) {
        Stack stack = new Stack();
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(drawable);

        Label label = new Label(title, skin, "title");
        label.setAlignment(Align.center);

        Table textTable = new Table();
        textTable.add(label).expand().bottom().padBottom(20);

        stack.add(button);
        stack.add(textTable);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        return stack;
    }

    private ImageButton createIconButton(String path, float width, float height, Runnable action) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        ImageButton button = new ImageButton(drawable);
        button.getImageCell().size(width, height);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    private Actor createIconButtonWithLabel(String path, float width, float height, String text, Runnable action) {
        Table container = new Table();
        ImageButton btn = createIconButton(path, width, height, action);
        Label label = new Label(text, skin, "title");
        label.setAlignment(Align.center);

        container.add(btn).row();
        container.add(label).padTop(2);

        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return container;
    }

    private Table createResourceWidget(String iconPath, String value) {
        Stack stack = new Stack();
        Texture texture = new Texture(Gdx.files.internal(iconPath));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Table bgTable = new Table();
        bgTable.setBackground(new TextureRegionDrawable(texture));

        Label label = new Label(value, skin, "title");
        label.setAlignment(Align.center);

        Table textTable = new Table();
        textTable.add(label).center().expand();

        stack.add(bgTable);
        stack.add(textTable);

        Table outer = new Table();
        outer.add(stack).size(130, 42);
        return outer;
    }

    private void confirmLogout() {
        new ConfirmModal("Log out?", "You'll need your password to sign back in.", "Log out",
                () -> runCommand("menu logout")).show();
    }
}