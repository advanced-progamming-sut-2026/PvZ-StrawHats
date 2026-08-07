package view.general_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import model.utils.GameSettings;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;

public abstract class BaseScreen implements Screen {
    protected String[] particlePaths = null;
    public abstract void initParticles();
    protected static final float DEFAULT_LOADING_DURATION = 1f;

    protected static float loadingDuration = DEFAULT_LOADING_DURATION;
    protected static String loadingImagePath;
    public Stage stage;
    protected Skin skin;
    protected SpriteBatch batch = new SpriteBatch();
    protected Table rootTable;
    protected Stack rootStack;
    protected Stack modalStack;
    public Stack toastStack;
    protected Image backgroundImage;
    protected InputMultiplexer multiplexer = new InputMultiplexer();
    protected ParticleCreator particles;

    public static float SCREEN_WIDTH = 1280f, SCREEN_HEIGHT = 720f;

    @Override
    public void show() {
        Viewport viewport = createViewport();
        stage = new Stage(viewport, batch);
        setSkin();

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(25f);

        modalStack = new Stack();
        toastStack = new Stack();
        rootStack = new Stack();
        rootStack.setFillParent(true);

        if (backgroundImage != null) {
            rootStack.add(backgroundImage);
        }

        rootStack.add(rootTable);
        rootStack.add(modalStack);
        rootStack.add(toastStack);

        stage.addActor(rootStack);

        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Actor target = event.getTarget();
                while (target != null && !(target instanceof Button)) {
                    target = target.getParent();
                }
                if (target != null && !((Button) target).isDisabled()) {
                    if (GameSettings.get().isDebugMode()) {
                        Gdx.app.log("ClickSound", "Touched: " + event.getTarget().getClass());
                    }
                    AudioManager.get().playSound(AudioEnum.SFX_CLICK, 1f);
                }
                return false;
            }
        });

        showLoading();
    }

    protected void setBackground(String path) {
        if (!Gdx.files.internal(path).exists()) {
            return;
        }
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundImage = new Image(texture);
        backgroundImage.setFillParent(true);
    }

    public void setSkin() {}

    public Stack getModalStack() {
        return modalStack;
    }

    public Stack getToastStack() {
        return toastStack;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    protected Viewport createViewport() {
        return new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        if (particles != null) {
            particles.dispose();
            particles = null;
        }
    }

    private void showLoading() {
        Image overlay;
        if (loadingImagePath == null) return;
        if (Gdx.files.internal(loadingImagePath).exists()) {
            Texture texture = new Texture(Gdx.files.internal(loadingImagePath));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            overlay = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        } else {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            pixmap.dispose();
            overlay = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        }
        overlay.setFillParent(true);
        rootStack.addActor(overlay);
        overlay.addAction(Actions.sequence(
                Actions.delay(loadingDuration),
                Actions.fadeOut(0.05f),
                Actions.removeActor()
        ));
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}