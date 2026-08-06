package view.general_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import model.utils.GameSettings;
import service.resource_manager.AudioEnum;
import service.resource_manager.AudioManager;

/**
 * Base class for every graphical screen (menus and the live match alike). It has the Stage, the
 * shared skin, and three stacked layers on top of the background: rootTable ,
 * modalStack (popups like the pause menu) and toastStack (transient error/notification toasts).
 */
public abstract class BaseScreen implements Screen {
    protected String[] particlePaths = null;
    public abstract void initParticles();

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
        stage = new Stage(viewport);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        setSkin();

        rootTable = new Table();
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
                return false; // don't consume the event
            }
        });
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
        return new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    @Override
    public void render(float delta) {
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
