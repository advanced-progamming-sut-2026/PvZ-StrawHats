package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.greenhouse.PotPlant;
import model.user_data.User;
import model.user_data.UserState;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.Toast;
import view.general_screens.UiScreen;

public class GreenhouseScreen extends UiScreen {

    private static final String BACK_ICON = "assets/images/ui/buttons_hud_back_normal.png";
    private static final String COLLECTION_ICON = "assets/images/ui/collection.png";
    private static final String COIN_ICON = "assets/images/ui/buttons_coin_buy_normal.png";
    private static final String GEM_ICON = "assets/images/ui/buttons_premium_normal.png";
    static final String SEED_PACKET_BG = "assets/images/ui/seedpacket_bg.png";
    static final String SEED_PACKET_ICON = "assets/images/ui/seedpacket.png";

    private static final String GREENHOUSE_BACKGROUND = "assets/images/backg/mainmenu_background.png";
    private static final String POT_EMPTY_ICON = "";
    private static final String POT_LOCKED_ICON = "";
    private static final String POT_GROWING_ICON = "";
    private static final String POT_MARIGOLD_ICON = "";
    private static final String SPARKLE_ICON = "";
    private static final String LOCK_ICON = "assets/images/greenhouse/lock_medium.png";

    private static final float POT_SIZE = 92f;

    private Label statusLabel;

    @Override
    public void show() {
        setBackground(GREENHOUSE_BACKGROUND);
        AudioManager.get().playMusic(AudioEnum.MENU_MUSIC, true);
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();
        rootTable.add(new Label("Greenhouse", skin, "title")).padTop(SPACE_MD).row();

        Greenhouse greenhouse = Greenhouse.getInstance();
        int total = Greenhouse.getRowCount() * Greenhouse.getColCount();
        statusLabel = new Label(greenhouse.countUnlockedPots() + " / " + total + " pots unlocked", skin, "muted");
        rootTable.add(statusLabel).padTop(SPACE_XS).row();

        rootTable.add(buildPotGrid(greenhouse)).expand().padTop(SPACE_MD).row();
        rootTable.add(buildBottomBar()).padBottom(SPACE_MD);
    }

    private Table buildPotGrid(Greenhouse greenhouse) {
        Table grid = new Table();
        for (int row = 1; row <= Greenhouse.getRowCount(); row++) {
            for (int col = 1; col <= Greenhouse.getColCount(); col++) {
                Pot pot = greenhouse.getPot(col, row);
                grid.add(buildPotCell(pot, col, row)).size(POT_SIZE + 30f, POT_SIZE + 42f).pad(SPACE_XS);
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

        Image circle = new Image(potCircleDrawable(status, plant));
        stack.add(circle);

        String iconPath = switch (status) {
            case LOCKED -> POT_LOCKED_ICON;
            case EMPTY -> POT_EMPTY_ICON;
            case GROWING, READY -> (plant != null && plant.isMarigold()) ? POT_MARIGOLD_ICON : POT_GROWING_ICON;
        };
        if (!iconPath.isEmpty() && Gdx.files.internal(iconPath).exists()) {
            stack.add(new Image(loadTextureSafe(iconPath)));
        } else {
            stack.add(new Label(fallbackGlyph(status, plant), skin, "title"));
        }

        if (status == PotStatus.LOCKED && !LOCK_ICON.isEmpty() && Gdx.files.internal(LOCK_ICON).exists()) {
            stack.add(new Image(loadTextureSafe(LOCK_ICON)));
        }

        if (status == PotStatus.READY) {
            Actor readyGlow;
            if (!SPARKLE_ICON.isEmpty() && Gdx.files.internal(SPARKLE_ICON).exists()) {
                readyGlow = new Image(loadTextureSafe(SPARKLE_ICON));
            } else {
                Label ready = new Label("READY", skin, "title");
                ready.setFontScale(0.55f);
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

        Label captionLabel = new Label(captionFor(status, plant), skin, status == PotStatus.LOCKED ? "muted" : "main");
        captionLabel.setAlignment(Align.center);
        captionLabel.setFontScale(0.7f);
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
            case READY -> plant.getPlantName();
            case GROWING -> plant.getPlantName() + "\n" + Greenhouse.formatDuration(plant.getRemainingSeconds());
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

    private com.badlogic.gdx.scenes.scene2d.utils.Drawable potCircleDrawable(PotStatus status, PotPlant plant) {
        Color fill = switch (status) {
            case LOCKED -> new Color(0.35f, 0.33f, 0.28f, 1f);
            case EMPTY -> new Color(0.55f, 0.40f, 0.24f, 1f);
            case READY -> new Color(0.90f, 0.75f, 0.20f, 1f);
            case GROWING -> (plant != null && plant.isMarigold())
                    ? new Color(0.85f, 0.55f, 0.15f, 1f)
                    : new Color(0.35f, 0.58f, 0.30f, 1f);
        };
        Color border = status == PotStatus.READY ? Color.WHITE : new Color(0.15f, 0.12f, 0.06f, 1f);
        return circleDrawable((int) POT_SIZE, fill, border, status == PotStatus.READY ? 5 : 3);
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
        float bgWidth = 110;
        float bgHeight = 30;
        float groupWidth = Math.max(width, bgWidth);
        float groupHeight = Math.max(height, bgHeight);

        Group group = new Group();
        group.setSize(groupWidth, groupHeight);

        Image bgImage = new Image(loadTextureSafe(bgPath));
        bgImage.setSize(bgWidth, bgHeight);
        bgImage.setPosition(((groupWidth - bgWidth) / 2f) + 10, (groupHeight - bgHeight) / 2f);
        group.addActor(bgImage);

        Table textTable = new Table();
        textTable.add(new Image(loadTextureSafe(iconPath))).left().expand();
        textTable.setSize(width, height);
        textTable.setPosition((groupWidth - width) / 2f, (groupHeight - height) / 2f);
        textTable.add(new Label(value, skin, "title")).center().expand();
        group.addActor(textTable);

        Table outer = new Table();
        outer.add(group).size(groupWidth, groupHeight);
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
