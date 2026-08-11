package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.menus.match.BeforeMenu;
import model.App;
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
import java.util.List;

/**
 * Graphical pre-match loadout screen.
 *
 * It intentionally uses the same SeedPacketCardFactory as CollectionScreen, so the
 * plant cards have exactly the same visual representation as the collection.
 * Selection itself remains owned by BeforeMenu.selectedPlants.
 */
public class BeforeMatchScreen extends UiScreen {

    private static final String BACKGROUND = "assets/images/backg/mainmenu_background.png";
    private static final String BOARD = "assets/images/backg/wood board.png";
    private static final String BACK = "assets/images/ui/buttons_hud_back_normal.png";

    private final SeedPacketCardFactory cardFactory = new SeedPacketCardFactory();
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
        List<String> names = new ArrayList<>();
        if (level.getAvailablePlants() != null) names.addAll(level.getAvailablePlants());

        if (names.isEmpty()) {
            // For normal levels the available list is the level's plant pool; if it is empty,
            // fall back to the player's unlocked collection so the screen remains usable.
            for (var config : new controller.CollectionManager().getUnlockedPlants(state)) {
                names.add(config.name);
            }
        }

        if (level instanceof ConveyorBeltLevel conveyor) {
            names.clear();
            if (conveyor.getConveyorPlants() != null) {
                for (var plant : conveyor.getConveyorPlants()) {
                    if (plant != null && plant.getName() != null) {
                        names.add(plant.getName());
                    }
                }
            }
        }

        names = new ArrayList<>(names.stream().distinct().toList());

        Table grid = new Table();
        grid.top();

        Table row = null;
        int index = 0;
        for (String name : names) {
            if (index % 4 == 0) {
                row = new Table();
                grid.add(row).center().padBottom(10).row();
            }
            boolean locked = isLocked(level, name);
            Actor card = buildCard(name, locked);
            row.add(card).pad(5);
            index++;
        }

        // The project's Skin is atlas-backed and does not register a ScrollPaneStyle.
        // Keep the existing plant-card mechanism, but render the grid directly so this
        // screen does not require an unavailable "default" ScrollPane style.
        Table gridContainer = new Table();
        gridContainer.top().left();
        gridContainer.add(grid).expandX().fillX().top().left();
        return gridContainer;
    }

    private boolean isLocked(Level level, String name) {
        if (level instanceof LockedPlantsLevel lockedLevel) {
            return lockedLevel.isPlantLocked(name);
        }
        return false;
    }

    private Actor buildCard(String name, boolean locked) {
        Table cell = new Table();
        try {
            SeedPacketCard card = cardFactory.buildCardForDisplayName(name);
            if (card == null) {
                return fallbackCard(name, locked);
            }
            cell.add(card).size(card.getWidth(), card.getHeight());
            cards.add(cell);

            cell.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    if (!locked) togglePlant(name);
                }
            });
        } catch (Throwable ignored) {
            return fallbackCard(name, locked);
        }

        cell.addAction(new com.badlogic.gdx.scenes.scene2d.Action() {
            @Override public boolean act(float delta) {
                cell.invalidateHierarchy();
                return true;
            }
        });

        return cell;
    }

    private Actor fallbackCard(String name, boolean locked) {
        Table cell = new Table();
        cell.setBackground(skin.getDrawable("card-background"));
        Label label = new Label((locked ? "LOCKED\n" : "") + name, skin, "main");
        label.setAlignment(Align.center);
        label.setWrap(true);
        cell.add(label).size(145, 150);
        if (!locked) {
            cell.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    togglePlant(name);
                }
            });
        }
        return cell;
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
