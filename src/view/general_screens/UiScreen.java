package view.general_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import controller.assets.ScreenManager;
import model.App;
import model.game_exceptions.GameException;
import view.GeneralPrinter;

import java.util.function.Consumer;

public abstract class UiScreen extends BaseScreen {

    protected static final float SPACE_XS = 2f;
    protected static final float SPACE_SM = 8f;
    protected static final float SPACE_MD = 10f;
    protected static final float SPACE_LG = 14f;
    protected static final float SPACE_XL = 22f;

    protected static final float CARD_PAD = 12f;
    protected static final float LABEL_WIDTH = 120f;
    protected static final float FIELD_WIDTH = 212f;
    protected static final float BUTTON_WIDTH = 270f;

    private final Consumer<String> printerListener = this::onMessage;

    private Texture loadLinearTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    @Override
    public void setSkin() {
        this.skin = new Skin();
        BitmapFont font;
        String fontPath = "assets/fonts/FBUSV8C5EI.TTF";
        if (Gdx.files.internal(fontPath).exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 22;
            parameter.color = Color.WHITE;
            font = generator.generateFont(parameter);
            generator.dispose();
        } else {
            font = new BitmapFont();
        }
        skin.add("default-font", font);

        Drawable goldenCardDrawable = createGoldenDrawable(520, 440);
        skin.add("card-background", goldenCardDrawable, Drawable.class);
        skin.add("modal-background", goldenCardDrawable, Drawable.class);

        Texture tfBack = loadLinearTexture("assets/images/ui/free_coins_button_large_golden_505x130_2.png");
        TextureRegionDrawable tfDrawable = new TextureRegionDrawable(tfBack);
        tfDrawable.setLeftWidth(16);
        tfDrawable.setRightWidth(16);
        skin.add("textfield-bg", tfDrawable, Drawable.class);

        Drawable wideCursor = createWideCursorDrawable(6, 26, Color.GOLD);
        skin.add("cursor", wideCursor, Drawable.class);

        Texture selectionTex = loadLinearTexture("assets/images/ui/free_coins_button_large_golden_505x130_2.png");
        skin.add("selection", new TextureRegionDrawable(selectionTex), Drawable.class);

        Texture btnUp = loadLinearTexture("assets/images/ui/quest_points_fillbar_fill.png");
        Texture btnDown = loadLinearTexture("assets/images/ui/quest_points_fillbar_fill_green.png");
        skin.add("button-up", new TextureRegionDrawable(btnUp), Drawable.class);
        skin.add("button-down", new TextureRegionDrawable(btnDown), Drawable.class);

        Texture checkOn = loadLinearTexture("assets/images/ui/checkbox_enabled.png");
        Texture checkOff = loadLinearTexture("assets/images/ui/checkbox_disabled.png");
        skin.add("checkbox-on", new TextureRegionDrawable(checkOn), Drawable.class);
        skin.add("checkbox-off", new TextureRegionDrawable(checkOff), Drawable.class);

        skin.add("title", new Label.LabelStyle(font, Color.WHITE));
        skin.add("main", new Label.LabelStyle(font, Color.LIGHT_GRAY));
        skin.add("muted", new Label.LabelStyle(font, Color.GRAY));

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = skin.getDrawable("textfield-bg");
        textFieldStyle.cursor = skin.getDrawable("cursor");
        textFieldStyle.selection = skin.getDrawable("selection");
        skin.add("main", textFieldStyle);

        TextButton.TextButtonStyle mainButtonStyle = new TextButton.TextButtonStyle();
        mainButtonStyle.font = font;
        mainButtonStyle.fontColor = Color.WHITE;
        mainButtonStyle.up = skin.getDrawable("button-up");
        mainButtonStyle.down = skin.getDrawable("button-down");
        skin.add("main", mainButtonStyle);

        TextButton.TextButtonStyle secButtonStyle = new TextButton.TextButtonStyle();
        secButtonStyle.font = font;
        secButtonStyle.fontColor = Color.LIGHT_GRAY;
        secButtonStyle.up = skin.getDrawable("button-up");
        secButtonStyle.down = skin.getDrawable("button-down");
        skin.add("secondary", secButtonStyle);

        TextButton.TextButtonStyle genderBtnStyle = new TextButton.TextButtonStyle();
        genderBtnStyle.font = font;
        genderBtnStyle.fontColor = Color.WHITE;
        genderBtnStyle.up = skin.getDrawable("button-up");
        genderBtnStyle.down = skin.getDrawable("button-down");
        genderBtnStyle.checked = skin.getDrawable("button-down");
        skin.add("gender-button", genderBtnStyle);

        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = font;
        checkBoxStyle.fontColor = Color.WHITE;
        checkBoxStyle.checkboxOn = skin.getDrawable("checkbox-on");
        checkBoxStyle.checkboxOff = skin.getDrawable("checkbox-off");
        skin.add("main", checkBoxStyle);
    }

    private Drawable createGoldenDrawable(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.45f, 0.34f, 0.10f, 0.90f));
        pixmap.fill();
        pixmap.setColor(new Color(0.22f, 0.18f, 0.08f, 0.88f));
        pixmap.fillRectangle(4, 4, width - 8, height - 8);
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private Drawable createWideCursorDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    protected void addTitleImage(Table card) {
        String titlePath = "assets/images/ui/pvz2_logo_horizontal.png";
        if (Gdx.files.internal(titlePath).exists()) {
            Texture titleTexture = loadLinearTexture(titlePath);
            Image titleImg = new Image(titleTexture);
            titleImg.setScaling(Scaling.fit);
            card.add(titleImg).colspan(2).size(150, 55).padBottom(SPACE_MD).row();
        } else {
            card.add(new Label("Plants vs Zombies", skin, "title")).colspan(2).padBottom(SPACE_MD).row();
        }
    }

    protected ScrollPane scrollable(Table content) {
        ScrollPane pane = new ScrollPane(content);
        pane.setScrollingDisabled(true, false);
        pane.setFadeScrollBars(false);
        pane.setOverscroll(false, false);
        return pane;
    }

    @Override
    public void initParticles() {
        if (particles != null) {
            particles.dispose();
        }
        particlePaths = new String[]{"assets/images/ui/hocus_crocus_49x28.png"};
        particles = new ParticleCreator(particlePaths, 15, 20f, 32f, 1.2f, true);
        Actor particleActor = particles.createActor();
        particleActor.setTouchable(Touchable.disabled);
        rootStack.addActorAt(1, particleActor);
    }

    @Override
    public void show() {
        super.show();
        initParticles();
        GeneralPrinter.addListener(printerListener);
    }

    @Override
    public void hide() {
        GeneralPrinter.removeListener(printerListener);
        super.hide();
    }

    private void onMessage(String message) {
        if (stage != null) {
            Toast.show(stage, message);
        }
    }

    protected void runCommand(String command) {
        try {
            App.currentMenu.handleCommand(command);
        } catch (GameException e) {
            GeneralPrinter.print("[Error] " + e.getMessage());
        } catch (Exception e) {
            GeneralPrinter.print(String.valueOf(e.getMessage()));
        }
        ScreenManager.syncWithCurrentMenu();
        if (ScreenManager.getScreen() == this) {
            onAfterCommand();
        }
    }

    protected void onAfterCommand() {
    }

    protected TextField field(boolean masked) {
        TextField textField = new TextField("", skin, "main");
        textField.setTextFieldFilter((tf, c) -> c != ' ');
        if (masked) {
            textField.setPasswordCharacter('*');
            textField.setPasswordMode(true);
        }
        return textField;
    }

    protected void addRow(Table card, String labelText, Actor field) {
        card.add(new Label(labelText, skin, "main")).width(LABEL_WIDTH).left();
        card.add(field).width(FIELD_WIDTH).left().row();
    }

    protected TextButton primaryButton(String text, Runnable action) {
        return styledButton(new TextButton(text, skin, "main"), action);
    }

    protected TextButton secondaryButton(String text, Runnable action) {
        return styledButton(new TextButton(text, skin, "secondary"), action);
    }

    protected TextButton styledButton(TextButton button, Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1.0f, 1.0f, 0.1f));
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    protected Label createLabel(String text, String styleName) {
        return new Label(text, skin, styleName);
    }
}