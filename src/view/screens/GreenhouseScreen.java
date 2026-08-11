package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.menus.greenhouse.PotController;
import model.collections.animations.AnimationFactory;
import model.collections.animations.AnimationJsonParser;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.greenhouse.PotPlant;
import model.user_data.User;
import model.user_data.UserState;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.Toast;
import view.general_screens.UiScreen;


import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.textures.TextureBank;

public class GreenhouseScreen extends UiScreen {

    private static final String BACK_ICON = "assets/images/ui/buttons_hud_back_normal.png";
    private static final String COLLECTION_ICON = "assets/images/ui/collection.png";
    private static final String COIN_ICON = "assets/images/ui/buttons_coin_buy_normal.png";
    private static final String GEM_ICON = "assets/images/ui/buttons_premium_normal.png";
    static final String SEED_PACKET_BG = "assets/images/ui/seedpacket_bg.png";
    static final String SEED_PACKET_ICON = "assets/images/ui/seedpacket.png";

    private static final String GREENHOUSE_BACKGROUND = "assets/images/backg/greenhouse.png";
    private static final String POT_EMPTY_ICON = "assets/images/greenhouse/pot_empty.png";
    private static final String POT_LOCKED_ICON = "assets/images/greenhouse/pot_empty.png";
    private static final String POT_READY_ICON = "assets/images/greenhouse/pot_growing.png";
    private static final String SPARKLE_ICON = "assets/images/greenhouse/sparkles.png";
    private static final String LOCK_ICON = "assets/images/greenhouse/lock_medium.png";

    private static final float POT_SIZE = 92f;
    private static final float POT_CELL_EXTRA_WIDTH = 50f;
    private static final float POT_CELL_EXTRA_HEIGHT = 40f;
    private static final float POT_CELL_GAP = 8f;

    private TextureBank textureBank;
    private PamPlayer pamPlayer;

    @Override
    public void show() {
        setBackground(GREENHOUSE_BACKGROUND);
        AudioManager.get().playMusic(AudioEnum.MENU_MUSIC, true);

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

        super.show();
        build();
    }

    @Override
    public void render(float delta) {
        if (textureBank != null) {
            try {
                textureBank.update();
            } catch (Throwable t) {
                Gdx.app.error("GreenhouseScreen", "textureBank.update() failed", t);
            }
        }
        super.render(delta);
    }

    private void build() {
        rootTable.clear();

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();
        Label label = new Label("Greenhouse", skin, "title");
        label.setFontScale(1.8f);
        rootTable.add(label).padTop(SPACE_MD).padBottom(-160).row();

        Greenhouse greenhouse = Greenhouse.getInstance();

        rootTable.add(buildPotGrid(greenhouse)).expand().padTop(SPACE_MD).padBottom(-195).row();
        rootTable.add(buildBottomBar()).padBottom(SPACE_MD);
    }

    private Table buildPotGrid(Greenhouse greenhouse) {
        Table grid = new Table();
        for (int row = 1; row <= Greenhouse.getRowCount(); row++) {
            for (int col = 1; col <= Greenhouse.getColCount(); col++) {
                Pot pot = greenhouse.getPot(col, row);
                grid.add(buildPotCell(pot, col, row))
                        .size(POT_SIZE + POT_CELL_EXTRA_WIDTH, POT_SIZE + POT_CELL_EXTRA_HEIGHT)
                        .pad(POT_CELL_GAP);
            }
            grid.row();
        }
        return grid;
    }

    private Table buildBottomBar() {
        Table bar = new Table();
        bar.add(primaryButton("Visit Shop", () -> runCommand("enter shop"))).width(220).height(60);
        return bar;
    }

    private enum PotStatus { LOCKED, EMPTY, GROWING, READY }

    private PotStatus statusOf(Pot pot) {
        if (pot.isLocked()) return PotStatus.LOCKED;
        PotPlant plant = pot.getPotPlant();
        if (plant == null) return PotStatus.EMPTY;
        return plant.isCollectAble() ? PotStatus.READY : PotStatus.GROWING;
    }

    private Table buildPotCell(Pot pot, int x, int y) {
        PotStatus status = statusOf(pot);
        PotPlant plant = pot.getPotPlant();

        Stack stack = new Stack();
        stack.setSize(POT_SIZE, POT_SIZE);

        
        String iconPath = switch (status) {
            case LOCKED -> POT_LOCKED_ICON;
            case EMPTY, GROWING -> POT_EMPTY_ICON;
            case READY -> POT_READY_ICON;
        };

        
        if (!iconPath.isEmpty() && Gdx.files.internal(iconPath).exists()) {
            stack.add(new Image(loadTextureSafe(iconPath)));
        } else {
            stack.add(new Label(fallbackGlyph(status, plant), skin, "title"));
        }

        if ((status == PotStatus.GROWING || status == PotStatus.READY) && plant != null && pamPlayer != null) {
            AnimationJsonParser.AnimationConfig animConfig = AnimationFactory.resolveByDisplayName(plant.getPlantName());
            if (animConfig != null && animConfig.path != null && !animConfig.path.isEmpty()) {
                Actor plantVisual = new PlantIdleAnimationActor(animConfig.path,
                        animConfig.canvasWidth(), animConfig.canvasHeight(), POT_SIZE);
                stack.add(plantVisual);
            }
        }

        
        if (status == PotStatus.LOCKED && !LOCK_ICON.isEmpty() && Gdx.files.internal(LOCK_ICON).exists()) {
            Table lockBadge = new Table();
            lockBadge.add(new Image(loadTextureSafe(LOCK_ICON))).size(44, 64);
            stack.add(lockBadge);
        }

        if (status == PotStatus.READY) {
            Actor readyGlow;
            if (!SPARKLE_ICON.isEmpty() && Gdx.files.internal(SPARKLE_ICON).exists()) {
                readyGlow = new Image(loadTextureSafe(SPARKLE_ICON));
            } else {
                Label ready = new Label("READY", skin, "title");
                ready.setFontScale(0.75f);
                ready.setAlignment(Align.bottom);
                readyGlow = ready;
            }
            stack.add(readyGlow);
            readyGlow.addAction(Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.12f, 1.12f, 0.5f),
                    Actions.scaleTo(1f, 1f, 0.5f))));
        }

        Table column = new Table();
        column.add(stack).size(POT_SIZE, POT_SIZE).row();

        Label captionLabel = new Label(captionFor(status, plant), skin, "title");
        captionLabel.setAlignment(Align.center);
        captionLabel.setWrap(true);
        column.add(captionLabel).width(POT_SIZE + 26f).padTop(4);

        column.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float clickX, float clickY) {
                onPotClicked(status, pot, x, y);
            }
        });

        return column;
    }

    private void onPotClicked(PotStatus status, Pot pot, int x, int y) {
        switch (status) {
            case LOCKED -> Toast.show(stage, "Locked - buy a Vase in the Shop, or find one during a level.");
            case EMPTY -> runCommand("plant pot at (" + x + ", " + y + ")");
            case READY -> runCommand("collect (" + x + ", " + y + ")");
            case GROWING -> confirmGrow(pot, x, y);
        }
    }

    private void confirmGrow(Pot pot, int x, int y) {
        PotController controller = pot.getPotController();
        int cost = controller.calculateGrowCost();
        UserState state = User.currentUser.userState;
        String message = "Spend " + cost + " diamond(s) to finish growing " + pot.getPotPlant().getPlantName()
                + " right now? You have " + state.diamonds + ".";
        new ConfirmModal("Speed up growth?", message, "Grow now",
                () -> runCommand("grow (" + x + ", " + y + ")")).show();
    }

    private String captionFor(PotStatus status, PotPlant plant) {
        return switch (status) {
            case LOCKED -> "Locked";
            case EMPTY -> "Tap to plant";
            case READY -> "Ready";
            case GROWING -> Greenhouse.formatDuration(plant.getRemainingSeconds());
        };
    }

    private String fallbackGlyph(PotStatus status, PotPlant plant) {
        return switch (status) {
            case LOCKED -> "";
            case EMPTY -> "+";
            case READY -> "!";
            case GROWING -> (plant != null && plant.isMarigold()) ? "M" : "P";
        };
    }

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton(BACK_ICON, 54, 54, () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.add(backBtn).padRight(16);
        topLeft.add(createIconButtonWithLabel(COLLECTION_ICON, 54, 54,
                "Collection", () -> runCommand("menu enter collection")));

        User user = User.currentUser;
        int coins = (user != null && user.userState != null) ? user.userState.coins : 0;
        int diamonds = (user != null && user.userState != null) ? user.userState.diamonds : 0;
        int seedPackets = totalSeedPackets(user);

        Table currencyRow = new Table();
        currencyRow.add(createResourceWidget(COIN_ICON, String.valueOf(coins), 130, 42)).padRight(15);
        currencyRow.add(createResourceWidget(GEM_ICON, String.valueOf(diamonds), 130, 42));

        Table topRight = new Table();
        topRight.add(currencyRow).right().row();
        topRight.add(createResourceWidget(SEED_PACKET_BG, SEED_PACKET_ICON, String.valueOf(seedPackets), 130, 55)).padTop(25).padBottom(-45).padRight(8).right();

        Table topBar = new Table();
        topBar.add(topLeft).left().expandX().padBottom(-110);
        topBar.add(topRight).right().padBottom(-120);
        return topBar;
    }

    private int totalSeedPackets(User user) {
        if (user == null || user.userState == null || user.userState.seedPacketInventory == null) {
            return 0;
        }
        return user.userState.seedPacketInventory.values().stream().mapToInt(Integer::intValue).sum();
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

    private Table createResourceWidget(String bgPath, String value, float width, float height) {
        Stack stack = new Stack();
        Table bgTable = new Table();
        bgTable.setBackground(new TextureRegionDrawable(loadTextureSafe(bgPath)));

        Table textTable = new Table();
        textTable.add(new Label(value, skin, "title")).center().expand();

        stack.add(bgTable);
        stack.add(textTable);

        Table outer = new Table();
        outer.add(stack).size(width, height);
        return outer;
    }

    private Table createResourceWidget(String bgPath, String iconPath, String value, float width, float height) {
        float bgWidth = 100;
        float bgHeight = 30;
        float groupWidth = Math.max(width, bgWidth);
        float groupHeight = Math.max(height, bgHeight);

        Group group = new Group();
        group.setSize(groupWidth, groupHeight);

        Image bgImage = new Image(loadTextureSafe(bgPath));
        bgImage.setSize(bgWidth, bgHeight);
        bgImage.setPosition(((groupWidth - bgWidth) / 2f) + 20, (groupHeight - bgHeight) / 2f);
        group.addActor(bgImage);

        Table textTable = new Table();
        Image iconImage = new Image(loadTextureSafe(iconPath));
        iconImage.setSize(55,85);
        textTable.add(iconImage).left().expand();
        textTable.setSize(width, height);
        textTable.setPosition((groupWidth - width) / 2f, (groupHeight - height) / 2f);
        textTable.add(new Label(value, skin, "title")).center().expand();
        group.addActor(textTable);

        Table outer = new Table();
        outer.add(group).size(groupWidth, groupHeight);
        return outer;
    }

    private class PlantIdleAnimationActor extends Actor {
        private final String animationPath;
        private final float canvasWidth;
        private final float canvasHeight;
        private final float targetSize;
        private float stateTime = 0f;

        PlantIdleAnimationActor(String animationPath, float canvasWidth, float canvasHeight, float targetSize) {
            this.animationPath = animationPath;
            this.canvasWidth = canvasWidth > 0 ? canvasWidth : targetSize;
            this.canvasHeight = canvasHeight > 0 ? canvasHeight : targetSize;
            this.targetSize = targetSize;

            setSize(targetSize, targetSize);
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
                if (clipName == null) return;

                ClipRef clip = pamPlayer.getClip(animationPath, clipName);
                if (clip == null) return;

                float scale = (targetSize / Math.max(canvasWidth, canvasHeight)) * 2.35f;
                float px = getX();
                float py = getY();

                
                float pivotX = px + (targetSize / 2f) + 57;
                float pivotY = py + (targetSize * 0.25f) + 125;

                Matrix4 original = batch.getTransformMatrix().cpy();
                Matrix4 scaled = new Matrix4(original)
                        .translate(pivotX, pivotY, 0)
                        .scale(scale, scale, 1f)
                        .translate(-pivotX, -pivotY, 0);
                batch.setTransformMatrix(scaled);

                pamPlayer.draw(batch, clip, stateTime, px, py, true);

                batch.setTransformMatrix(original);
            } catch (Throwable t) {
                Gdx.app.error("GreenhouseScreen", "Failed to draw idle animation", t);
            }
        }
    }

    @Override
    protected void onAfterCommand() {
        build();
    }
}