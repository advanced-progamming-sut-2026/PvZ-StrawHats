package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.greenhouse.shop.Product;
import model.greenhouse.shop.Shop;
import model.user_data.User;
import model.user_data.UserState;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;
import view.general_screens.UiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopScreen extends UiScreen {

    private static final Shop STORE = new Shop();

    private static final float TAB_W = 200f;
    private static final float TAB_H = 55f;
    private static final float TAB_SPACING = 20f;
    private static final float BOARD_PAD_LEFT = 40f;
    private static final float ARROW_W = 36f;
    private static final float ARROW_H = 26f;

    private enum ShopTab { DAILY, ITEMS }

    private ShopTab currentTab = ShopTab.DAILY;

    @Override
    public void show() {
        setBackground("assets/images/backg/mainmenu_background.png");
        AudioManager.get().playMusic(AudioEnum.MENU_MUSIC, true);
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();

        UserState state = User.currentUser.userState;
        STORE.refreshDailyOffer(state);

        rootTable.add(buildTopBar()).fillX().padTop(5).padLeft(15).padRight(15).row();

        Table centerTable = new Table();
        Label title = new Label("Shop", skin, "title");
        title.setFontScale(1.6f);
        centerTable.add(title).padBottom(SPACE_MD).row();
        centerTable.add(buildTabbedBoard(state));

        rootTable.add(centerTable).expand().center().row();
    }

    private Stack buildTabbedBoard(UserState state) {
        TextureRegionDrawable woodBoardBg = createTextureDrawable("assets/images/backg/wood board.png");
        TextureRegionDrawable tabBg = createTextureDrawable("assets/images/ui/zombies_active.png");
        TextureRegionDrawable arrowDown = createTextureDrawable("assets/images/ui/zombies_active.png");

        float arrowPadTop = TAB_H - 8f;
        float padLeftArrow1 = BOARD_PAD_LEFT + (TAB_W / 2f) - (ARROW_W / 2f);
        float padLeftArrow2 = TAB_SPACING + TAB_W - ARROW_W;

        Table inArrows = new Table();
        inArrows.top().left();
        Image arrowDailyIn = new Image(arrowDown);
        Image arrowItemsIn = new Image(arrowDown);
        arrowDailyIn.setVisible(currentTab != ShopTab.DAILY);
        arrowItemsIn.setVisible(currentTab != ShopTab.ITEMS);
        inArrows.add(arrowDailyIn).size(ARROW_W, ARROW_H).padTop(arrowPadTop).padLeft(padLeftArrow1);
        inArrows.add(arrowItemsIn).size(ARROW_W, ARROW_H).padTop(arrowPadTop).padLeft(padLeftArrow2);

        Table woodLayer = new Table();
        woodLayer.padTop(TAB_H - 12f);
        Table boardWrapper = new Table();
        boardWrapper.setBackground(woodBoardBg);
        boardWrapper.pad(40);
        woodLayer.add(boardWrapper).width(780).height(480);

        Table outArrows = new Table();
        outArrows.top().left();
        Image arrowDailyAct = new Image(arrowDown);
        Image arrowItemsAct = new Image(arrowDown);
        arrowDailyAct.setVisible(currentTab == ShopTab.DAILY);
        arrowItemsAct.setVisible(currentTab == ShopTab.ITEMS);
        outArrows.add(arrowDailyAct).size(ARROW_W, ARROW_H).padTop(arrowPadTop).padLeft(padLeftArrow1);
        outArrows.add(arrowItemsAct).size(ARROW_W, ARROW_H).padTop(arrowPadTop).padLeft(padLeftArrow2);

        Table tabsLayer = new Table();
        tabsLayer.top().left();
        tabsLayer.padTop(0).padLeft(BOARD_PAD_LEFT);

        Table dailyTab = new Table();
        dailyTab.setBackground(tabBg);
        Label dailyLabel = new Label("Daily Offer", skin, "title");
        dailyLabel.setFontScale(0.85f);
        dailyTab.add(dailyLabel).center();
        dailyTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentTab = ShopTab.DAILY;
                build();
            }
        });

        Table itemsTab = new Table();
        itemsTab.setBackground(tabBg);
        Label itemsLabel = new Label("Shop Items", skin, "title");
        itemsLabel.setFontScale(0.85f);
        itemsTab.add(itemsLabel).center();
        itemsTab.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentTab = ShopTab.ITEMS;
                build();
            }
        });

        tabsLayer.add(dailyTab).size(TAB_W, TAB_H).padRight(TAB_SPACING);
        tabsLayer.add(itemsTab).size(TAB_W, TAB_H);

        Table list = currentTab == ShopTab.DAILY ? buildDailyOfferList(state) : buildItemsList(state);
        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        boardWrapper.add(scrollPane).expand().fill();

        Stack mainStack = new Stack();
        mainStack.add(inArrows);
        mainStack.add(woodLayer);
        mainStack.add(outArrows);
        mainStack.add(tabsLayer);
        return mainStack;
    }

    private Table buildDailyOfferList(UserState state) {
        Table list = new Table();
        list.top();
        list.add(buildDailyOfferCard(state)).width(680).row();
        return list;
    }

    private Table buildItemsList(UserState state) {
        Table list = new Table();
        list.top();
        for (Product product : Product.values()) {
            if (product == Product.DAILY_OFFER) continue;
            list.add(buildProductCard(product, state)).width(680).padBottom(SPACE_MD).row();
        }
        return list;
    }

    private Table buildDailyOfferCard(UserState state) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(SPACE_MD);

        Image icon = new Image(loadTextureSafe(""));
        card.add(icon).size(64, 64).padRight(SPACE_MD);

        Table info = new Table();
        info.left();

        String plantName = plantName(state.dailyOfferPlantId);
        Label nameLabel = new Label("Daily Offer: " + plantName, skin, "main");
        info.add(nameLabel).left().row();

        Label subLabel = new Label(state.dailyOfferPurchased
                ? "Already purchased today - come back tomorrow."
                : "10 seed packs for " + Product.DAILY_OFFER.getCoinCost() + " coins.",
                skin, "muted");
        subLabel.setWrap(true);
        info.add(subLabel).width(380).left().padTop(SPACE_XS);

        card.add(info).expandX().fillX().left();

        boolean canBuy = !state.dailyOfferPurchased && state.dailyOfferPlantId != null;
        TextButton buyBtn = primaryButton(state.dailyOfferPurchased ? "Bought" : "Buy",
                () -> buy("daily-offer", 1, null));
        buyBtn.setDisabled(!canBuy);
        card.add(buyBtn).width(120).height(46);

        return card;
    }

    private Table buildProductCard(Product product, UserState state) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(SPACE_MD);

        Image icon = new Image(loadTextureSafe(iconFor(product)));
        card.add(icon).size(56, 56).padRight(SPACE_MD);

        Table info = new Table();
        info.left();
        info.add(new Label(product.getDisplayName(), skin, "main")).left().row();
        info.add(new Label(costLabel(product), skin, "muted")).left().padTop(SPACE_XS).row();
        String note = extraNote(product, state);
        if (note != null) {
            Label noteLabel = new Label(note, skin, "muted");
            noteLabel.setWrap(true);
            info.add(noteLabel).width(360).left().padTop(SPACE_XS);
        }
        card.add(info).expandX().fillX().left();

        TextButton buyBtn = primaryButton("Buy", () -> onBuyClicked(product, state));
        card.add(buyBtn).width(120).height(46);

        return card;
    }

    private void onBuyClicked(Product product, UserState state) {
        if (product == Product.SEED_CHOICE) {
            new SeedChoiceModal(state, plantId -> buy(product.getItemId(), 1, plantId)).show();
        } else {
            buy(product.getItemId(), 1, null);
        }
    }

    private void buy(String itemId, int count, Integer plantTypeId) {
        String cmd = "shop buy -i " + itemId + " -n " + count
                + (plantTypeId != null ? " -t " + plantName(plantTypeId) : "");
        runCommand(cmd);
    }

    private String costLabel(Product product) {
        if (product.getCoinCost() > 0) return product.getCoinCost() + " coins";
        if (product.getDiamondCost() > 0) return product.getDiamondCost() + " diamonds";
        return "Free";
    }

    private String extraNote(Product product, UserState state) {
        if (product == Product.POT) {
            int total = Greenhouse.getRowCount() * Greenhouse.getColCount();
            return "Pots unlocked: " + countUnlockedPots() + " / " + total;
        }
        if (product == Product.PLANT_FOOD) {
            return "Holding: " + state.plantFoodCount + " / 3";
        }
        if (product == Product.SEED_CHOICE) {
            return "Choose which unlocked plant to buy seeds for.";
        }
        return null;
    }

    private int countUnlockedPots() {
        int count = 0;
        Pot[][] pots = Greenhouse.getInstance().getPots();
        for (Pot[] row : pots) {
            for (Pot pot : row) {
                if (pot != null && !pot.isLocked()) count++;
            }
        }
        return count;
    }

    private String iconFor(Product product) {
        return switch (product) {
            case POT -> "";
            case PLANT_FOOD -> "";
            case SEED_RANDOM -> "";
            case SEED_CHOICE -> "";
            case CURRENCY_EXCHANGE -> "assets/images/ui/buttons_premium_normal.png";
            case DAILY_OFFER -> "";
        };
    }

    private String plantName(Integer plantId) {
        if (plantId == null) return "none available";
        Map<Integer, PlantJsonParser.PlantConfig> blueprints = PlantFactory.getBlueprints();
        PlantJsonParser.PlantConfig config = blueprints.get(plantId);
        return config == null ? ("#" + plantId) : config.name;
    }

    private Table buildTopBar() {
        ImageButton backBtn = createIconButton("assets/images/ui/buttons_hud_back_normal.png", 54, 54,
                () -> runCommand("menu exit"));

        Table topLeft = new Table();
        topLeft.add(backBtn).padRight(16);
        topLeft.add(createIconButtonWithLabel("assets/images/ui/collection.png", 54, 54,
                "Collection", () -> runCommand("menu enter collection")));

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

    private Table createIconButtonWithLabel(String path, float width, float height, String text, Runnable action) {
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

    private TextureRegionDrawable createTextureDrawable(String path) {
        return new TextureRegionDrawable(loadTextureSafe(path));
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

    private class SeedChoiceModal extends view.general_screens.Modal {

        SeedChoiceModal(UserState state, java.util.function.Consumer<Integer> onPick) {
            content.add(new Label("Choose a Plant", skin, "title")).padBottom(SPACE_SM).row();

            List<Integer> unlockedIds = new ArrayList<>(state.unlockedPlantIds);
            Table list = new Table();
            if (unlockedIds.isEmpty()) {
                list.add(new Label("No unlocked plants yet.", skin, "muted"));
            } else {
                for (Integer plantId : unlockedIds) {
                    TextButton pick = secondaryButton(plantName(plantId), () -> {
                        hide();
                        onPick.accept(plantId);
                    });
                    list.add(pick).width(260).height(42).padBottom(SPACE_XS).row();
                }
            }
            content.add(scrollable(list)).width(280).height(220).row();

            TextButton cancel = secondaryButton("Cancel", this::hide);
            content.add(cancel).width(140).padTop(SPACE_SM);
        }
    }
}
