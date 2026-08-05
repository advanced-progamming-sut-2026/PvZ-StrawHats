package service.resource_manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import model.assets.AssetPaths;
import model.assets.GameAssetManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SkinManager implements Disposable {

    public enum SkinEnum {
        DEFAULT(AssetPaths.UI_ATLAS),
        DARK(AssetPaths.UI_ATLAS),
        LIGHT(AssetPaths.UI_ATLAS);

        private final String atlasPath;

        SkinEnum(String atlasPath) {
            this.atlasPath = atlasPath;
        }

        public String getAtlasPath() {
            return atlasPath;
        }
    }

    public enum FontEnum {
        DEFAULT_FONT(AssetPaths.DEFAULT_FONT),
        TITLE_FONT(AssetPaths.DEFAULT_FONT),
        SMALL_FONT(AssetPaths.DEFAULT_FONT);

        private final String fontPath;

        FontEnum(String fontPath) {
            this.fontPath = fontPath;
        }

        public String getFontPath() {
            return fontPath;
        }
    }

    private final GameAssetManager gameAssetManager;
    private final Map<SkinEnum, Skin> skinCache;
    private final Map<String, BitmapFont> customFonts;
    private SkinEnum currentSkinEnum;
    private Skin currentSkin;

    public SkinManager() {
        this.gameAssetManager = GameAssetManager.getInstance();
        this.skinCache = new EnumMap<>(SkinEnum.class);
        this.customFonts = new HashMap<>();
        this.currentSkinEnum = SkinEnum.DEFAULT;
    }

    public void loadAll() {
        gameAssetManager.initialize();
        for (SkinEnum skinEnum : SkinEnum.values()) {
            load(skinEnum);
        }
    }

    public void load(SkinEnum skinEnum) {
        if (!skinCache.containsKey(skinEnum)) {
            String atlasPath = skinEnum.getAtlasPath();
            if (!gameAssetManager.isLoaded(atlasPath)) {
                gameAssetManager.loadAtlas(atlasPath);
                gameAssetManager.finishLoading();
            }
            TextureAtlas atlas = gameAssetManager.getAtlas(atlasPath);
            if (atlas != null) {
                Skin skin = new Skin(atlas);
                skinCache.put(skinEnum, skin);
                if (currentSkin == null) {
                    this.currentSkin = skin;
                    this.currentSkinEnum = skinEnum;
                }
            }
        }
    }

    public void setCurrentSkin(SkinEnum skinEnum) {
        if (!skinCache.containsKey(skinEnum)) {
            load(skinEnum);
        }
        Skin skin = skinCache.get(skinEnum);
        if (skin != null) {
            this.currentSkinEnum = skinEnum;
            this.currentSkin = skin;
        }
    }

    public Skin getCurrentSkin() {
        if (currentSkin == null) {
            load(SkinEnum.DEFAULT);
        }
        return currentSkin;
    }

    public Skin getSkin(SkinEnum skinEnum) {
        if (!skinCache.containsKey(skinEnum)) {
            load(skinEnum);
        }
        return skinCache.get(skinEnum);
    }

    public BitmapFont getFont() {
        return getFont(FontEnum.DEFAULT_FONT);
    }

    public BitmapFont getFont(FontEnum fontEnum) {
        String fontPath = fontEnum.getFontPath();
        if (!gameAssetManager.isLoaded(fontPath)) {
            gameAssetManager.loadFont(fontPath);
            gameAssetManager.finishLoading();
        }
        return gameAssetManager.getFont(fontPath);
    }

    public BitmapFont getFont(String fontName) {
        if (customFonts.containsKey(fontName)) {
            return customFonts.get(fontName);
        }
        Skin skin = getCurrentSkin();
        if (skin != null && skin.has(fontName, BitmapFont.class)) {
            return skin.getFont(fontName);
        }
        return getFont(FontEnum.DEFAULT_FONT);
    }

    public void addCustomFont(String name, BitmapFont font) {
        customFonts.put(name, font);
    }

    public void setFontScale(FontEnum fontEnum, float scale) {
        BitmapFont font = getFont(fontEnum);
        if (font != null) {
            font.getData().setScale(scale);
        }
    }

    public void setFontScale(String fontName, float scale) {
        setFontScale(fontName, scale, scale);
    }

    public void setFontScale(String fontName, float scaleX, float scaleY) {
        BitmapFont font = getFont(fontName);
        if (font != null) {
            font.getData().setScale(scaleX, scaleY);
        }
    }

    public Drawable getDrawable(String name) {
        Skin skin = getCurrentSkin();
        return skin != null && skin.has(name, Drawable.class) ? skin.getDrawable(name) : null;
    }

    public TextureRegion getRegion(String regionName) {
        TextureAtlas uiAtlas = gameAssetManager.getAtlas(AssetPaths.UI_ATLAS);
        if (uiAtlas != null) {
            return uiAtlas.findRegion(AssetPaths.slug(regionName));
        }
        return null;
    }

    public Color getColor(String name) {
        Skin skin = getCurrentSkin();
        return skin != null && skin.has(name, Color.class) ? skin.getColor(name) : Color.WHITE;
    }

    public Label.LabelStyle getLabelStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null && skin.has(styleName, Label.LabelStyle.class) ? skin.get(styleName, Label.LabelStyle.class) : null;
    }

    public TextButton.TextButtonStyle getTextButtonStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null && skin.has(styleName, TextButton.TextButtonStyle.class) ? skin.get(styleName, TextButton.TextButtonStyle.class) : null;
    }

    public TextField.TextFieldStyle getTextFieldStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null && skin.has(styleName, TextField.TextFieldStyle.class) ? skin.get(styleName, TextField.TextFieldStyle.class) : null;
    }

    public void resetToDefault() {
        setCurrentSkin(SkinEnum.DEFAULT);
        for (BitmapFont font : customFonts.values()) {
            font.getData().setScale(1.0f, 1.0f);
        }
    }

    public void update(float delta) {
    }

    @Override
    public void dispose() {
        for (Skin skin : skinCache.values()) {
            skin.dispose();
        }
        skinCache.clear();
        for (BitmapFont font : customFonts.values()) {
            font.dispose();
        }
        customFonts.clear();
        currentSkin = null;
    }
}