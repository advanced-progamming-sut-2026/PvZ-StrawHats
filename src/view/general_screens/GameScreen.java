package view.general_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import controller.assets.GameAssetManager;
import controller.menus.match.AfterMenu;
import controller.menus.match.BeforeMenu;
import controller.menus.match.MatchMenu;
import controller.menus.match.MeanwhileMenu;
import model.App;
import model.collections.item.GroundItem;
import model.collections.plant.Plant;
import model.collections.plant.PlantFactory;
import model.collections.plant.PlantJsonParser;
import model.collections.zombie.Zombie;
import model.game_exceptions.GameException;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.user_data.User;
import model.user_data.UserState;
import service.card_factory.SeedPacketCard;
import service.card_factory.SeedPacketCardFactory;
import model.match.main.levels.special_levels.BossLevel;
import model.match.main.levels.special_levels.ConveyorBeltLevel;
import model.match.main.levels.special_levels.DeadLineLevel;
import model.match.main.levels.special_levels.IntroductionLevel;
import model.match.main.levels.special_levels.LockedPlantsLevel;
import model.match.main.levels.special_levels.LoveYourPlantsLevel;
import model.match.main.levels.special_levels.NightOpsLevel;
import model.match.main.levels.special_levels.PlantWhatYouGetLevel;
import model.match.main.levels.special_levels.SaveOurSeedsLevel;
import model.match.main.levels.special_levels.TimedWarLevel;
import model.projectile.Projectile;
import model.projectile.zombie_projectile.ZombieProjectile;
import model.utils.GameSession;
import model.utils.GameSettings;
import service.GameClock;
import service.resource_manager.AudioManager;

public class GameScreen extends UiScreen {

    private static final float TILE_SIZE = 96f;

    private static final String SUN_ICON = "images/chapters/egypt/egypt_gameplay/sun.png";
    private static final String PLANT_FOOD_ICON = "images/chapters/egypt/egypt_gameplay/plantfood.png";
    private static final String COIN_ICON = "images/ui/buttons_coin_buy_normal.png";

    private static final String BOARD_BG_EGYPT = "images/chapters/egypt/egypt_gameplay/map.png";
    private static final String BOARD_BG_DEFAULT_DAY = "images/chapters/egypt/egypt_gameplay/map.png";
    private static final String BOARD_BG_DEFAULT_NIGHT = "images/chapters/egypt/egypt_gameplay/map.png";
    private static final String BOARD_BG_BEACH = "images/chapters/egypt/egypt_gameplay/map.png";
    private static final String BOARD_BG_FROSTBITE = "images/chapters/egypt/egypt_gameplay/map.png";
    private static final String BOARD_BG_DARK_AGES = "images/chapters/egypt/egypt_gameplay/map.png";

    private static final float SIDEBAR_CARD_WIDTH = 105f;

    private enum Tool { NONE, SHOVEL, FEED }

    private GameSession session;
    private OrthographicCamera boardCamera;
    private FitViewport boardViewport;
    private float boardWidth, boardHeight;

    private double tickAccumulator = 0;
    private boolean paused = false;
    private boolean matchFinished = false;

    private int hoverRow = -1, hoverCol = -1;
    private Tool armedTool = Tool.NONE;
    private String armedPlantName = null;

    private TextureRegion whitePixel;
    private BitmapFont font;
    private boolean ownsFont;
    private Texture boardBackgroundTexture;
    private Label specialInfoLabel;

    private final SeedPacketCardFactory seedCardFactory = new SeedPacketCardFactory();

    private Label sunAmountLabel;
    private Label plantFoodAmountLabel;
    private Label coinAmountLabel;
    private WaveProgressBar waveBar;
    private TextButton pauseButton;
    private TextButton shovelButton;
    private TextButton feedButton;
    private TextButton startWavesButton;
    private Table plantCardList;
    private final List<LoadoutEntry> loadoutEntries = new ArrayList<>();

    private record LoadoutEntry(String plantName, int plantId, int cost, Stack cardStack,
                                Label costLabel, Label cooldownLabel,
                                Image dimOverlay, Image selectionBorder) {
    }

    private record CurrencyWidget(Table widget, Label label) {
    }

    @Override
    public void initParticles() {
        particles = new ParticleCreator(
                new String[]{},
                18, 4, 9, 0.3f, false, 0f, 0f, boardWidth, boardHeight);
        rootStack.addActorAt(1, particles.createActor());
    }

    @Override
    public void show() {
        super.show();

        session = GameSession.getInstance();
        boardWidth = session.getCols() * TILE_SIZE;
        boardHeight = session.getRows() * TILE_SIZE;

        boardCamera = new OrthographicCamera();
        boardViewport = new FitViewport(boardWidth, boardHeight, boardCamera);
        boardCamera.position.set(boardWidth / 2f, boardHeight / 2f, 0);
        boardCamera.update();

        whitePixel = makeWhitePixel();
        font = new BitmapFont();
        ownsFont = true;

        String backgroundPath = resolveExistingAssetPath(resolveBoardBackgroundPath());
        if (Gdx.files.internal(backgroundPath).exists()) {
            boardBackgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
            boardBackgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        createHud();
        addBoardClickCatcher();
        initParticles();


        if (session.getLevel() != null) {
            Toast.show(stage, session.getLevel().getName() + " - " + session.getLevel().getGameMode());
        }
    }

    private static String resolveExistingAssetPath(String path) {
        if (path == null || Gdx.files.internal(path).exists() || !path.startsWith("assets/")) {
            return path;
        }
        String withoutPrefix = path.substring("assets/".length());
        return Gdx.files.internal(withoutPrefix).exists() ? withoutPrefix : path;
    }



    private void createHud() {
        CurrencyWidget sunWidget = buildCurrencyWidget(SUN_ICON);
        CurrencyWidget plantFoodWidget = buildCurrencyWidget(PLANT_FOOD_ICON);
        CurrencyWidget coinWidget = buildCurrencyWidget(COIN_ICON);
        sunAmountLabel = sunWidget.label();
        plantFoodAmountLabel = plantFoodWidget.label();
        coinAmountLabel = coinWidget.label();

        waveBar = new WaveProgressBar();
        specialInfoLabel = new Label("", skin, "main");
        specialInfoLabel.setAlignment(1);

        pauseButton = new TextButton("Pause", skin);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        shovelButton = new TextButton("Shovel", skin);
        shovelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                armTool(Tool.SHOVEL);
            }
        });

        feedButton = new TextButton("Plant Food", skin);
        feedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                armTool(Tool.FEED);
            }
        });

        startWavesButton = new TextButton("Let's Rock!", skin);
        startWavesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runCommand("start zombie waves");
            }
        });

        Table currencyRow = new Table();
        currencyRow.add(sunWidget.widget()).pad(4);
        currencyRow.add(plantFoodWidget.widget()).pad(4);
        currencyRow.add(coinWidget.widget()).pad(4);

        Table topBar = new Table();
        topBar.add(currencyRow).pad(4);
        topBar.add(waveBar).width(260).height(18).pad(8).expandX();
        topBar.add(specialInfoLabel).width(260).pad(8);
        topBar.add(startWavesButton).pad(8);
        topBar.add(pauseButton).pad(8);

        Table plantPanel = buildPlantCardPanel();

        rootTable.top();
        rootTable.add(topBar).colspan(2).expandX().fillX().top().row();
        rootTable.add(plantPanel).top().left().padLeft(10).padTop(8).expandY().fillY();
        rootTable.add().expand().fill();
        rootTable.row();
    }

    private CurrencyWidget buildCurrencyWidget(String iconPath) {
        Stack stack = new Stack();
        Image icon = new Image(loadTextureSafe(resolveExistingAssetPath(iconPath)));
        Label label = new Label("0", skin, "title");

        Table labelTable = new Table();
        labelTable.add(label).center().expand();

        stack.add(icon);
        stack.add(labelTable);

        Table widget = new Table();
        widget.add(stack).size(112, 46);
        return new CurrencyWidget(widget, label);
    }

    private Table buildPlantCardPanel() {
        Table panel = new Table();
        panel.setBackground(skin.getDrawable("card-background"));
        panel.pad(10).top();

        plantCardList = new Table();
        plantCardList.top();
        rebuildPlantCards();

        ScrollPane scrollPane = scrollable(plantCardList);
        panel.add(scrollPane).width(SIDEBAR_CARD_WIDTH + 24).expand().fill().row();

        panel.add(shovelButton).size(SIDEBAR_CARD_WIDTH + 24, 56).padTop(10).row();
        panel.add(feedButton).size(SIDEBAR_CARD_WIDTH + 24, 56).padTop(6);

        return panel;
    }

    private void rebuildPlantCards() {
        plantCardList.clear();
        loadoutEntries.clear();

        for (String plantName : BeforeMenu.selectedPlants) {
            PlantJsonParser.PlantConfig config = findPlantConfig(plantName);
            if (config == null) continue;

            Stack cardStack = new Stack();
            float cardW = 150f, cardH = 190f;
            try {
                SeedPacketCard card = seedCardFactory.buildCardForDisplayName(plantName);
                if (card != null) {
                    cardW = card.getWidth();
                    cardH = card.getHeight();
                    cardStack.add(card);
                }
            } catch (Throwable ignored) {
            }

            if (cardStack.getChildren().isEmpty()) {
                Table fallback = new Table();
                fallback.setBackground(skin.getDrawable("card-background"));
                Label label = new Label(plantName, skin, "main");
                label.setAlignment(Align.center);
                label.setWrap(true);
                fallback.add(label).center();
                cardStack.add(fallback);
            }

            float slotW = SIDEBAR_CARD_WIDTH;
            float slotH = cardH * (slotW / cardW);
            cardStack.setSize(slotW, slotH);

            Image dimOverlay = new Image(skin.getDrawable("modal-background"));
            dimOverlay.setColor(0f, 0f, 0f, 0.6f);
            dimOverlay.setVisible(false);
            cardStack.add(dimOverlay);

            Image selectionBorder = new Image(skin.getDrawable("card-background"));
            selectionBorder.setColor(1f, 0.85f, 0.2f, 0.35f);
            selectionBorder.setVisible(false);
            cardStack.add(selectionBorder);

            Label cooldownLabel = new Label("", skin, "title");
            Table cooldownWrap = new Table();
            cooldownWrap.add(cooldownLabel).expand().center();
            cardStack.add(cooldownWrap);

            Label costLabel = new Label(String.valueOf(config.cost), skin, "main");
            Table costRow = new Table();
            costRow.bottom();
            costRow.add(costLabel).padBottom(4);
            cardStack.add(costRow);

            cardStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {



                    event.stop();
                    if (dimOverlay.isVisible()) return;
                    armPlant(plantName);
                }
            });

            plantCardList.add(cardStack).size(slotW, slotH).pad(6).row();
            loadoutEntries.add(new LoadoutEntry(plantName, config.id, config.cost,
                    cardStack, costLabel, cooldownLabel, dimOverlay, selectionBorder));
        }
    }

    private PlantJsonParser.PlantConfig findPlantConfig(String plantName) {
        for (PlantJsonParser.PlantConfig config : PlantFactory.getBlueprints().values()) {
            if (config.name.equalsIgnoreCase(plantName)) return config;
        }
        return null;
    }

    private void refreshHud() {
        sunAmountLabel.setText(String.valueOf(session.getSunCount()));
        plantFoodAmountLabel.setText(String.valueOf(session.getPlantFoodCount()));
        UserState userState = User.currentUser != null ? User.currentUser.userState : null;
        coinAmountLabel.setText(String.valueOf(userState != null ? userState.coins : 0));

        int totalWaves = Math.max(1, session.getTotalWaveCount());
        waveBar.setProgress((float) session.getWavesSpawnedCount() / totalWaves, session.getTotalWaveCount());
        startWavesButton.setVisible(!session.isWavesStarted());

        for (LoadoutEntry entry : loadoutEntries) {
            boolean ready = session.isPlantReady(entry.plantId());
            boolean affordable = session.getSunCount() >= entry.cost();
            entry.cooldownLabel().setText(ready ? "" : String.format("%.1fs", session.getPlantCooldown(entry.plantId())));
            entry.dimOverlay().setVisible(!ready || !affordable);
            entry.selectionBorder().setVisible(entry.plantName().equals(armedPlantName));
            entry.costLabel().setColor(affordable ? Color.WHITE : new Color(0.9f, 0.2f, 0.2f, 1f));
        }
        shovelButton.setChecked(armedTool == Tool.SHOVEL);
        feedButton.setChecked(armedTool == Tool.FEED);
        feedButton.setDisabled(session.getPlantFoodCount() <= 0);
        refreshSpecialHud();
    }

    private void armPlant(String plantName) {
        armedPlantName = plantName;
        armedTool = Tool.NONE;
    }

    private void armTool(Tool tool) {
        armedTool = (armedTool == tool) ? Tool.NONE : tool;
        armedPlantName = null;
    }

    private void togglePause() {
        if (paused) return;
        paused = true;
        new PauseModal().show();
    }



    private void addBoardClickCatcher() {
        rootStack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBoardClick();
            }
        });
    }

    private Vector2 unprojectMouse() {
        Vector3 tmp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        boardViewport.unproject(tmp);
        return new Vector2(tmp.x, tmp.y);
    }

    private void updateHoverAndCollection() {
        Vector2 world = unprojectMouse();
        int col = (int) Math.floor(world.x / TILE_SIZE);
        int row = (int) Math.floor((boardHeight - world.y) / TILE_SIZE);
        boolean inBounds = row >= 0 && row < session.getRows() && col >= 0 && col < session.getCols();
        hoverRow = inBounds ? row : -1;
        hoverCol = inBounds ? col : -1;

        if (inBounds) {
            double continuousCol = world.x / TILE_SIZE;
            double continuousRow = (boardHeight - world.y) / TILE_SIZE;
            session.collectItemsNear(new Position(continuousCol, continuousRow));
        }
    }

    private void handleBoardClick() {
        if (paused || matchFinished || hoverRow < 0 || hoverCol < 0) return;
        int x = hoverCol + 1, y = hoverRow + 1;
        String command;
        if (armedPlantName != null) {
            command = "plant plant -t " + armedPlantName + " -l (" + x + ", " + y + ")";
        } else if (armedTool == Tool.SHOVEL) {
            command = "pluck plant -l (" + x + ", " + y + ")";
        } else if (armedTool == Tool.FEED) {
            command = "feed plant -l (" + x + ", " + y + ")";
        } else {
            return;
        }

        if (runCommand(command)) {
            armedPlantName = null;
            armedTool = Tool.NONE;
        }
    }

    public boolean runCommand(String command) {
        try {
            App.currentMenu.handleCommand(command);
            return true;
        } catch (GameException e) {
            Toast.show(stage, e.getMessage());
            return false;
        } catch (Exception e) {
            
            
            Gdx.app.error("GameScreen", "Uncaught exception running command: " + command, e);
            Toast.show(stage, "Something went wrong (" +
                    (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()) + ").");
            return false;
        }
    }



    @Override
    public void render(float delta) {
        if (!paused && !matchFinished) {
            tickAccumulator += delta * GameSettings.get().getGameSpeed();
            try {
                while (tickAccumulator >= GameClock.SECONDS_PER_TICK) {
                    session.tick();
                    tickAccumulator -= GameClock.SECONDS_PER_TICK;
                    checkMatchEnd();
                    if (matchFinished) {
                        tickAccumulator = 0;
                        break;
                    }
                }
            } catch (Exception e) {

                Gdx.app.error("GameScreen", "Uncaught exception during session.tick()", e);
                Toast.show(stage, "Something went wrong during the match ("
                        + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
                        + "). The match has been paused - please save & exit.");
                paused = true;
                tickAccumulator = 0;
            }
            updateHoverAndCollection();
        }

        refreshHud();

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBoard();
        AudioManager.get().update(delta);

        super.render(delta);
    }

    private void refreshSpecialHud() {
        if (specialInfoLabel == null || session == null || session.getLevel() == null) return;
        var level = session.getLevel();

        String text = "";
        if (level instanceof TimedWarLevel timed) {
            double remaining = timed.getTimeLimit() == null ? 0 : timed.getTimeLimit().getSecondsRemaining();
            text = String.format("TIME %.1fs | %d/%d ZOMBIES",
                    Math.max(0, remaining), timed.getZombiesKilledSoFar(), timed.getZombiesToKill());
        } else if (level instanceof LoveYourPlantsLevel love) {
            text = "PLANTS LOST " + love.getPlantsLost() + "/" + love.getMaxPlantLoss();
        } else if (level instanceof ConveyorBeltLevel conveyor) {
            text = conveyor.getCurrentPlant() == null
                    ? "CONVEYOR: WAITING"
                    : "CONVEYOR: " + conveyor.getCurrentPlant().getName();
        } else if (level instanceof LockedPlantsLevel) {
            text = "LOCKED PLANTS";
        } else if (level instanceof SaveOurSeedsLevel) {
            text = "PROTECT THE SEEDS";
        } else if (level instanceof DeadLineLevel) {
            text = "DO NOT CROSS THE LINE";
        } else if (level instanceof BossLevel) {
            text = "BOSS LEVEL";
        } else if (level instanceof PlantWhatYouGetLevel) {
            text = "PLANT WHAT YOU GET";
        } else if (level instanceof NightOpsLevel) {
            text = "NIGHT OPS";
        } else if (level instanceof IntroductionLevel) {
            text = "TUTORIAL";
        }
        specialInfoLabel.setText(text);
    }

    private void checkMatchEnd() {
        if (matchFinished) return;
        if (session.isGameOver()) {
            matchFinished = true;
            runCommand("end game -r lose");
            new EndMatchModal(false).show();
        } else if (session.isGameWon()) {
            matchFinished = true;
            runCommand("end game -r win");
            new EndMatchModal(true).show();
        }
    }



    private void drawBoard() {
        boardCamera.update();
        batch.setProjectionMatrix(boardCamera.combined);
        batch.begin();

        drawBackground();
        drawSeasonEffects();
        drawSpecialLevelBoardEffects();
        if (GameSettings.get().isShowGrid()) drawGridLines();
        drawHoverHighlight();
        drawGroundItems();
        drawPlantsAndZombiesSorted();
        drawProjectiles();
        drawLawnMowers();

        batch.end();
    }

    private void drawBackground() {
        if (boardBackgroundTexture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(boardBackgroundTexture, 0, 0, boardWidth, boardHeight);
            return;
        }

        boolean night = session.getLevel() != null && session.getLevel().getSeason() != null
                && session.getLevel().getSeason().isNight();
        Color base = night ? new Color(0.12f, 0.16f, 0.24f, 1f) : new Color(0.36f, 0.56f, 0.27f, 1f);
        batch.setColor(base);
        batch.draw(whitePixel, 0, 0, boardWidth, boardHeight);
        batch.setColor(Color.WHITE);
    }

    private boolean isEgyptLevel() {
        return session != null
                && session.getLevel() != null
                && session.getLevel().getSeason() != null
                && "Egypt".equalsIgnoreCase(session.getLevel().getSeason().getName());
    }

    private String resolveBoardBackgroundPath() {
        boolean night = session.getLevel() != null && session.getLevel().getSeason() != null
                && session.getLevel().getSeason().isNight();
        String seasonName = session.getLevel() != null && session.getLevel().getSeason() != null
                ? session.getLevel().getSeason().getName() : null;

        if (seasonName != null) {
            if (seasonName.equalsIgnoreCase("Egypt")) return BOARD_BG_EGYPT;
            if (seasonName.equalsIgnoreCase("Big Wave Beach")) return BOARD_BG_BEACH;
            if (seasonName.equalsIgnoreCase("Frostbite Caves")) return BOARD_BG_FROSTBITE;
            if (seasonName.equalsIgnoreCase("Dark Ages")) return BOARD_BG_DARK_AGES;
        }
        return night ? BOARD_BG_DEFAULT_NIGHT : BOARD_BG_DEFAULT_DAY;
    }

    private void drawSeasonEffects() {
        if (session.getLevel() == null || session.getLevel().getSeason() == null) return;

        if (isEgyptLevel() && session.isWavesStarted()
                && session.getTotalWaveCount() > 0
                && session.getWavesSpawnedCount() >= session.getTotalWaveCount()) {
            float alpha = 0.10f + 0.07f * (float) Math.sin(System.nanoTime() / 180_000_000.0);
            batch.setColor(0.78f, 0.60f, 0.28f, alpha);
            for (int i = 0; i < 7; i++) {
                float x = (float) ((System.nanoTime() / 12_000_000L + i * 170) % (boardWidth + 220)) - 220;
                batch.draw(whitePixel, x, 0, 180, boardHeight);
            }
            batch.setColor(Color.WHITE);
        }

        if (session.getLevel() instanceof NightOpsLevel
                || session.getLevel().getSeason().isNight()) {
            batch.setColor(0.04f, 0.06f, 0.14f, 0.28f);
            batch.draw(whitePixel, 0, 0, boardWidth, boardHeight);
            batch.setColor(Color.WHITE);
        }
    }

    private void drawSpecialLevelBoardEffects() {
        var level = session.getLevel();
        if (level == null) return;

        if (level instanceof SaveOurSeedsLevel save && save.getSeedPositions() != null) {
            for (Position p : save.getSeedPositions().keySet()) {
                drawCellBorder((int) p.y(), (int) p.x(),
                        new Color(0.2f, 1f, 0.35f, 0.55f), 5f);
            }
        }

        if (level instanceof DeadLineLevel deadline && deadline.getDeadLine() != null) {
            int column = (int) deadline.getDeadLine().x();
            float x = column * TILE_SIZE;
            batch.setColor(1f, 0.18f, 0.10f, 0.72f);
            batch.draw(whitePixel, x, 0, 7, boardHeight);
            batch.setColor(Color.WHITE);
        }

        if (level instanceof IntroductionLevel) {
            batch.setColor(1f, 1f, 0.75f, 0.16f);
            batch.draw(whitePixel, 0, 0, boardWidth, boardHeight);
            batch.setColor(Color.WHITE);
        }

        if (level instanceof ConveyorBeltLevel conveyor) {
            float h = 58f;
            batch.setColor(0.10f, 0.10f, 0.10f, 0.88f);
            batch.draw(whitePixel, 0, boardHeight - h, boardWidth, h);
            batch.setColor(0.55f, 0.40f, 0.18f, 0.75f);
            for (int i = 0; i < 12; i++) {
                float x = i * 85f + 10f;
                batch.draw(whitePixel, x, boardHeight - h + 7f, 52f, 7f);
            }
            batch.setColor(Color.WHITE);
            if (conveyor.getCurrentPlant() != null && font != null) {
                font.setColor(Color.WHITE);
                font.draw(batch, "NEXT: " + conveyor.getCurrentPlant().getName(),
                        18f, boardHeight - 20f);
            }
        }

        if (level instanceof PlantWhatYouGetLevel) {
            batch.setColor(0.85f, 0.65f, 0.18f, 0.16f);
            batch.draw(whitePixel, 0, 0, boardWidth, boardHeight);
            batch.setColor(Color.WHITE);
        }

        drawEgyptGraves();
    }

    private void drawEgyptGraves() {
        if (!isEgyptLevel() || session.getEnvironment() == null) return;
        for (int r = 0; r < session.getRows(); r++) {
            for (int c = 0; c < session.getCols(); c++) {
                Cell cell = session.getEnvironment().getCell(r, c);
                if (cell == null || cell.getObstacle() == null) continue;
                if (!"Grave".equalsIgnoreCase(cell.getObstacle().getName())) continue;
                drawGrave(r, c);
            }
        }
    }

    private void drawGrave(int row, int col) {
        float x = col * TILE_SIZE + TILE_SIZE * 0.22f;
        float y = cellBottomY(row) + TILE_SIZE * 0.08f;
        float w = TILE_SIZE * 0.56f;
        float h = TILE_SIZE * 0.72f;

        TextureRegion region = GameAssetManager.get().getUiRegion("grave");
        if (region != null) {
            batch.draw(region, x, y, w, h);
            return;
        }

        batch.setColor(0.62f, 0.60f, 0.55f, 1f);
        batch.draw(whitePixel, x, y, w, h * 0.78f);
        batch.draw(whitePixel, x + w * 0.20f, y + h * 0.78f, w * 0.60f, h * 0.22f);
        batch.setColor(0.30f, 0.29f, 0.27f, 1f);
        batch.draw(whitePixel, x + w * 0.14f, y + h * 0.20f, w * 0.72f, h * 0.05f);
        batch.draw(whitePixel, x + w * 0.42f, y + h * 0.08f, w * 0.16f, h * 0.30f);
        batch.setColor(Color.WHITE);
    }

    private void drawCellBorder(int row, int col, Color color, float thickness) {
        if (row < 0 || col < 0 || row >= session.getRows() || col >= session.getCols()) return;
        float x = col * TILE_SIZE;
        float y = cellBottomY(row);
        batch.setColor(color);
        batch.draw(whitePixel, x, y, TILE_SIZE, thickness);
        batch.draw(whitePixel, x, y + TILE_SIZE - thickness, TILE_SIZE, thickness);
        batch.draw(whitePixel, x, y, thickness, TILE_SIZE);
        batch.draw(whitePixel, x + TILE_SIZE - thickness, y, thickness, TILE_SIZE);
        batch.setColor(Color.WHITE);
    }

    private void drawGridLines() {
        batch.setColor(1f, 1f, 1f, 0.25f);
        for (int c = 0; c <= session.getCols(); c++) {
            batch.draw(whitePixel, c * TILE_SIZE - 1, 0, 2, boardHeight);
        }
        for (int r = 0; r <= session.getRows(); r++) {
            batch.draw(whitePixel, 0, r * TILE_SIZE - 1, boardWidth, 2);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawHoverHighlight() {
        if (hoverRow < 0 || hoverCol < 0) return;
        boolean active = armedPlantName != null || armedTool != Tool.NONE;
        batch.setColor(active ? new Color(0.6f, 1f, 0.6f, 0.35f) : new Color(1f, 1f, 1f, 0.2f));
        batch.draw(whitePixel, hoverCol * TILE_SIZE, cellBottomY(hoverRow), TILE_SIZE, TILE_SIZE);
        batch.setColor(Color.WHITE);
    }

    private void drawGroundItems() {
        for (var item : session.getItems()) {
            if (!(item instanceof GroundItem groundItem) || !groundItem.isAlive()) continue;
            Position pos = groundItem.getPosition();
            if (pos == null) continue;
            float x = (float) (pos.x() * TILE_SIZE) + TILE_SIZE * 0.3f;
            float y = boardHeight - (float) ((pos.y() + 1) * TILE_SIZE) + TILE_SIZE * 0.3f;
            TextureRegion region = GameAssetManager.get().getItemRegion(groundItem.getItemType().name());
            drawEntity(region, x, y, TILE_SIZE * 0.4f, TILE_SIZE * 0.4f, itemColor(groundItem.getItemType().name()),
                    groundItem.getItemType().name().substring(0, 1));
        }
    }

    private void drawPlantsAndZombiesSorted() {
        List<Plant> plants = new ArrayList<>(session.getPlants());
        plants.sort(Comparator.comparingDouble(p -> p.getPosition() != null ? p.getPosition().y() : 0));
        for (Plant plant : plants) {
            Position pos = plant.getPosition();
            if (pos == null) continue;
            float x = (float) (pos.x() * TILE_SIZE);
            float y = boardHeight - (float) ((pos.y() + 1) * TILE_SIZE);
            TextureRegion region = GameAssetManager.get().getPlantRegion(plant.getName());
            drawEntity(region, x, y, TILE_SIZE, TILE_SIZE, new Color(0.2f, 0.6f, 0.2f, 1f),
                    initials(plant.getName()));
        }

        List<Zombie> zombies = new ArrayList<>(session.getZombies());
        zombies.sort(Comparator.comparingDouble(z -> z.getPosition() != null ? z.getPosition().y() : 0));
        for (Zombie zombie : zombies) {
            Position pos = zombie.getPosition();
            if (pos == null) continue;
            float x = (float) (pos.x() * TILE_SIZE);
            float y = boardHeight - (float) ((pos.y() + 1) * TILE_SIZE);
            TextureRegion region = GameAssetManager.get().getZombieRegion(zombie.getAlias());
            drawEntity(region, x, y, TILE_SIZE, TILE_SIZE, new Color(0.55f, 0.5f, 0.45f, 1f),
                    initials(zombie.getAlias()));
        }
    }

    private void drawProjectiles() {
        for (Projectile projectile : session.getProjectiles()) {
            drawSmallDot(projectile.getPosition(), new Color(0.9f, 0.9f, 0.2f, 1f));
        }
        for (ZombieProjectile projectile : session.getZombieProjectiles()) {
            drawSmallDot(projectile.getPosition(), new Color(0.7f, 0.2f, 0.2f, 1f));
        }
    }

    private void drawSmallDot(Position pos, Color color) {
        if (pos == null) return;
        float size = TILE_SIZE * 0.18f;
        float x = (float) (pos.x() * TILE_SIZE) + TILE_SIZE / 2f - size / 2f;
        float y = boardHeight - (float) ((pos.y() + 1) * TILE_SIZE) + TILE_SIZE / 2f - size / 2f;
        batch.setColor(color);
        batch.draw(whitePixel, x, y, size, size);
        batch.setColor(Color.WHITE);
    }

    private void drawLawnMowers() {
        for (int row = 0; row < session.getRows(); row++) {
            if (session.isLawnMowerUsed(row)) continue;
            TextureRegion region = GameAssetManager.get().getUiRegion("lawn_mower");
            float y = cellBottomY(row);
            drawEntity(region, -TILE_SIZE * 0.6f, y, TILE_SIZE * 0.6f, TILE_SIZE, new Color(0.7f, 0.1f, 0.1f, 1f), "M");
        }
    }

    private float cellBottomY(int row) {
        return boardHeight - (row + 1) * TILE_SIZE;
    }

    private void drawEntity(TextureRegion region, float x, float y, float w, float h, Color fallbackColor, String label) {
        if (region != null) {
            batch.draw(region, x, y, w, h);
            return;
        }
        batch.setColor(fallbackColor);
        batch.draw(whitePixel, x, y, w, h);
        batch.setColor(Color.WHITE);
        if (font != null && label != null && !label.isEmpty()) {
            font.draw(batch, label, x + w * 0.15f, y + h * 0.65f);
        }
    }

    private static String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) sb.append(Character.toUpperCase(word.charAt(0)));
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }

    private static Color itemColor(String itemTypeName) {
        return switch (itemTypeName) {
            case "SUN" -> new Color(1f, 0.85f, 0.1f, 1f);
            case "PLANT_FOOD" -> new Color(0.2f, 0.9f, 0.4f, 1f);
            case "COIN" -> new Color(0.85f, 0.7f, 0.2f, 1f);
            case "DIAMOND" -> new Color(0.3f, 0.8f, 1f, 1f);
            case "SEED_PACK" -> new Color(0.6f, 0.4f, 0.2f, 1f);
            default -> Color.LIGHT_GRAY;
        };
    }

    private static TextureRegion makeWhitePixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (whitePixel != null) whitePixel.getTexture().dispose();
        if (boardBackgroundTexture != null) boardBackgroundTexture.dispose();
        if (ownsFont && font != null) font.dispose();
        seedCardFactory.dispose();
    }



    private void retryMatch() {
        MatchMenu matchMenu = new MatchMenu();
        App.currentMenu = matchMenu;
        try {
            matchMenu.handleCommand("start game");
        } catch (GameException e) {
            Toast.show(stage, e.getMessage());
            return;
        }
        if (App.currentMenu instanceof MeanwhileMenu) {
            session = GameSession.getInstance();
            matchFinished = false;
            paused = false;
            tickAccumulator = 0;
            rebuildPlantCards();
        } else {


            Toast.show(stage, "Pick your plants to retry - the loadout screen isn't wired up yet.");
        }
    }

    private class PauseModal extends Modal {
        PauseModal() {
            pad(24);
            add(new Label("Paused", skin)).padBottom(16).row();

            TextButton resume = new TextButton("Resume", skin);
            resume.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    paused = false;
                    hide();
                }
            });
            add(resume).width(220).height(50).pad(6).row();

            TextButton restart = new TextButton("Restart", skin);
            restart.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (runCommand("restart")) {
                        paused = false;
                        matchFinished = false;
                        hide();
                    }
                }
            });
            add(restart).width(220).height(50).pad(6).row();

            TextButton exit = new TextButton("Save & Exit", skin);
            exit.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    runCommand("menu exit");
                    hide();

                }
            });
            add(exit).width(220).height(50).pad(6);
        }
    }

    private class EndMatchModal extends Modal {
        EndMatchModal(boolean won) {
            pad(24);
            String summary = App.currentMenu instanceof AfterMenu afterMenu
                    ? afterMenu.showMenu()
                    : (won ? "You win!" : "You lose!");
            Label label = new Label(summary, skin);
            label.setWrap(true);
            add(label).width(420).padBottom(16).row();

            if (!won) {
                TextButton retry = new TextButton("Retry", skin);
                retry.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        retryMatch();
                        hide();
                    }
                });
                add(retry).width(220).height(50).pad(6).row();
            }

            TextButton exit = new TextButton("Exit", skin);
            exit.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (App.currentMenu instanceof AfterMenu) {
                        runCommand("menu exit");
                    }
                    hide();

                }
            });
            add(exit).width(220).height(50).pad(6);
        }
    }




    private class WaveProgressBar extends Actor {
        private float progress = 0f;
        private int totalWaves = 0;

        void setProgress(float progress, int totalWaves) {
            this.progress = Math.max(0f, Math.min(1f, progress));
            this.totalWaves = totalWaves;
        }

        @Override
        public void draw(Batch batchParam, float parentAlpha) {
            float x = getX(), y = getY(), w = getWidth(), h = getHeight();
            batchParam.setColor(0.15f, 0.15f, 0.15f, 0.8f);
            batchParam.draw(whitePixel, x, y, w, h);

            batchParam.setColor(0.8f, 0.25f, 0.2f, 1f);
            batchParam.draw(whitePixel, x, y, w * progress, h);

            if (totalWaves > 0) {
                batchParam.setColor(1f, 1f, 1f, 0.6f);
                for (int i = 1; i < totalWaves; i++) {
                    float markX = x + w * ((float) i / totalWaves);
                    batchParam.draw(whitePixel, markX - 1, y, 2, h);
                }
            }
            batchParam.setColor(1f, 1f, 1f, 1f);
        }
    }
}