package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import controller.menus.match.BeforeMenu;
import model.App;
import model.collections.plant.PlantJsonParser;
import model.game_exceptions.GameException;
import model.match.main.levels.Level;
import model.match.main.levels.special_levels.ConveyorBeltLevel;
import model.match.main.levels.special_levels.LockedPlantsLevel;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;
import service.card_factory.SeedPacketCard;
import service.card_factory.SeedPacketCardFactory;
import view.general_screens.UiScreen;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BeforeMatchScreen extends UiScreen {

    private static final String BACKGROUND = "assets/images/backg/mainmenu_background.png";
    private static final String BOARD = "assets/images/backg/wood board.png";
    private static final String BACK = "assets/images/ui/buttons_hud_back_normal.png";
    private static final String LOCK_ICON = "assets/images/lock.png";

    private final SeedPacketCardFactory cardFactory = new SeedPacketCardFactory();
    private final CollectionManager collectionManager = new CollectionManager();
    private final List<Actor> cards = new ArrayList<>();

    @Override
    public void show() {
        super.show();
        build();
    }

    private void build() {
        rootTable.clear();
        rootTable.setBackground(new TextureRegionDrawable(loadTextureSafe(BACKGROUND)));

        Level level = GameSession.peekInstance() == null ? null : GameSession.peekInstance().getLevel();
        if (level == null) {
            rootTable.add(new Label("No level loaded.", skin, "title")).center();
            return;
        }

        Table outer = new Table();
        outer.setBackground(new TextureRegionDrawable(loadTextureSafe(BOARD)));
        outer.pad(18);

        Table header = new Table();
        TextButton back = new TextButton("Back", skin);
        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                runCommand("menu exit");
            }
        });
        header.add(back).left().width(120).height(52);
        header.add(new Label(level.getName(), skin, "title")).expandX().center();
        outer.add(header).fillX().row();

        String mode = level.getGameMode() == null ? "Adventure" : level.getGameMode();
        Label description = new Label(mode + "\nChoose up to 8 plants. Forced plants are added automatically.",
                skin, "main");
        description.setAlignment(Align.center);
        description.setWrap(true);
        outer.add(description).width(850).padTop(8).padBottom(12).row();

        if (level instanceof ConveyorBeltLevel) {
            outer.add(buildConveyorNotice()).fillX().padBottom(10).row();
        }

        Table content = new Table();
        content.add(buildPlantGrid(level)).expand().fill();
        content.add(buildLoadoutPanel(level)).width(275).padLeft(18).top();
        outer.add(content).expand().fill().row();

        Table bottom = new Table();
        TextButton start = new TextButton("Let's Rock!", skin);
        start.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                runCommand("start game");
            }
        });
        bottom.add(start).width(240).height(58);
        outer.add(bottom).padTop(12);

        rootTable.add(outer).expand().fill();
    }

    private Table buildConveyorNotice() {
        Table t = new Table();
        t.setBackground(skin.getDrawable("card-background"));
        Label l = new Label("CONVEYOR BELT\nPlants arrive during the match; manual loadout is not required.",
                skin, "main");
        l.setAlignment(Align.center);
        l.setWrap(true);
        t.add(l).expandX().fillX().pad(8);
        return t;
    }

    private Actor buildPlantGrid(Level level) {
        UserState state = User.currentUser.userState;

        if (level instanceof ConveyorBeltLevel conveyor) {
            List<String> names = new ArrayList<>();
            if (conveyor.getConveyorPlants() != null) {
                for (var plant : conveyor.getConveyorPlants()) {
                    if (plant != null && plant.getName() != null) {
                        names.add(plant.getName());
                    }
                }
            }
            return buildGridContainer(names, name -> false, name -> false);
        }

        Set<String> availableNames = new LinkedHashSet<>();
        if (level.getAvailablePlants() != null) availableNames.addAll(level.getAvailablePlants());

        List<PlantJsonParser.PlantConfig> allPlants = collectionManager.getAllPlants();
        List<String> names = new ArrayList<>();
        for (PlantJsonParser.PlantConfig config : allPlants) {
            names.add(config.name);
        }

        boolean restrictToAvailable = !availableNames.isEmpty();

        return buildGridContainer(names,
                name -> !state.isPlantUnlocked(findId(allPlants, name))
                        || isLevelLocked(level, name)
                        || (restrictToAvailable && !containsIgnoreCase(availableNames, name)),
                name -> !state.isPlantUnlocked(findId(allPlants, name)));
    }

    private int findId(List<PlantJsonParser.PlantConfig> plants, String name) {
        for (PlantJsonParser.PlantConfig config : plants) {
            if (config.name.equalsIgnoreCase(name)) return config.id;
        }
        return -1;
    }

    private boolean containsIgnoreCase(Set<String> values, String target) {
        for (String value : values) {
            if (value.equalsIgnoreCase(target)) return true;
        }
        return false;
    }

    private Actor buildGridContainer(List<String> names, java.util.function.Predicate<String> darkened,
                                      java.util.function.Predicate<String> showLockIcon) {
        Table grid = new Table();
        grid.top();

        Table row = null;
        int index = 0;
        for (String name : names) {
            if (index % 5 == 0) {
                row = new Table();
                grid.add(row).center().padBottom(SPACE_MD).row();
            }
            boolean isDarkened = darkened.test(name);
            Actor card = buildCard(name, isDarkened, showLockIcon.test(name));
            row.add(card).pad(SPACE_SM);
            index++;
        }

        Table gridContainer = new Table();
        gridContainer.top().left();
        gridContainer.add(grid).expandX().fillX().top().left();
        return gridContainer;
    }

    private boolean isLevelLocked(Level level, String name) {
        if (level instanceof LockedPlantsLevel lockedLevel) {
            return lockedLevel.isPlantLocked(name);
        }
        return false;
    }

    private Actor buildCard(String name, boolean darkened, boolean showLockIcon) {
        Stack cardStack = new Stack();
        float cardW = 150f;
        float cardH = 190f;

        try {
            SeedPacketCard card = cardFactory.buildCardForDisplayName(name);
            if (card != null) {
                cardW = card.getWidth();
                cardH = card.getHeight();
                cardStack.add(card);
            }
        } catch (Throwable ignored) {
        }
        cardStack.setSize(cardW, cardH);

        if (cardStack.getChildren().isEmpty()) {
            Table fallback = new Table();
            fallback.setBackground(skin.getDrawable("card-background"));
            Label label = new Label(name, skin, "main");
            label.setAlignment(Align.center);
            label.setWrap(true);
            fallback.add(label).size(cardW, cardH);
            cardStack.add(fallback);
        }

        if (darkened) {
            Image dim = new Image(solidColorDrawable(new Color(0f, 0f, 0f, 0.55f)));
            cardStack.add(dim);
        }
        if (showLockIcon) {
            Image lockImage = new Image(loadTextureSafe(LOCK_ICON));
            Container<Image> lockContainer = new Container<>(lockImage);
            lockContainer.size(56f, 56f);
            lockContainer.align(Align.center);
            cardStack.add(lockContainer);
        }

        Table cell = new Table();
        cell.add(cardStack).size(cardW, cardH);
        cards.add(cell);

        if (!darkened) {
            cardStack.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    togglePlant(name);
                }
            });
        }

        return cell;
    }

    private Drawable solidColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Table buildLoadoutPanel(Level level) {
        Table panel = new Table();
        panel.setBackground(skin.getDrawable("card-background"));
        panel.pad(12).top();

        Label title = new Label("YOUR LOADOUT", skin, "title");
        panel.add(title).center().row();

        Table slots = new Table();
        for (int i = 0; i < 8; i++) {
            String name = i < BeforeMenu.selectedPlants.size()
                    ? BeforeMenu.selectedPlants.get(i) : "Empty";
            TextButton slot = new TextButton(name, skin);
            final String selected = name;
            if (!"Empty".equals(name)) {
                slot.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event, float x, float y) {
                        removePlant(selected);
                    }
                });
            }
            slots.add(slot).width(225).height(42).pad(3).row();
        }
        panel.add(slots).padTop(8).row();

        int forcedCount = level.getForcedPlants() == null ? 0 : level.getForcedPlants().size();
        Label info = new Label("Selected: " + BeforeMenu.selectedPlants.size() + "/8\nForced: " + forcedCount,
                skin, "main");
        info.setAlignment(Align.center);
        panel.add(info).padTop(8).row();

        if (level instanceof LockedPlantsLevel locked) {
            String lockedText = locked.getLockedPlants() == null ? "none" : locked.getLockedPlants().toString();
            Label locks = new Label("Locked plants:\n" + lockedText, skin, "muted");
            locks.setWrap(true);
            panel.add(locks).width(235).padTop(12);
        }

        return panel;
    }

    private void togglePlant(String name) {
        if (BeforeMenu.selectedPlants.stream().anyMatch(p -> p.equalsIgnoreCase(name))) {
            removePlant(name);
            return;
        }
        runCommand("add plant -t " + name);
        build();
    }

    private void removePlant(String name) {
        runCommand("remove plant -t " + name);
        build();
    }

    @Override
    protected void onAfterCommand() {
        build();
    }

    @Override
    public void dispose() {
        cardFactory.dispose();
        super.dispose();
    }
}
