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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import model.projectile.Projectile;
import model.projectile.zombie_projectile.ZombieProjectile;
import model.utils.GameSession;
import model.utils.GameSettings;
import service.GameClock;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;

/**
 * The live match screen: 5x9 lawn, real-time zombie waves, planting/shovel/plant-food, HUD,
 * pause and win/loss modals. All gameplay mutation goes back through the same Menu commands the
 * terminal build used (see MeanwhileMenu), so validation/cost/cooldown logic isn't duplicated
 * here - this screen only ticks the simulation and turns clicks into command strings.
 *
 * Art: every plant/zombie/item is drawn from GameAssetManager's atlases when a region exists;
 * anything not supplied yet falls back to a tinted, labeled rectangle so the board is still fully readable.
 */

public class GameScreen extends BaseScreen {

    private static final float TILE_SIZE = 96f;

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

    private Label sunLabel;
    private Label plantFoodLabel;
    private WaveProgressBar waveBar;
    private TextButton pauseButton;
    private TextButton shovelButton;
    private TextButton feedButton;
    private TextButton startWavesButton;
    private Table loadoutTable;
    private final List<LoadoutEntry> loadoutEntries = new ArrayList<>();

    private record LoadoutEntry(String plantName, int plantId, int cost, TextButton button) {
    }

    @Override
    public void initParticles() {
        particles = new ParticleCreator(
                new String[]{},  // TODO: define a default particle
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

        createHud();
        addBoardClickCatcher();
        initParticles();


        if (session.getLevel() != null) {
            Toast.show(stage, session.getLevel().getName() + " - " + session.getLevel().getGameMode());
        }
    }

    // ---------------------------------------------------------------- HUD

    private void createHud() {
        sunLabel = new Label("", skin);
        plantFoodLabel = new Label("", skin);
        waveBar = new WaveProgressBar();

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

        Table topBar = new Table();
        topBar.add(sunLabel).pad(8);
        topBar.add(plantFoodLabel).pad(8);
        topBar.add(waveBar).width(320).height(18).pad(8).expandX();
        topBar.add(startWavesButton).pad(8);
        topBar.add(pauseButton).pad(8);

        loadoutTable = new Table();
        buildLoadoutBar();

        Table bottomBar = new Table();
        bottomBar.add(loadoutTable).pad(6);
        bottomBar.add(shovelButton).size(110, 60).pad(6);
        bottomBar.add(feedButton).size(110, 60).pad(6);

        rootTable.top();
        rootTable.add(topBar).expandX().fillX().top().row();
        rootTable.add().expand().row();
        rootTable.add(bottomBar).expandX().fillX().bottom();
    }

    private void buildLoadoutBar() {
        loadoutTable.clear();
        loadoutEntries.clear();
        for (String plantName : BeforeMenu.selectedPlants) {
            PlantJsonParser.PlantConfig config = findPlantConfig(plantName);
            if (config == null) continue;

            TextButton button = new TextButton(plantName, skin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    armPlant(plantName);
                }
            });
            loadoutTable.add(button).size(110, 70).pad(4);
            loadoutEntries.add(new LoadoutEntry(plantName, config.id, config.cost, button));
        }
    }

    private PlantJsonParser.PlantConfig findPlantConfig(String plantName) {
        for (PlantJsonParser.PlantConfig config : PlantFactory.getBlueprints().values()) {
            if (config.name.equalsIgnoreCase(plantName)) return config;
        }
        return null;
    }

    private void refreshHud() {
        sunLabel.setText("Sun: " + session.getSunCount());
        plantFoodLabel.setText("Plant food: " + session.getPlantFoodCount());
        int totalWaves = Math.max(1, session.getTotalWaveCount());
        waveBar.setProgress((float) session.getWavesSpawnedCount() / totalWaves, session.getTotalWaveCount());
        startWavesButton.setVisible(!session.isWavesStarted());

        for (LoadoutEntry entry : loadoutEntries) {
            boolean ready = session.isPlantReady(entry.plantId());
            boolean affordable = session.getSunCount() >= entry.cost();
            String cooldownText = ready ? "" : String.format("%n%.1fs", session.getPlantCooldown(entry.plantId()));
            entry.button().setText(entry.plantName() + " (" + entry.cost() + ")" + cooldownText);
            entry.button().setDisabled(!ready || !affordable);
            entry.button().setChecked(entry.plantName().equals(armedPlantName));
        }
        shovelButton.setChecked(armedTool == Tool.SHOVEL);
        feedButton.setChecked(armedTool == Tool.FEED);
        feedButton.setDisabled(session.getPlantFoodCount() <= 0);
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



    /** invisible actor sitting behind the HUD; only receives clicks the HUD didn't consume*/
    private void addBoardClickCatcher() {
        Actor catcher = new Actor();
        catcher.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleBoardClick();
            }
        });
        rootStack.addActorAt(1, catcher); // above background/particles, below rootTable's HUD
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

    /** Sends a command to whatever Menu is currently active and reports failures as a toast.
     *  Returns true on success, so callers can chain a follow-up action. */
    private boolean runCommand(String command) {
        try {
            App.currentMenu.handleCommand(command);
            return true;
        } catch (GameException e) {
            Toast.show(stage, e.getMessage());
            return false;
        }
    }

    // ------------------------------------------------------------ Update

    @Override
    public void render(float delta) {
        if (!paused && !matchFinished) {
            tickAccumulator += delta * GameSettings.get().getGameSpeed();
            while (tickAccumulator >= GameClock.SECONDS_PER_TICK) {
                session.tick();
                tickAccumulator -= GameClock.SECONDS_PER_TICK;
                checkMatchEnd();
                if (matchFinished) {
                    tickAccumulator = 0;
                    break;
                }
            }
            updateHoverAndCollection();
        }

        refreshHud();

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBoard();
        AudioManager.get().update(delta);

        super.render(delta); // stage.act + stage.draw: HUD, modals, toasts
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

    // ------------------------------------------------------------ Render

    private void drawBoard() {
        boardCamera.update();
        batch.setProjectionMatrix(boardCamera.combined);
        batch.begin();

        drawBackground();
        if (GameSettings.get().isShowGrid()) drawGridLines();
        drawHoverHighlight();
        drawGroundItems();
        drawPlantsAndZombiesSorted();
        drawProjectiles();
        drawLawnMowers();

        batch.end();
    }

    private void drawBackground() {
        boolean night = session.getLevel() != null && session.getLevel().getSeason() != null
                && session.getLevel().getSeason().isNight();
        Color base = night ? new Color(0.12f, 0.16f, 0.24f, 1f) : new Color(0.36f, 0.56f, 0.27f, 1f);
        batch.setColor(base);
        batch.draw(whitePixel, 0, 0, boardWidth, boardHeight);
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
        if (ownsFont && font != null) font.dispose();
    }

    // -------------------------------------------------------- End-of-match

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
            buildLoadoutBar();
        } else {
            // The stage requires picking a loadout again (BeforeMenu); no graphical loadout
            // screen exists yet, so we can't drop the player straight back into a match here.
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
                    // TODO: once a graphical GameMenu screen exists, ScreenManager.setScreen(...) there.
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
                    // TODO: once a graphical GameMenu screen exists, ScreenManager.setScreen(...) there.
                }
            });
            add(exit).width(220).height(50).pad(6);
        }
    }

    // ---------------------------------------------------------------- Wave bar


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
