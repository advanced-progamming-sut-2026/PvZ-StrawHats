package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import model.user_data.User;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.UiScreen;

public class MainMenuScreen extends UiScreen {

    private ScrollPane carouselPane;

    @Override
    public void show() {
        setBackground("assets/images/backg/mainmenu_background.png");
        AudioManager.get().playMusic(AudioEnum.MENU_MUSIC, true);
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();
        rootTable.setFillParent(true);

        Table topBar = new Table();

        Table topLeft = new Table();
        topLeft.add(createProfileButton(() -> runCommand("menu enter profile"))).size(72, 72).padRight(16);

        topLeft.add(createIconButton("assets/images/ui/buttons_hud_settings_selected.png", 54, 54, () -> runCommand("menu enter settings"))).padRight(16);

        topLeft.add(createIconButtonWithLabel("assets/images/ui/buttons_hud_news_selected copy 2.png", 54, 54, "News", () -> runCommand("menu enter news")));

        Table topRight = new Table();
        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;

        topRight.add(createResourceWidget("assets/images/ui/buttons_coin_buy_normal.png", String.valueOf(coins))).padRight(15);
        topRight.add(createResourceWidget("assets/images/ui/buttons_premium_normal.png", String.valueOf(diamonds)));

        topBar.add(topLeft).left().expandX();
        topBar.add(topRight).right();

        Table centerTable = new Table();
        Table carouselContent = new Table();

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

    private Actor createProfileButton(Runnable action) {
        Stack stack = new Stack();

        Texture frameTex = loadTextureSafe("assets/images/ui/reward4_bg.png");
        Image frameImg = new Image(frameTex);

        User user = User.currentUser;
        String avatarPath = (user != null && user.profilePicture != null && !user.profilePicture.isEmpty())
                ? user.profilePicture
                : "assets/images/ui/avatar_luffy.png";

        Texture avatarTex = loadTextureSafe(avatarPath);
        Image avatarImg = new Image(avatarTex);

        Table avatarContainer = new Table();
        avatarContainer.add(avatarImg).size(48, 48);

        stack.add(frameImg);
        stack.add(avatarContainer);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        return stack;
    }

    private Actor createBannerCard(String path, String title, Runnable action) {
        Stack stack = new Stack();
        Texture texture = loadTextureSafe(path);
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
        Texture texture = loadTextureSafe(path);
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
        Texture texture = loadTextureSafe(iconPath);

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

    private Texture loadTextureSafe(String path) {
        if (path != null && !path.isEmpty() && Gdx.files.internal(path).exists()) {
            Texture tex = new Texture(Gdx.files.internal(path));
            tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return tex;
        }
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        Texture fallback = new Texture(pixmap);
        pixmap.dispose();
        return fallback;
    }

    private void confirmLogout() {
        new ConfirmModal("Log out?", "You'll need your password to sign back in.", "Log out",
                () -> runCommand("menu logout")).show();
    }
}