package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.menus.match.MatchMenu;
import model.match.main.levels.Level;
import model.match.main.levels.special_levels.BossLevel;
import model.user_data.User;
import model.utils.LevelLoader;
import model.utils.LevelProgression;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.UiScreen;

import java.util.ArrayList;
import java.util.List;

public class DarkAgesStagesScreen extends UiScreen {

    private static final String CHAPTER_NAME = "Dark Ages";

    private static final String BACK_ICON = "assets/images/ui/buttons_hud_back_normal.png";
    private static final String COLLECTION_ICON = "assets/images/ui/collection.png";
    private static final String GREENHOUSE_ICON = "assets/images/ui/greenhouse.png";
    private static final String LEADERBOARD_ICON = "assets/images/ui/leaderboard.png";
    private static final String COIN_ICON = "assets/images/ui/buttons_coin_buy_normal.png";
    private static final String GEM_ICON = "assets/images/ui/buttons_premium_normal.png";

    private static final String CHAPTER_BACKGROUND = "";
    private static final String STAGE_NODE_ICON = "";
    private static final String BOSS_NODE_ICON = "";
    private static final String LOCK_ICON = "";
    private static final String STAR_ICON = "";

    private static final float NODE_SIZE = 108f;
    private static final float BOSS_NODE_SIZE = 138f;
    private static final float PATH_WIDTH = 1080f;
    private static final float PATH_HEIGHT = 360f;

    private static final Color TRAIL_COLOR = new Color(0.42f, 0.28f, 0.55f, 0.85f);
    private static final Color UNLOCKED_FILL = new Color(0.45f, 0.25f, 0.60f, 1f);
    private static final Color UNLOCKED_BOSS_FILL = new Color(0.33f, 0.55f, 0.28f, 1f);

    private List<Level> allLevels = new ArrayList<>();
    private List<Level> chapterLevels = new ArrayList<>();

    private Label selectionLabel;
    private TextButton playButton;

    @Override
    public void show() {
        if (!CHAPTER_BACKGROUND.isEmpty()) {
            setBackground(CHAPTER_BACKGROUND);
        }
        AudioManager.get().playMusic(AudioEnum.MENU_MUSIC, true);
        super.show();
        loadLevels();
        build();
    }

    private void loadLevels() {
        try {
            allLevels = LevelProgression.sorted(LevelLoader.loadLevels());
        } catch (Exception e) {
            allLevels = new ArrayList<>();
        }
        chapterLevels = allLevels.stream()
                .filter(level -> level.getSeason().getName().equalsIgnoreCase(CHAPTER_NAME))
                .toList();
    }

    private void build() {
        rootTable.clear();

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();
        rootTable.add(new Label("Dark Ages", skin, "title")).padTop(SPACE_MD).row();
        rootTable.add(buildPathContainer()).expand().padTop(SPACE_SM).row();
        rootTable.add(buildSelectionBar()).fillX().padBottom(SPACE_SM);
    }

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton(BACK_ICON, 54, 54, () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.add(backBtn).padRight(16);
        topLeft.add(createIconButtonWithLabel(COLLECTION_ICON, 54, 54,
                "Collection", () -> runCommand("menu enter collection"))).padRight(16);
        topLeft.add(createIconButtonWithLabel(GREENHOUSE_ICON, 54, 54,
                "Greenhouse", () -> runCommand("menu greenhouse"))).padRight(16);
        topLeft.add(createIconButtonWithLabel(LEADERBOARD_ICON, 54, 54,
                "Leaderboard", () -> runCommand("menu leaderboard")));

        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;

        Table topRight = new Table();
        topRight.add(createResourceWidget(COIN_ICON, String.valueOf(coins))).padRight(15);
        topRight.add(createResourceWidget(GEM_ICON, String.valueOf(diamonds)));

        Table topBar = new Table();
        topBar.add(topLeft).left().expandX();
        topBar.add(topRight).right();
        return topBar;
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

    private Table buildPathContainer() {
        Table wrap = new Table();
        wrap.add(new StagePath()).size(PATH_WIDTH, PATH_HEIGHT);
        return wrap;
    }

    private Table buildSelectionBar() {
        Table bar = new Table();
        bar.setBackground(skin.getDrawable("card-background"));
        bar.pad(14);

        selectionLabel = new Label("", skin, "main");
        selectionLabel.setWrap(true);

        playButton = primaryButton("Play", () -> runCommand("start game"));

        bar.add(selectionLabel).width(760).left().expandX();
        bar.add(playButton).width(180).height(56).padLeft(SPACE_LG);

        refreshSelectionBar();
        return bar;
    }

    private void refreshSelectionBar() {
        Level selected = MatchMenu.selectedLevel;
        boolean isDarkAgesSelection = selected != null
                && selected.getSeason().getName().equalsIgnoreCase(CHAPTER_NAME);
        if (isDarkAgesSelection) {
            selectionLabel.setText(selected.getName() + "\n" + selected.getGameMode());
            playButton.setDisabled(false);
            playButton.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
        } else {
            selectionLabel.setText("Tap an unlocked stage to select it.");
            playButton.setDisabled(true);
            playButton.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        }
    }

    @Override
    protected void onAfterCommand() {
        build();
    }

    private enum StageStatus { LOCKED, UNLOCKED, COMPLETED, CURRENT }

    private StageStatus statusOf(Level level) {
        int lastLevel = (User.currentUser != null && User.currentUser.userState != null)
                ? User.currentUser.userState.lastLevel : 0;
        Level selected = MatchMenu.selectedLevel;
        if (selected != null && selected.getId() == level.getId()) {
            return StageStatus.CURRENT;
        }
        if (LevelProgression.isCompleted(allLevels, lastLevel, level)) {
            return StageStatus.COMPLETED;
        }
        if (LevelProgression.isUnlocked(allLevels, lastLevel, level)) {
            return StageStatus.UNLOCKED;
        }
        return StageStatus.LOCKED;
    }

    private class StagePath extends Group {

        private final float[] centerX;
        private final float[] centerY;

        StagePath() {
            setSize(PATH_WIDTH, PATH_HEIGHT);

            int count = chapterLevels.size();
            centerX = new float[Math.max(count, 1)];
            centerY = new float[Math.max(count, 1)];

            for (int i = 0; i < count; i++) {
                float xFrac = count <= 1 ? 0.5f : 0.08f + 0.84f * i / (count - 1);
                float yFrac = (i == count - 1) ? 0.5f : (i % 2 == 0 ? 0.28f : 0.76f);
                centerX[i] = xFrac * PATH_WIDTH;
                centerY[i] = yFrac * PATH_HEIGHT;
            }

            addActor(new TrailActor());

            for (int i = 0; i < count; i++) {
                addActor(buildNode(chapterLevels.get(i), i, i + 1));
            }

            if (count == 0) {
                Label empty = new Label("No Dark Ages stages found.", skin, "muted");
                empty.setPosition(PATH_WIDTH / 2f - 130f, PATH_HEIGHT / 2f);
                addActor(empty);
            }
        }

        private Actor buildNode(Level level, int index, int stageNumber) {
            boolean boss = level instanceof BossLevel;
            float size = boss ? BOSS_NODE_SIZE : NODE_SIZE;
            StageStatus status = statusOf(level);

            Stack stack = new Stack();
            stack.setSize(size, size);

            Image circle = new Image(nodeCircleDrawable(size, boss, status));
            stack.add(circle);

            String iconPath = boss ? BOSS_NODE_ICON : STAGE_NODE_ICON;
            if (iconPath != null && !iconPath.isEmpty() && Gdx.files.internal(iconPath).exists()) {
                Image icon = new Image(loadTextureSafe(iconPath));
                stack.add(icon);
            } else {
                Label numberLabel = new Label(boss ? "BOSS" : String.valueOf(stageNumber), skin, "title");
                numberLabel.setAlignment(Align.center);
                stack.add(numberLabel);
            }

            if (status == StageStatus.LOCKED) {
                if (LOCK_ICON != null && !LOCK_ICON.isEmpty() && Gdx.files.internal(LOCK_ICON).exists()) {
                    Image lock = new Image(loadTextureSafe(LOCK_ICON));
                    stack.add(lock);
                } else {
                    Label lockLabel = new Label("LOCK", skin, "muted");
                    lockLabel.setAlignment(Align.center);
                    stack.add(lockLabel);
                }
            } else if (status == StageStatus.COMPLETED
                    && STAR_ICON != null && !STAR_ICON.isEmpty() && Gdx.files.internal(STAR_ICON).exists()) {
                Image star = new Image(loadTextureSafe(STAR_ICON));
                star.setSize(size * 0.4f, size * 0.4f);
                stack.add(star);
            }

            Table column = new Table();
            column.add(stack).size(size, size).row();
            Label nameLabel = new Label(level.getName(), skin, status == StageStatus.LOCKED ? "muted" : "main");
            nameLabel.setAlignment(Align.center);
            nameLabel.setFontScale(0.8f);
            nameLabel.setWrap(true);
            column.add(nameLabel).width(size + 40f).padTop(4);
            column.pack();
            column.setPosition(centerX[index] - column.getWidth() / 2f,
                    centerY[index] - column.getHeight() + size / 2f);

            if (status != StageStatus.LOCKED) {
                column.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        runCommand("select stage -s " + level.getId());
                    }
                });
            }

            return column;
        }

        private com.badlogic.gdx.scenes.scene2d.utils.Drawable nodeCircleDrawable(float size, boolean boss, StageStatus status) {
            Color fill = switch (status) {
                case LOCKED -> new Color(0.35f, 0.33f, 0.28f, 1f);
                case COMPLETED -> new Color(0.30f, 0.62f, 0.28f, 1f);
                case CURRENT -> new Color(0.95f, 0.75f, 0.15f, 1f);
                case UNLOCKED -> boss ? UNLOCKED_BOSS_FILL : UNLOCKED_FILL;
            };
            Color border = status == StageStatus.CURRENT ? Color.WHITE : new Color(0.15f, 0.12f, 0.06f, 1f);
            return circleDrawable((int) size, fill, border, status == StageStatus.CURRENT ? 5 : 3);
        }

        private com.badlogic.gdx.scenes.scene2d.utils.Drawable circleDrawable(int diameter, Color fill, Color border, int borderWidth) {
            Pixmap pixmap = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
            pixmap.setColor(border);
            pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2);
            pixmap.setColor(fill);
            pixmap.fillCircle(diameter / 2, diameter / 2, diameter / 2 - borderWidth);
            Texture texture = new Texture(pixmap);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pixmap.dispose();
            return new TextureRegionDrawable(texture);
        }

        private class TrailActor extends Actor {
            private final com.badlogic.gdx.graphics.g2d.TextureRegion pixel = whitePixelRegion();

            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(TRAIL_COLOR);
                for (int i = 0; i < centerX.length - 1; i++) {
                    drawSegment(batch, centerX[i], centerY[i], centerX[i + 1], centerY[i + 1]);
                }
                batch.setColor(Color.WHITE);
            }

            private void drawSegment(Batch batch, float x1, float y1, float x2, float y2) {
                float dx = x2 - x1;
                float dy = y2 - y1;
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                float thickness = 14f;
                batch.draw(pixel, x1, y1 - thickness / 2f, 0f, thickness / 2f,
                        length, thickness, 1f, 1f, angle);
            }
        }
    }

    private static com.badlogic.gdx.graphics.g2d.TextureRegion whitePixelRegion() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new com.badlogic.gdx.graphics.g2d.TextureRegion(texture);
    }
}