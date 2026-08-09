package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
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

import static view.screens.GreenhouseScreen.SEED_PACKET_BG;
import static view.screens.GreenhouseScreen.SEED_PACKET_ICON;

public class ShopScreen extends UiScreen {

    private static final Shop STORE = new Shop();


    private static final String TAB_ICON_PLANTS = "assets/images/shop/event_icon_potw_down.png";
    private static final String TAB_ICON_CURRENCY = "assets/images/shop/moneybag.png";
    private static final String INFO_ICON = "assets/images/shop/info_icon.png";

    private static final float TAB_ICON_SIZE = 46f;

    private static final float CARD_W = 200f;
    private static final float CARD_H = 300f;
    private static final float CARD_IMAGE_SIZE = 130f;

    private static final float DAILY_BOX_W = 260f;
    private static final float ITEMS_BOX_W = 700f;
    private static final float PANEL_H = 380f;

    private enum ShopTab { PLANTS, CURRENCY }

    private ShopTab currentTab = ShopTab.PLANTS;

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

        centerTable.add(buildCategoryTabs()).left().padBottom(SPACE_SM).row();

        Table board = new Table();
        board.setBackground(skin.getDrawable("card-background"));
        board.pad(SPACE_MD);
        board.add(buildDailyOfferBox(state)).width(DAILY_BOX_W).height(PANEL_H).top().padRight(SPACE_MD);
        board.add(buildItemsBox(state)).width(ITEMS_BOX_W).height(PANEL_H).top();
        centerTable.add(board);

        rootTable.add(centerTable).expand().center().row();
    }

    private Table buildCategoryTabs() {
        Table tabs = new Table();
        tabs.add(buildCategoryTab(TAB_ICON_PLANTS, "Plants", ShopTab.PLANTS)).padRight(SPACE_MD);
        tabs.add(buildCategoryTab(TAB_ICON_CURRENCY, "Coins & Gems", ShopTab.CURRENCY));
        return tabs;
    }

    private Table buildCategoryTab(String iconPath, String label, ShopTab tab) {
        Table container = new Table();
        if (tab == currentTab) {
            container.setBackground(skin.getDrawable("card-background"));
        }
        container.pad(SPACE_XS, SPACE_SM, SPACE_XS, SPACE_SM);

        Image icon = new Image(loadTextureSafe(iconPath));
        container.add(icon).size(TAB_ICON_SIZE, TAB_ICON_SIZE).row();

        Label tabLabel = new Label(label, skin, tab == currentTab ? "title" : "muted");
        tabLabel.setFontScale(0.8f);
        container.add(tabLabel).padTop(2);

        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentTab = tab;
                build();
            }
        });
        return container;
    }

    private Table buildDailyOfferBox(UserState state) {
        Table box = new Table();
        box.top();

        Label header = new Label("Daily Offer", skin, "title");
        header.setFontScale(0.9f);
        box.add(header).left().padBottom(SPACE_SM).row();

        Table list = new Table();
        list.top();
        list.add(buildDailyOfferCard(state)).width(CARD_W).row();

        ScrollPane scrollPane = scrollable(list);
        box.add(scrollPane).expand().fill();
        return box;
    }

    private Table buildItemsBox(UserState state) {
        Table box = new Table();
        box.top();

        String header = currentTab == ShopTab.PLANTS ? "Plants" : "Coins & Gems";
        Label headerLabel = new Label(header, skin, "title");
        headerLabel.setFontScale(0.9f);
        box.add(headerLabel).left().padBottom(SPACE_SM).row();

        Table row = new Table();
        row.left();
        for (Product product : productsForTab(currentTab)) {
            row.add(buildItemCard(product, state)).width(CARD_W).height(CARD_H).padRight(SPACE_MD);
        }

        ScrollPane scrollPane = new ScrollPane(row);
        scrollPane.setScrollingDisabled(false, true);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);
        box.add(scrollPane).expand().fill();
        return box;
    }

    private List<Product> productsForTab(ShopTab tab) {
        return switch (tab) {
            case PLANTS -> List.of(Product.SEED_RANDOM, Product.SEED_CHOICE);
            case CURRENCY -> List.of(Product.POT, Product.PLANT_FOOD, Product.CURRENCY_EXCHANGE);
        };
    }

    private Table buildDailyOfferCard(UserState state) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(SPACE_SM);
        card.top();

        Table headerRow = new Table();
        String plantName = plantName(state.dailyOfferPlantId);
        Label nameLabel = new Label(plantName, skin, "main");
        nameLabel.setFontScale(0.85f);
        nameLabel.setWrap(true);
        headerRow.add(nameLabel).expandX().left().width(CARD_W - 60f);
        headerRow.add(new Image(loadTextureSafe(INFO_ICON))).size(22, 22).right();
        card.add(headerRow).fillX().row();

        Image icon = new Image(loadTextureSafe(""));
        card.add(icon).size(CARD_IMAGE_SIZE, CARD_IMAGE_SIZE).padTop(SPACE_SM).padBottom(SPACE_SM).row();

        Label subLabel = new Label(state.dailyOfferPurchased
                ? "Already purchased today - come back tomorrow."
                : "10 seed packs, once a day.",
                skin, "muted");
        subLabel.setFontScale(0.85f);
        subLabel.setWrap(true);
        card.add(subLabel).width(CARD_W - 40f).padBottom(SPACE_SM).row();

        boolean canBuy = !state.dailyOfferPurchased && state.dailyOfferPlantId != null;
        TextButton buyBtn = primaryButton(state.dailyOfferPurchased
                ? "Bought" : Product.DAILY_OFFER.getCoinCost() + " coins", () -> buy("daily-offer", 1, null));
        buyBtn.setDisabled(!canBuy);
        card.add(buyBtn).width(150).height(44);

        return card;
    }

    private Table buildItemCard(Product product, UserState state) {
        Table card = new Table();
        card.setBackground(skin.getDrawable("card-background"));
        card.pad(SPACE_SM);
        card.top();

        Table headerRow = new Table();
        Label nameLabel = new Label(product.getDisplayName(), skin, "main");
        nameLabel.setFontScale(0.85f);
        nameLabel.setWrap(true);
        headerRow.add(nameLabel).expandX().left().width(CARD_W - 60f);
        headerRow.add(new Image(loadTextureSafe(INFO_ICON))).size(22, 22).right();
        card.add(headerRow).fillX().row();

        Image icon = new Image(loadTextureSafe(iconFor(product)));
        card.add(icon).size(CARD_IMAGE_SIZE, CARD_IMAGE_SIZE).padTop(SPACE_SM).padBottom(SPACE_SM).row();

        String note = extraNote(product, state);
        if (note != null) {
            Label noteLabel = new Label(note, skin, "muted");
            noteLabel.setFontScale(0.85f);
            noteLabel.setWrap(true);
            card.add(noteLabel).width(CARD_W - 40f).padBottom(SPACE_SM).row();
        } else {
            card.add().expandY().row();
        }

        TextButton buyBtn = primaryButton(costLabel(product), () -> onBuyClicked(product, state));
        card.add(buyBtn).width(150).height(44);

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
        int seedPackets = totalSeedPackets(user);

        Table currencyRow = new Table();
        currencyRow.add(createResourceWidget("assets/images/ui/buttons_coin_buy_normal.png",
                String.valueOf(coins))).padRight(15);
        currencyRow.add(createResourceWidget("assets/images/ui/buttons_premium_normal.png",
                String.valueOf(diamonds)));

        Table topRight = new Table();
        topRight.add(currencyRow).right().row();
        topRight.add(createResourceWidget(SEED_PACKET_BG, SEED_PACKET_ICON, String.valueOf(seedPackets), 130, 55))
                .padTop(25).padBottom(-45).padRight(8).right();

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
