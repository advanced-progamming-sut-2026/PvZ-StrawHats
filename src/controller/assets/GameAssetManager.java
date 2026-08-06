package controller.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class GameAssetManager {

    private static GameAssetManager instance;

    private final AssetManager assetManager;
    private boolean coreAssetsQueued = false;

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
        loadAtlas(AssetPaths.UI_ATLAS);
        loadFont(AssetPaths.DEFAULT_FONT);
        coreAssetsQueued = true;
    }

    public void loadAtlas(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, TextureAtlas.class);
        }
    }

    public void loadTexture(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, Texture.class);
        }
    }

    public void loadFont(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, BitmapFont.class);
        }
    }

    public void loadSound(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, Sound.class);
        }
    }

    public void loadMusic(String path) {
        if (!assetManager.isLoaded(path)) {
            assetManager.load(path, Music.class);
        }
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
        return assetManager.isLoaded(path);
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

    private TextureRegion findRegion(String atlasPath, String regionName) {
        TextureAtlas atlas = getAtlas(atlasPath);
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
    }
}
