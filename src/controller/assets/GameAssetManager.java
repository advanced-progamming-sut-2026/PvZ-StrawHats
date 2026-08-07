package controller.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class GameAssetManager {

    private static GameAssetManager instance;

    private final AssetManager assetManager;
    private boolean coreAssetsQueued = false;
    private Skin skin;

    private GameAssetManager() {
        this.assetManager = new AssetManager();
    }

    public static GameAssetManager get() {
        if (instance == null) {
            instance = new GameAssetManager();
        }
        return instance;
    }

    public void initialize() {
        if (coreAssetsQueued) {
            return;
        }
        loadAtlas(AssetPaths.PLANTS_ATLAS);
        loadAtlas(AssetPaths.ZOMBIES_ATLAS);
        loadAtlas(AssetPaths.ITEMS_ATLAS);
        loadAtlas(AssetPaths.TILES_ATLAS);
        // UI_ATLAS is intentionally not queued here yet - see initializeSkin(): until a real
        // atlas + skin JSON exist, getSkin() is served by a small procedurally-generated skin
        // instead, so screens have working buttons/fields/labels from day one.
        loadFont(AssetPaths.DEFAULT_FONT);
        coreAssetsQueued = true;
        initializeSkin();
    }

    // ---- loading (each is a no-op, not a crash, if the file isn't on disk yet) ----

    public void loadAtlas(String path) {
        if (!assetManager.isLoaded(path) && exists(path)) {
            assetManager.load(path, TextureAtlas.class);
        }
    }

    public void loadTexture(String path) {
        if (!assetManager.isLoaded(path) && exists(path)) {
            assetManager.load(path, Texture.class);
        }
    }

    public void loadFont(String path) {
        if (!assetManager.isLoaded(path) && exists(path)) {
            assetManager.load(path, BitmapFont.class);
        }
    }

    public void loadSound(String path) {
        if (!assetManager.isLoaded(path) && exists(path)) {
            assetManager.load(path, Sound.class);
        }
    }

    public void loadMusic(String path) {
        if (!assetManager.isLoaded(path) && exists(path)) {
            assetManager.load(path, Music.class);
        }
    }

    private boolean exists(String path) {
        return path != null && !path.isEmpty() && Gdx.files.internal(path).exists();
    }

    public boolean update() {
        return assetManager.update();
    }

    public float getProgress() {
        return assetManager.getProgress();
    }

    public void finishLoading() {
        assetManager.finishLoading();
    }

    public boolean isLoaded(String path) {
        return path != null && assetManager.isLoaded(path);
    }

    public <T> T get(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    public TextureAtlas getAtlas(String atlasPath) {
        return assetManager.get(atlasPath, TextureAtlas.class);
    }

    public Texture getTexture(String path) {
        return assetManager.get(path, Texture.class);
    }

    public BitmapFont getFont(String path) {
        return assetManager.get(path, BitmapFont.class);
    }

    public Sound getSound(String path) {
        return assetManager.get(path, Sound.class);
    }

    public Music getMusic(String path) {
        return assetManager.get(path, Music.class);
    }

    public TextureRegion getPlantRegion(String plantName) {
        return findRegion(AssetPaths.PLANTS_ATLAS, AssetPaths.plantRegion(plantName));
    }

    public TextureRegion getZombieRegion(String zombieAlias) {
        return findRegion(AssetPaths.ZOMBIES_ATLAS, AssetPaths.zombieRegion(zombieAlias));
    }
    public TextureRegion getItemRegion(String name) {
        return findRegion(AssetPaths.ITEMS_ATLAS, AssetPaths.uiRegion(name));
    }

    private TextureRegion findRegion(String atlasPath, String regionName) {
        TextureAtlas atlas = getAtlas(atlasPath);
        if (atlas == null) {
            return null;
        }
        TextureRegion region = atlas.findRegion(regionName);
        if (region == null) {
            Gdx.app.error("GameAssetManager", "Missing region '" + regionName + "' in " + atlasPath);
        }
        return region;
    }

    public AssetManager raw() {
        return assetManager;
    }

    public void dispose() {
        assetManager.dispose();
        coreAssetsQueued = false;
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }

    public Skin getSkin() {
        if (skin == null) {
            initializeSkin();
        }
        return skin;
    }

    // ---- skin ----

    /**
     * Populates {@link #skin} so {@code Toast}/{@code Modal}/every new screen has a working
     * "default" Label/TextButton/TextField/CheckBox/Window style from the moment the app boots,
     * with no packed atlas required. Once a real {@code assets/atlases/ui.atlas} + skin JSON
     * exist, swap the body of this method for {@code new Skin(Gdx.files.internal("assets/ui/skin.json"))}
     * and every screen that reads styles by name ("default", "secondary", "title", "muted")
     * keeps working unchanged.
     */
    private void initializeSkin() {
        if (skin != null) {
            return;
        }
        try {
            skin = buildFallbackSkin();
        } catch (Exception e) {
            Gdx.app.error("GameAssetManager", "Failed to build the fallback UI skin", e);
        }
    }

    private Skin buildFallbackSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.15f);
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2f);

        Color parchment = Color.valueOf("FFFDF6");
        Color border = Color.valueOf("C7B88A");
        Color green = Color.valueOf("4C8C4A");
        Color greenDark = Color.valueOf("386B37");
        Color sun = Color.valueOf("F2B705");
        Color sunDark = Color.valueOf("C68F02");
        Color textDark = Color.valueOf("2E2A1F");
        Color textMuted = Color.valueOf("6E6553");

        Drawable panel = roundedDrawable(560, 10, 18, parchment, border, 3);
        Drawable buttonUp = roundedDrawable(240, 56, 14, sun, sunDark, 2);
        Drawable buttonDown = roundedDrawable(240, 56, 14, sunDark, sunDark, 2);
        Drawable buttonOver = roundedDrawable(240, 56, 14, lighten(sun), sunDark, 2);
        Drawable buttonChecked = roundedDrawable(240, 56, 14, green, greenDark, 2);
        Drawable buttonDisabled = roundedDrawable(240, 56, 14, Color.valueOf("D8D2C1"), Color.valueOf("BDB59E"), 2);
        Drawable fieldBg = roundedDrawable(340, 56, 12, Color.WHITE, border, 2);
        Drawable cursor = solidDrawable(2, 34, textDark);
        Drawable selection = solidDrawable(2, 34, sun);
        Drawable checkOn = roundedDrawable(30, 30, 7, green, greenDark, 2);
        Drawable checkOff = roundedDrawable(30, 30, 7, Color.WHITE, border, 2);

        skin.add("default", new Label.LabelStyle(font, textDark));
        skin.add("title", new Label.LabelStyle(titleFont, greenDark));
        skin.add("muted", new Label.LabelStyle(font, textMuted));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = buttonUp;
        buttonStyle.down = buttonDown;
        buttonStyle.over = buttonOver;
        buttonStyle.checked = buttonChecked;
        buttonStyle.disabled = buttonDisabled;
        buttonStyle.font = font;
        buttonStyle.fontColor = textDark;
        buttonStyle.checkedFontColor = Color.WHITE;
        buttonStyle.disabledFontColor = textMuted;
        skin.add("default", buttonStyle);

        TextButton.TextButtonStyle secondaryStyle = new TextButton.TextButtonStyle(buttonStyle);
        secondaryStyle.up = roundedDrawable(240, 56, 14, parchment, border, 2);
        secondaryStyle.down = roundedDrawable(240, 56, 14, border, border, 2);
        secondaryStyle.over = roundedDrawable(240, 56, 14, Color.WHITE, border, 2);
        secondaryStyle.fontColor = greenDark;
        skin.add("secondary", secondaryStyle);

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = font;
        fieldStyle.fontColor = textDark;
        fieldStyle.background = fieldBg;
        fieldStyle.cursor = cursor;
        fieldStyle.selection = selection;
        fieldStyle.messageFont = font;
        fieldStyle.messageFontColor = textMuted;
        skin.add("default", fieldStyle);

        CheckBox.CheckBoxStyle checkStyle = new CheckBox.CheckBoxStyle();
        checkStyle.checkboxOn = checkOn;
        checkStyle.checkboxOff = checkOff;
        checkStyle.font = font;
        checkStyle.fontColor = textDark;
        skin.add("default", checkStyle);

        skin.add("default", new Window.WindowStyle(titleFont, greenDark, panel));

        return skin;
    }

    private Color lighten(Color c) {
        return new Color(Math.min(1f, c.r + 0.08f), Math.min(1f, c.g + 0.08f), Math.min(1f, c.b + 0.08f), c.a);
    }

    private Drawable roundedDrawable(int width, int height, int radius, Color fill, Color border, int borderWidth) {
        Pixmap outer = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        outer.setColor(border);
        fillRounded(outer, 0, 0, width, height, radius);

        Pixmap inner = new Pixmap(width - borderWidth * 2, height - borderWidth * 2, Pixmap.Format.RGBA8888);
        inner.setColor(fill);
        fillRounded(inner, 0, 0, width - borderWidth * 2, height - borderWidth * 2, Math.max(0, radius - borderWidth));
        outer.drawPixmap(inner, borderWidth, borderWidth);
        inner.dispose();

        Texture texture = new Texture(outer);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        outer.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private Drawable solidDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(Math.max(1, width), Math.max(1, height), Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private void fillRounded(Pixmap pixmap, int x, int y, int w, int h, int radius) {
        radius = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
        pixmap.fillRectangle(x + radius, y, w - 2 * radius, h);
        pixmap.fillRectangle(x, y + radius, w, h - 2 * radius);
        pixmap.fillCircle(x + radius, y + radius, radius);
        pixmap.fillCircle(x + w - radius - 1, y + radius, radius);
        pixmap.fillCircle(x + radius, y + h - radius - 1, radius);
        pixmap.fillCircle(x + w - radius - 1, y + h - radius - 1, radius);
    }

    public TextureRegion getUiRegion(String name) {
        return findRegion(AssetPaths.UI_ATLAS, AssetPaths.uiRegion(name));
    }

}
