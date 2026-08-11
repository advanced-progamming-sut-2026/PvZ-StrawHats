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
    private static final String LOCK_ICON = "assets/images/lock.png";
    private static final String UPGRADE_ICON = "assets/images/ui/collection/rift_perk_upgrade_uparrow.png";

    private static final String MAIN_BG = "assets/images/backg/mainmenu_background.png";
    private static final String TAB_ACTIVE_BG = "assets/images/ui/zombies_active.png";
    private static final String TAB_BOARD_BG = "assets/images/backg/wood board.png";

    private static final int PLANTS_PER_ROW = 5;
    private static final float CARD_W = 135f;
    private static final float BAR_H = 14f;

    private static final Color YELLOW = new Color(0.95f, 0.80f, 0.15f, 1f);
    private static final Color GREEN = new Color(0.30f, 0.75f, 0.25f, 1f);
    private static final Color GRAY = new Color(0.45f, 0.45f, 0.45f, 1f);

    private enum CollectionTab { PLANTS, ZOMBIES }

    private CollectionTab currentTab = CollectionTab.PLANTS;

    private final SeedPacketCardFactory cardFactory = new SeedPacketCardFactory();
    private final CollectionManager manager = new CollectionManager();

    private Integer openPlantId = null;
    private Actor popupOverlay;

    private final TextureRegion whitePixel = whitePixelRegion();
    private Texture upgradeIconTexture;

    private TextureBank textureBank;
    private PamPlayer pamPlayer;
    private Map<String,Boolean> visibility = new HashMap<>();

    @Override
    public void show() {
        if (textureBank == null) {
            try {
                FileHandle rootHandle = Gdx.files.internal("assets/pvz-assets");
                textureBank = new TextureBank("atlases", rootHandle);
                pamPlayer = new PamPlayer(textureBank, rootHandle);

                Gdx.app.log("PAM_INIT", "PAM System and TextureBank initialized successfully!");
            } catch (Throwable t) {
                Gdx.app.error("PAM_INIT", "Failed to initialize PAM System", t);
            }
        }

        if (upgradeIconTexture == null) {
            upgradeIconTexture = loadTextureSafe(UPGRADE_ICON);
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
            if (upgradeIconTexture != null) {
                upgradeIconTexture.dispose();
            }
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to dispose card factory", t);
        }
        super.dispose();
    }

    private void build() {
        rootTable.clear();
        rootTable.setBackground(new TextureRegionDrawable(loadTextureSafe(MAIN_BG)));

        Stack mainStack = new Stack();
        Table boardContainer = new Table();
        boardContainer.top();
        Table contentBox = currentTab == CollectionTab.PLANTS ? buildPlantsTabBox() : buildZombiesTabBox();
        boardContainer.add(contentBox).expand().fill().padTop(68f).padLeft(20f).padRight(20f).padBottom(20f);

        Table topBarContainer = new Table();
        topBarContainer.top();
        topBarContainer.add(buildTopBar()).expandX().fillX().padTop(12f).padLeft(20f).padRight(20f);

        mainStack.add(boardContainer);
        mainStack.add(topBarContainer);

        rootTable.add(mainStack).expand().fill();

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

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton(BACK_ICON, 54, 54, () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.left();
        topLeft.add(backBtn).padRight(20).top();
        topLeft.add(buildTabs()).top();

        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;

        Table topRight = new Table();
        topRight.right();
        topRight.add(createResourceWidget(COIN_ICON, String.valueOf(coins))).padRight(15).padTop(-70);
        topRight.add(createResourceWidget(GEM_ICON, String.valueOf(diamonds))).padTop(-70);

        Table topBar = new Table();
        topBar.add(topLeft).left().expandX();
        topBar.add(topRight).right();
        return topBar;
    }

    private Table buildTabs() {
        Table tabs = new Table();
        tabs.left();
        tabs.add(buildTab("Plants", CollectionTab.PLANTS)).padRight(SPACE_SM);
        tabs.add(buildTab("Zombies", CollectionTab.ZOMBIES));
        return tabs;
    }

    private Table buildTab(String label, CollectionTab tab) {
        Table container = new Table();

        if (tab == currentTab) {
            container.setBackground(new TextureRegionDrawable(loadTextureSafe(TAB_ACTIVE_BG)));
            container.pad(8, 22, 10, 22);

            Label tabLabel = new Label(label, skin, "title");
            container.add(tabLabel).center();
        } else {
            TextureRegionDrawable tabDrawable = new TextureRegionDrawable(loadTextureSafe(TAB_ACTIVE_BG));
            Drawable inactiveDrawable = tabDrawable.tint(new Color(0.35f, 0.30f, 0.45f, 0.88f));
            container.setBackground(inactiveDrawable);
            container.pad(8, 22, 14, 22);

            Label tabLabel = new Label(label, skin, "muted");
            tabLabel.setColor(new Color(0.85f, 0.85f, 0.90f, 1f));
            container.add(tabLabel).center();
        }

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

    private Table buildPlantsTabBox() {
        Table box = new Table();
        box.setBackground(new TextureRegionDrawable(loadTextureSafe(TAB_BOARD_BG)));
        box.top();

        UserState state = User.currentUser.userState;
        List<PlantJsonParser.PlantConfig> plants = sortedPlants(state);

        Table grid = new Table();
        grid.top().padTop(10f);
        Table currentRow = null;
        for (int i = 0; i < plants.size(); i++) {
            if (i % PLANTS_PER_ROW == 0) {
                currentRow = new Table();
                grid.add(currentRow).center().padBottom(SPACE_SM).row();
            }
            currentRow.add(buildPlantCardCell(plants.get(i), state)).padLeft(SPACE_SM).padRight(SPACE_SM);
        }

        ScrollPane pane = scrollable(grid);
        box.add(pane).expand().fill().padTop(35f).padBottom(15f).padLeft(20f).padRight(20f);
        return box;
    }

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
        float cardW = CARD_W;
        float cardH = CARD_W * 1.38f;

        try {
            SeedPacketCard card = cardFactory.buildCardForDisplayName(config.name);
            if (card != null) {
                cardW = card.getWidth();
                cardH = card.getHeight();
                cardStack.add(card);
            }
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to build seed packet card for " + config.name, t);
        }

        cardStack.setSize(cardW, cardH);

        if (!unlocked) {
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

        cell.add(cardStack).size(cardW, cardH).row();

        if (unlocked) {
            int level = state.getPlantLevel(config.id);
            int packetsOwned = state.seedPacketInventory.getOrDefault(config.id, 0);
            float progress = level <= 0 ? 1f : Math.min(1f, (float) packetsOwned / level);

            int coinCost = level * 500;
            int packetsNeeded = level;
            boolean canUpgrade = state.coins >= coinCost && packetsOwned >= packetsNeeded;

            SeedProgressBar bar = new SeedProgressBar();
            bar.setProgress(progress, canUpgrade, upgradeIconTexture);
            cell.add(bar).width(cardW).height(BAR_H).padTop(4);
        } else {
            cell.add(new Table()).width(cardW).height(BAR_H).padTop(4);
        }

        return cell;
    }

    private Table buildZombiesTabBox() {
        Table box = new Table();
        box.setBackground(new TextureRegionDrawable(loadTextureSafe(TAB_BOARD_BG)));
        box.top();
        box.add(new Label("Zombie collection coming soon.", skin, "muted")).padTop(60f);
        return box;
    }

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
        panel.setBackground(new TextureRegionDrawable(loadTextureSafe("assets/images/backg/wood board.png")));
        panel.pad(SPACE_LG);
        panel.top().left();

        Table header = new Table();
        ImageButton back = createIconButton(BACK_ICON, 50, 50, this::closePlantInfo);
        header.add(back).left().expandX();
        panel.add(header).fillX().padBottom(SPACE_MD).row();

        Table body = new Table();
        body.top();

        Table animBox = new Table();
        animBox.setBackground(new TextureRegionDrawable(loadTextureSafe("assets/images/ui/collection/card_plant_bg_modern.png")));
        String animationPath = null;
        try {
            animationPath = AnimationFactory.pathForDisplayName(config.name);
        } catch (Throwable t) {
            Gdx.app.error("CollectionScreen", "Failed to resolve idle animation for " + config.name, t);
        }
        PlantIdleAnimationActor animActor = new PlantIdleAnimationActor(animationPath, 0, 0);
        animBox.add(animActor).size(220, 220);
        body.add(animBox).size(250, 250).padRight(SPACE_LG).top();

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

        body.add(info).top().left().expandX().padLeft(30f);
        panel.add(body).row();

        Table centered = new Table();
        centered.add(panel).width(SCREEN_WIDTH * 0.82f).height(SCREEN_HEIGHT * 0.8f);
        popupStack.add(centered);

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

    private class PlantIdleAnimationActor extends Actor {
        private final String animationPath;
        private float stateTime = 0f;
        private float offsetX = 0f;
        private float offsetY = 0f;

        PlantIdleAnimationActor(String animationPath, float ox, float oy) {
            this.animationPath = animationPath;
            this.offsetX = ox + 110f;
            this.offsetY = oy + 110f;
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
                        pamPlayer.draw(batch, clip, stateTime, getX() + offsetX, getY() + offsetY, true);
                    }
                }
                else {
                    visibility.put("Magnet_Item",false);
                    if (clip != null) {
                        pamPlayer.draw(batch, clip, stateTime, getX() + offsetX, getY() + offsetY, true,visibility);
                    }
                }
            } catch (Throwable t) {
                Gdx.app.error("CollectionScreen", "Failed to draw idle animation for path " + animationPath, t);
            }
        }
    }

    private class SeedProgressBar extends Actor {
        private float progress = 0f;
        private boolean canUpgrade = false;
        private Texture upgradeTexture;

        void setProgress(float value, boolean canUpgrade, Texture upgradeTexture) {
            this.progress = Math.max(0f, Math.min(1f, value));
            this.canUpgrade = canUpgrade;
            this.upgradeTexture = upgradeTexture;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = getX(), y = getY(), w = getWidth(), h = getHeight();

            batch.setColor(0.12f, 0.10f, 0.06f, 0.9f);
            batch.draw(whitePixel, x, y, w, h);

            boolean isFullAndReady = (progress >= 1f && canUpgrade);
            batch.setColor(isFullAndReady ? GREEN : YELLOW);
            batch.draw(whitePixel, x, y, w * progress, h);

            batch.setColor(GRAY);
            batch.draw(whitePixel, x, y + h - 1f, w, 1f);
            batch.draw(whitePixel, x, y, w, 1f);
            batch.draw(whitePixel, x, y, 1f, h);
            batch.draw(whitePixel, x + w - 1f, y, 1f, h);

            if (isFullAndReady && upgradeTexture != null) {
                batch.setColor(Color.WHITE);
                float iconSize = h - 2f;
                batch.draw(upgradeTexture, x + 2f, y + 1f, iconSize, iconSize);
            }

            batch.setColor(Color.WHITE);
        }
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

    protected Table createResourceWidget(String iconPath, String value) {
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

    protected Texture loadTextureSafe(String path) {
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

    protected static TextureRegion whitePixelRegion() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }
}