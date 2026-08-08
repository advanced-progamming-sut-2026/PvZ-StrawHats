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
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
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
        loadFont(AssetPaths.DEFAULT_FONT);
        coreAssetsQueued = true;
        initializeSkin();
    }

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

        Drawable panel = roundedPatch(64, 18, 3, parchment, border);
        Drawable buttonUp = roundedPatch(56, 14, 2, sun, sunDark);
        Drawable buttonDown = roundedPatch(56, 14, 2, sunDark, sunDark);
        Drawable buttonOver = roundedPatch(56, 14, 2, lighten(sun), sunDark);
        Drawable buttonChecked = roundedPatch(56, 14, 2, green, greenDark);
        Drawable buttonDisabled = roundedPatch(56, 14, 2, Color.valueOf("D8D2C1"), Color.valueOf("BDB59E"));
        Drawable fieldBg = roundedPatch(48, 12, 2, Color.WHITE, border);
        Drawable cursor = solidDrawable(2, 34, textDark);
        Drawable selection = solidDrawable(2, 34, sun);
        Drawable checkOn = roundedPatch(28, 7, 2, green, greenDark);
        Drawable checkOff = roundedPatch(28, 7, 2, Color.WHITE, border);

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
        secondaryStyle.up = roundedPatch(56, 14, 2, parchment, border);
        secondaryStyle.down = roundedPatch(56, 14, 2, border, border);
        secondaryStyle.over = roundedPatch(56, 14, 2, Color.WHITE, border);
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

        skin.add("modal-background", panel, Drawable.class);

        return skin;
    }

    private Color lighten(Color c) {
        return new Color(Math.min(1f, c.r + 0.08f), Math.min(1f, c.g + 0.08f), Math.min(1f, c.b + 0.08f), c.a);
    }

    private Drawable roundedPatch(int tile, int radius, int borderWidth, Color fill, Color border) {
        Pixmap outer = new Pixmap(tile, tile, Pixmap.Format.RGBA8888);
        outer.setColor(border);
        fillRounded(outer, 0, 0, tile, tile, radius);

        int innerSize = tile - borderWidth * 2;
        Pixmap inner = new Pixmap(innerSize, innerSize, Pixmap.Format.RGBA8888);
        inner.setColor(fill);
        fillRounded(inner, 0, 0, innerSize, innerSize, Math.max(0, radius - borderWidth));
        outer.drawPixmap(inner, borderWidth, borderWidth);
        inner.dispose();

        Texture texture = new Texture(outer);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        outer.dispose();

        int inset = Math.max(borderWidth + 1, radius);
        NinePatch patch = new NinePatch(texture, inset, inset, inset, inset);
        return new NinePatchDrawable(patch);
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
