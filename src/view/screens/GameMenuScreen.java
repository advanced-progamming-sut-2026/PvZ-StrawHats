package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

    private static final float CARD_HEIGHT = 230f;
    private static final float CARD_PAD = 0f;

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
        rootTable.add(new Label("Choose a Chapter", skin, "title")).padTop(SPACE_LG).row();

        Map<String, Boolean> unlockStatus = computeChapterUnlockStatus();

        Table content = new Table();
        for (int i = 0; i < CHAPTERS.length; i++) {
            String chapter = CHAPTERS[i];
            content.add(createChapterCard(chapter, CHAPTER_ART[i], unlockStatus.getOrDefault(chapter, false)))
                    .expandX().fillX().height(CARD_HEIGHT).pad(CARD_PAD);
        }

        rootTable.add(content).fillX().padTop(SPACE_SM).padLeft(20).padRight(20).row();
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

    private Table createResourceWidget(String iconPath, String value) {
        Stack stack = new Stack();
        Table bgTable = new Table();
        bgTable.setBackground(new TextureRegionDrawable(loadTextureSafe(iconPath)));

        Table textTable = new Table();
        textTable.add(new Label(value, skin, "title")).center().expand();

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

    @Override
    protected void onAfterCommand() {
        build();
    }
}