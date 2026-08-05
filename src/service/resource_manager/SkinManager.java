package service.resource_manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SkinManager implements Disposable {

    public enum SkinEnum {
        DEFAULT("skins/default/uiskin.json"),
        DARK("skins/dark/dark_uiskin.json"),
        LIGHT("skins/light/light_uiskin.json");

        private final String jsonPath;

        SkinEnum(String jsonPath) {
            this.jsonPath = jsonPath;
        }

        public String getJsonPath() {
            return jsonPath;
        }
    }

    public enum FontEnum {
        DEFAULT_FONT("default-font"),
        TITLE_FONT("title-font"),
        SMALL_FONT("small-font");

        private final String fontName;

        FontEnum(String fontName) {
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }
    }

    private final Map<SkinEnum, Skin> skinCache;
    private final Map<String, BitmapFont> customFonts;
    private SkinEnum currentSkinEnum;
    private Skin currentSkin;

    public SkinManager() {
        this.skinCache = new EnumMap<>(SkinEnum.class);
        this.customFonts = new HashMap<>();
        this.currentSkinEnum = SkinEnum.DEFAULT;
    }

    public void loadAll() {
        for (SkinEnum skinEnum : SkinEnum.values()) {
            load(skinEnum);
        }
    }

    public void load(SkinEnum skinEnum) {
        if (!skinCache.containsKey(skinEnum)) {
            if (Gdx.files.internal(skinEnum.getJsonPath()).exists()) {
                Skin skin = new Skin(Gdx.files.internal(skinEnum.getJsonPath()));
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
        return getFont("default-font");
    }

    public BitmapFont getFont(FontEnum fontEnum) {
        return getFont(fontEnum.getFontName());
    }

    public BitmapFont getFont(String fontName) {
        if (customFonts.containsKey(fontName)) {
            return customFonts.get(fontName);
        }
        Skin skin = getCurrentSkin();
        if (skin != null && skin.has(fontName, BitmapFont.class)) {
            return skin.getFont(fontName);
        }
        return new BitmapFont();
    }

    public void addCustomFont(String name, BitmapFont font) {
        customFonts.put(name, font);
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

    public void setFontScale(FontEnum fontEnum, float scale) {
        setFontScale(fontEnum.getFontName(), scale, scale);
    }

    public Drawable getDrawable(String name) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.getDrawable(name) : null;
    }

    public TextureRegion getRegion(String name) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.getRegion(name) : null;
    }

    public Color getColor(String name) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.getColor(name) : Color.WHITE;
    }

    public Label.LabelStyle getLabelStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.get(styleName, Label.LabelStyle.class) : null;
    }

    public TextButton.TextButtonStyle getTextButtonStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.get(styleName, TextButton.TextButtonStyle.class) : null;
    }

    public TextField.TextFieldStyle getTextFieldStyle(String styleName) {
        Skin skin = getCurrentSkin();
        return skin != null ? skin.get(styleName, TextField.TextFieldStyle.class) : null;
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