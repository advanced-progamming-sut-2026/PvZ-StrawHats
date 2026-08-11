package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import model.match.main.levels.Level;
import model.user_data.User;
import model.utils.LevelLoader;
import model.utils.LevelProgression;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.UiScreen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameMenuScreen extends UiScreen {

    private static final String[] CHAPTERS = {
            "Egypt", "Frostbite Caves", "Big Wave Beach", "Dark Ages"
    };
    private static final String[] CHAPTER_ART = {
            "assets/images/chapters/egypt.png",
            "assets/images/chapters/iceage.png",
            "assets/images/chapters/beach.png",
            "assets/images/chapters/dark.png"
    };

    private static final float CARD_WIDTH = 280f;
    private static final float CARD_HEIGHT = 230f;
    private static final float CHAPTER_CARD_PAD = 20f;
    private static final float CAROUSEL_VIEWPORT_WIDTH = 1270f;
    private static final float UNIT_WIDTH = (CARD_WIDTH + CHAPTER_CARD_PAD * 2) * CHAPTERS.length;

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

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();

        Table centerTable = new Table();
        Label label = new Label("Choose a Chapter", skin, "title");
        label.setFontScale(1.9f);
        centerTable.add(label).padTop(-210).padBottom(19).row();
        centerTable.add(buildChapterCarousel());

        rootTable.add(centerTable).expand().center().row();
    }

    private Table buildChapterCarousel() {
        Map<String, Boolean> unlockStatus = computeChapterUnlockStatus();

        Table carouselContent = new Table();
        for (int lap = 0; lap < 3; lap++) {
            for (int i = 0; i < CHAPTERS.length; i++) {
                String chapter = CHAPTERS[i];
                carouselContent.add(createChapterCard(chapter, CHAPTER_ART[i], unlockStatus.getOrDefault(chapter, false)))
                        .size(CARD_WIDTH, CARD_HEIGHT).pad(0, CHAPTER_CARD_PAD, 0, CHAPTER_CARD_PAD);
            }
        }

        carouselPane = new ScrollPane(carouselContent);
        carouselPane.setOverscroll(false, false);
        carouselPane.setFlickScroll(true);
        carouselPane.setScrollingDisabled(false, true);

        Table viewport = new Table();
        viewport.add(carouselPane).width(CAROUSEL_VIEWPORT_WIDTH).height(CARD_HEIGHT);

        Gdx.app.postRunnable(() -> carouselPane.setScrollX(UNIT_WIDTH));

        return viewport;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (carouselPane != null) {
            float maxX = carouselPane.getMaxX();
            float currentX = carouselPane.getScrollX();
            if (currentX <= 0f) {
                carouselPane.setScrollX(Math.min(currentX + UNIT_WIDTH, maxX));
            } else if (currentX >= maxX) {
                carouselPane.setScrollX(Math.max(currentX - UNIT_WIDTH, 0f));
            }
        }
    }

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton("assets/images/ui/buttons_hud_back_normal.png", 54, 54,
                () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.add(backBtn).padRight(16);
        topLeft.add(createIconButtonWithLabel("assets/images/ui/collection.png", 54, 54,
                "Collection", () -> runCommand("menu enter collection"))).padRight(16);
        topLeft.add(createIconButtonWithLabel("assets/images/ui/greenhouse.png", 54, 54,
                "Greenhouse", () -> runCommand("menu greenhouse"))).padRight(16);
        topLeft.add(createIconButtonWithLabel("assets/images/ui/leaderboard.png", 54, 54,
                "Leaderboard", () -> runCommand("menu leaderboard")));

        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;

        Table topRight = new Table();
        topRight.add(createResourceWidget("assets/images/ui/buttons_coin_buy_normal.png",
                String.valueOf(coins))).padRight(15);
        topRight.add(createResourceWidget("assets/images/ui/buttons_premium_normal.png",
                String.valueOf(diamonds)));

        Table topBar = new Table();
        topBar.add(topLeft).left().expandX();
        topBar.add(topRight).right();
        return topBar;
    }

    private Map<String, Boolean> computeChapterUnlockStatus() {
        Map<String, Boolean> status = new LinkedHashMap<>();
        try {
            List<Level> allLevels = LevelProgression.sorted(LevelLoader.loadLevels());
            int lastLevel = (User.currentUser != null && User.currentUser.userState != null)
                    ? User.currentUser.userState.lastLevel : 0;
            for (String chapter : CHAPTERS) {
                boolean unlocked = allLevels.stream()
                        .filter(level -> level.getSeason().getName().equalsIgnoreCase(chapter))
                        .anyMatch(level -> LevelProgression.isUnlocked(allLevels, lastLevel, level));
                status.put(chapter, unlocked);
            }
        } catch (Exception e) {
            for (String chapter : CHAPTERS) status.put(chapter, false);
        }
        return status;
    }

    private Actor createChapterCard(String chapter, String artPath, boolean unlocked) {
        Stack stack = new Stack();
        TextureRegionDrawable drawable = new TextureRegionDrawable(loadTextureSafe(artPath));
        ImageButton button = new ImageButton(drawable);

        Label titleLabel = new Label(chapter, skin, "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setWrap(true);
        Label statusLabel = new Label(unlocked ? "Unlocked" : "Locked", skin, unlocked ? "main" : "muted");
        statusLabel.setAlignment(Align.center);

        Table textTable = new Table();
        textTable.add(titleLabel).expandX().fillX().bottom().padBottom(30).padLeft(4).padRight(4).row();
        textTable.add(statusLabel).expandX().bottom().padBottom(10);

        stack.add(button);
        stack.add(textTable);

        stack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runCommand("menu enter chapter -c " + chapter);
            }
        });

        return stack;
    }

    private ImageButton createIconButton(String path, float width, float height, Runnable action) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(loadTextureSafe(path));
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

    

    

    @Override
    protected void onAfterCommand() {
        build();
    }
}