package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.CollectionManager;
import model.collections.animations.AnimationFactory;
import model.collections.plant.PlantJsonParser;
import model.user_data.User;
import model.user_data.UserState;
import service.card_factory.SeedPacketCard;
import service.card_factory.SeedPacketCardFactory;
import view.general_screens.UiScreen;

// --- PAM imports (same pattern as EgyptStagesScreen) ---
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.textures.TextureBank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionScreen extends UiScreen {

    private static final String BACK_ICON = "assets/images/ui/buttons_hud_back_normal.png";
    private static final String COIN_ICON = "assets/images/ui/buttons_coin_buy_normal.png";
    private static final String GEM_ICON = "assets/images/ui/buttons_premium_normal.png";

    // TODO: paste the real lock icon's asset path here, e.g. "assets/images/ui/xxxx_lock.png"
    private static final String LOCK_ICON = "assets/images/ui/lock.png";

    private static final int PLANTS_PER_ROW = 7;
    private static final float CARD_W = 150f;
    private static final float CARD_H = 190f;
    private static final float BAR_H = 14f;

    private static final Color YELLOW = new Color(0.95f, 0.80f, 0.15f, 1f);
    private static final Color GREEN = new Color(0.30f, 0.75f, 0.25f, 1f);
    private static final Color GRAY = new Color(0.45f, 0.45f, 0.45f, 1f);

    private enum CollectionTab { PLANTS, ZOMBIES }

    private CollectionTab currentTab = CollectionTab.PLANTS;

    private final SeedPacketCardFactory cardFactory = new SeedPacketCardFactory();
    private final CollectionManager manager = new CollectionManager();

    // Which plant's full-screen info popup is currently open, if any. Kept across
    // rebuilds so purchase/upgrade actions can refresh the popup in place.
    private Integer openPlantId = null;
    private Actor popupOverlay;

    private final TextureRegion whitePixel = whitePixelRegion();

    private TextureBank textureBank;
    private PamPlayer pamPlayer;
    private Map<String,Boolean> visibility = new HashMap<>();

    @Override
    public void show() {
        if (textureBank == null) {
            try {
                // Same PAM root/init pattern as EgyptStagesScreen.
                FileHandle rootHandle = Gdx.files.internal("assets/pvz-assets");
                textureBank = new TextureBank("atlases", rootHandle);
                pamPlayer = new PamPlayer(textureBank, rootHandle);

                Gdx.app.log("PAM_INIT", "PAM System and TextureBank initialized successfully!");
            } catch (Throwable t) {
                Gdx.app.error("PAM_INIT", "Failed to initialize PAM System", t);
            }
        }

        super.show();
        build();
    }

    @Override
    public void render(float delta) {
        if (textureBank != null) {
            try {
                textureBank.update();
            } catch (Throwable t) {
                Gdx.app.error("CollectionScreen", "textureBank.update() failed", t);
            }
        }
        super.render(delta);
    }

    @Override
    public void dispose() {
        try {
            cardFactory.dispose();
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to dispose card factory", t);
        }
        super.dispose();
    }

    private void build() {
        rootTable.clear();

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();
        rootTable.add(new Label("Collection", skin, "title")).padTop(SPACE_MD).row();
        rootTable.add(buildTabs()).padTop(SPACE_SM).row();

        Table board = new Table();
        board.setBackground(skin.getDrawable("card-background"));
        board.pad(SPACE_MD);
        board.add(currentTab == CollectionTab.PLANTS ? buildPlantsTabBox() : buildZombiesTabBox())
                .size(1180, 480);
        rootTable.add(board).expand().padTop(SPACE_SM);

        // A rebuild invalidates any popup actor currently on screen (it may be showing
        // stale level/coin data), so it's torn down and, if one was open, rebuilt fresh
        // for the same plant - this is what makes the purchase/upgrade button flip
        // immediately after a successful command.
        if (popupOverlay != null) {
            popupOverlay.remove();
            popupOverlay = null;
        }
        if (openPlantId != null) {
            openPlantInfo(openPlantId);
        }
    }

    @Override
    protected void onAfterCommand() {
        build();
    }

    // ------------------------------------------------------------------
    // Top bar / tabs
    // ------------------------------------------------------------------

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton(BACK_ICON, 54, 54, () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.add(backBtn);

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

    private Table buildTabs() {
        Table tabs = new Table();
        tabs.add(buildTab("Plants", CollectionTab.PLANTS)).padRight(SPACE_MD);
        tabs.add(buildTab("Zombies", CollectionTab.ZOMBIES));
        return tabs;
    }

    private Table buildTab(String label, CollectionTab tab) {
        Table container = new Table();
        if (tab == currentTab) {
            container.setBackground(skin.getDrawable("card-background"));
        }
        container.pad(SPACE_XS, SPACE_LG, SPACE_XS, SPACE_LG);

        Label tabLabel = new Label(label, skin, tab == currentTab ? "title" : "muted");
        container.add(tabLabel);

        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentTab == tab) {
                    return;
                }
                currentTab = tab;
                openPlantId = null;
                build();
            }
        });
        return container;
    }

    // ------------------------------------------------------------------
    // Plants tab
    // ------------------------------------------------------------------

    private Table buildPlantsTabBox() {
        Table box = new Table();
        box.top();

        UserState state = User.currentUser.userState;
        List<PlantJsonParser.PlantConfig> plants = sortedPlants(state);

        Table grid = new Table();
        grid.top();
        Table currentRow = null;
        for (int i = 0; i < plants.size(); i++) {
            if (i % PLANTS_PER_ROW == 0) {
                currentRow = new Table();
                grid.add(currentRow).left().row();
            }
            currentRow.add(buildPlantCardCell(plants.get(i), state)).pad(SPACE_SM);
        }

        ScrollPane pane = scrollable(grid);
        box.add(pane).expand().fill();
        return box;
    }

    /** Sorted the same way the console collection commands treat plants: unlocked first. */
    private List<PlantJsonParser.PlantConfig> sortedPlants(UserState state) {
        List<PlantJsonParser.PlantConfig> all = manager.getAllPlants();
        all.sort((a, b) -> {
            boolean unlockedA = state.isPlantUnlocked(a.id);
            boolean unlockedB = state.isPlantUnlocked(b.id);
            if (unlockedA != unlockedB) {
                return unlockedA ? -1 : 1;
            }
            return Integer.compare(a.id, b.id);
        });
        return all;
    }

    private Table buildPlantCardCell(PlantJsonParser.PlantConfig config, UserState state) {
        boolean unlocked = state.isPlantUnlocked(config.id);

        Table cell = new Table();

        Stack cardStack = new Stack();
        cardStack.setSize(CARD_W, CARD_H);

        try {
            SeedPacketCard card = cardFactory.buildCardForDisplayName(config.name);
            if (card != null) {
                cardStack.add(card);
            }
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to build seed packet card for " + config.name, t);
        }

        if (!unlocked) {
            // Dim the whole card, then sit a small fixed-size lock badge centered on top.
            Image dim = new Image(solidColorDrawable(new Color(0f, 0f, 0f, 0.35f)));
            cardStack.add(dim);

            Image lockImage = new Image(loadTextureSafe(LOCK_ICON));
            Container<Image> lockContainer = new Container<>(lockImage);
            lockContainer.size(56f, 56f);
            lockContainer.align(Align.center);
            cardStack.add(lockContainer);
        }

        cardStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openPlantId = config.id;
                openPlantInfo(config.id);
            }
        });

        cell.add(cardStack).size(CARD_W, CARD_H).row();

        if (unlocked) {
            int level = state.getPlantLevel(config.id);
            int packetsOwned = state.seedPacketInventory.getOrDefault(config.id, 0);
            float progress = level <= 0 ? 1f : Math.min(1f, (float) packetsOwned / level);

            SeedProgressBar bar = new SeedProgressBar();
            bar.setProgress(progress);
            cell.add(bar).width(CARD_W).height(BAR_H).padTop(4);
        } else {
            // Empty spacer so locked/unlocked rows still line up.
            cell.add(new Table()).width(CARD_W).height(BAR_H).padTop(4);
        }

        return cell;
    }

    // ------------------------------------------------------------------
    // Zombies tab - intentionally left empty for now.
    // ------------------------------------------------------------------

    private Table buildZombiesTabBox() {
        Table box = new Table();
        box.add(new Label("Zombie collection coming soon.", skin, "muted"));
        return box;
    }

    // ------------------------------------------------------------------
    // Full-screen plant info popup
    // ------------------------------------------------------------------

    private void openPlantInfo(int plantId) {
        PlantJsonParser.PlantConfig config = null;
        for (PlantJsonParser.PlantConfig candidate : manager.getAllPlants()) {
            if (candidate.id == plantId) {
                config = candidate;
                break;
            }
        }
        if (config == null) {
            openPlantId = null;
            return;
        }

        if (popupOverlay != null) {
            popupOverlay.remove();
        }
        popupOverlay = buildPlantInfoPopup(config);
        getModalStack().add(popupOverlay);
    }

    private void closePlantInfo() {
        if (popupOverlay != null) {
            popupOverlay.remove();
            popupOverlay = null;
        }
        openPlantId = null;
    }

    private Stack buildPlantInfoPopup(PlantJsonParser.PlantConfig config) {
        UserState state = User.currentUser.userState;
        boolean unlocked = state.isPlantUnlocked(config.id);

        Stack popupStack = new Stack();

        Image scrim = new Image(solidColorDrawable(new Color(0f, 0f, 0f, 0.72f)));
        popupStack.add(scrim);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("card-background"));
        panel.pad(SPACE_LG);
        panel.top().left();

        Table header = new Table();
        // Same "back to last menu" icon used everywhere else in the UI.
        ImageButton back = createIconButton(BACK_ICON, 50, 50, this::closePlantInfo);
        header.add(back).left().expandX();
        panel.add(header).fillX().padBottom(SPACE_MD).row();

        Table body = new Table();
        body.top();

        // Left square: the plant's idle animation, via the same collection animation
        // mechanism used elsewhere (model.collections.animations.AnimationFactory).
        Table animBox = new Table();
        animBox.setBackground(skin.getDrawable("modal-background"));
        String animationPath = null;
        try {
            animationPath = AnimationFactory.pathForDisplayName(config.name);
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to resolve idle animation for " + config.name, t);
        }
        PlantIdleAnimationActor animActor = new PlantIdleAnimationActor(animationPath);
        animBox.add(animActor).size(220, 220);
        body.add(animBox).size(250, 250).padRight(SPACE_LG).top();

        // Right side: info + purchase/upgrade button.
        Table info = new Table();
        info.top().left();

        Label nameLabel = new Label(config.name, skin, "title");
        nameLabel.setFontScale(1.3f);
        info.add(nameLabel).left().padBottom(SPACE_SM).row();

        info.add(statLabel("Type: " + config.category)).left().row();
        info.add(statLabel("HP: " + config.baseHp)).left().row();
        info.add(statLabel("Damage: " + config.damage)).left().row();
        info.add(statLabel("Recharge: " + config.recharge + "s")).left().row();
        String tags = (config.tags == null || config.tags.isEmpty())
                ? "None"
                : config.tags.stream().map(Enum::name).collect(Collectors.joining(", "));
        info.add(statLabel("Tags: " + tags)).left().padBottom(SPACE_LG).row();

        TextButton actionButton = buildActionButton(config, state, unlocked);
        info.add(actionButton).width(320).height(58).left();

        body.add(info).top().left().expandX();
        panel.add(body).row();

        Table centered = new Table();
        centered.add(panel).width(SCREEN_WIDTH * 0.82f).height(SCREEN_HEIGHT * 0.8f);
        popupStack.add(centered);

        // Tapping the dimmed area outside the panel closes the popup too.
        popupStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == popupStack) {
                    closePlantInfo();
                }
            }
        });

        return popupStack;
    }

    private TextButton buildActionButton(PlantJsonParser.PlantConfig config, UserState state, boolean unlocked) {
        if (!unlocked) {
            int cost = CollectionManager.getPurchaseCost();
            TextButton button = coloredButton("Purchase - " + cost + " coins", YELLOW);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Same command the console collection menu already uses; runCommand
                    // rebuilds the screen afterward (see onAfterCommand), which is what
                    // flips this button into the upgrade button immediately on success.
                    runCommand("menu collection purchase-plant -p " + config.name);
                }
            });
            return button;
        }

        int level = state.getPlantLevel(config.id);
        int coinCost = level * 500;
        int packetsNeeded = level;
        int packetsOwned = state.seedPacketInventory.getOrDefault(config.id, 0);
        boolean canUpgrade = state.coins >= coinCost && packetsOwned >= packetsNeeded;

        String label = "Upgrade to Lv " + (level + 1) + " - " + coinCost + " coins, "
                + packetsOwned + "/" + packetsNeeded + " packets";
        TextButton button = coloredButton(label, canUpgrade ? GREEN : GRAY);
        button.setDisabled(!canUpgrade);
        if (canUpgrade) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runCommand("menu collection upgrade-plant -p " + config.name);
                }
            });
        }
        return button;
    }

    private Label statLabel(String text) {
        Label label = new Label(text, skin, "main");
        label.setFontScale(0.9f);
        return label;
    }

    private TextButton coloredButton(String text, Color color) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default-font");
        style.fontColor = Color.BLACK;
        style.up = solidColorDrawable(color);
        style.down = solidColorDrawable(color.cpy().mul(0.85f, 0.85f, 0.85f, 1f));
        style.disabled = solidColorDrawable(GRAY);
        style.disabledFontColor = new Color(0.8f, 0.8f, 0.8f, 1f);
        return new TextButton(text, style);
    }

    // ------------------------------------------------------------------
    // Idle-animation actor - same PAM syntax as EgyptStagesScreen's MapDecorationActor.
    // ------------------------------------------------------------------

    private class PlantIdleAnimationActor extends Actor {
        private final String animationPath;
        private float stateTime = 0f;

        PlantIdleAnimationActor(String animationPath) {
            this.animationPath = animationPath;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            stateTime += delta;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (pamPlayer == null || animationPath == null) {
                return;
            }
            try {
                String clipName = AnimationFactory.resolveClipNameForPath(animationPath, "idle");
                if (clipName == null) {
                    return;
                }

                ClipRef clip = pamPlayer.getClip(animationPath, clipName);
                if(!animationPath.contains("MAGNETSHROOM")) {

                    if (clip != null) {
                        pamPlayer.draw(batch, clip, stateTime, getX(), getY(), true);
                    }
                }
                else {
                    visibility.put("Magnet_Item",false);
                    if (clip != null) {
                        pamPlayer.draw(batch, clip, stateTime, getX(), getY(), true,visibility);
                    }
                }
            } catch (Throwable t) {
                Gdx.app.error("CollectionScreen", "Failed to draw idle animation for path " + animationPath, t);
            }
        }
    }

    // ------------------------------------------------------------------
    // Seed-packet counter bar: yellow while filling, green once full (upgrade ready).
    // ------------------------------------------------------------------

    private class SeedProgressBar extends Actor {
        private float progress = 0f;

        void setProgress(float value) {
            this.progress = Math.max(0f, Math.min(1f, value));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = getX(), y = getY(), w = getWidth(), h = getHeight();

            batch.setColor(0.12f, 0.10f, 0.06f, 0.9f);
            batch.draw(whitePixel, x, y, w, h);

            batch.setColor(progress >= 1f ? GREEN : YELLOW);
            batch.draw(whitePixel, x, y, w * progress, h);

            batch.setColor(Color.WHITE);
        }
    }

    // ------------------------------------------------------------------
    // Small local helpers (same pattern used in the other screens in this project).
    // ------------------------------------------------------------------

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

    private Drawable solidColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private static TextureRegion whitePixelRegion() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }
}
