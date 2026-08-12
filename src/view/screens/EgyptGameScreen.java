package view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.files.FileHandle;

import model.collections.animations.AnimationFactory;
import model.collections.animations.ZombieAnimationRegistry;
import model.match.main.levels.Level;
import model.match.main.levels.special_levels.BossLevel;
import model.match.main.levels.special_levels.ConveyorBeltLevel;
import model.match.main.levels.special_levels.IntroductionLevel;
import model.match.main.levels.special_levels.LockedPlantsLevel;
import model.utils.GameSession;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.textures.TextureBank;
import view.general_screens.GameScreen;

/** Egypt-specific gameplay shell. The actual simulation remains in GameScreen. */
public class EgyptGameScreen extends GameScreen {

    private static final String PANEL_BACKGROUND = "assets/images/backg/wood board.png";

    private Table egyptSidePanel;
    private TextureBank textureBank;
    private PamPlayer pamPlayer;

    @Override
    public void show() {
        super.show();
        initPam();
        buildEgyptSidePanel();
    }

    private void initPam() {
        try {
            FileHandle root = Gdx.files.internal("assets/pvz-assets");
            textureBank = new TextureBank("atlases", root);
            pamPlayer = new PamPlayer(textureBank, root);
        } catch (Throwable ignored) {
            textureBank = null;
            pamPlayer = null;
        }
    }

    private void buildEgyptSidePanel() {
        if (egyptSidePanel != null) egyptSidePanel.remove();

        Level level = GameSession.peekInstance() == null ? null : GameSession.peekInstance().getLevel();
        if (level == null) return;

        egyptSidePanel = new Table();
        egyptSidePanel.setTouchable(Touchable.childrenOnly);
        try {
            egyptSidePanel.setBackground(new TextureRegionDrawable(loadTextureSafe(PANEL_BACKGROUND)));
        } catch (Throwable ignored) {
            egyptSidePanel.setBackground(skin.getDrawable("card-background"));
        }
        egyptSidePanel.pad(12f);

        Label title = new Label("ANCIENT EGYPT", skin, "title");
        title.setAlignment(Align.center);
        egyptSidePanel.add(title).width(300f).center().row();

        Label stageLabel = new Label(level.getName(), skin, "main");
        stageLabel.setAlignment(Align.center);
        stageLabel.setWrap(true);
        egyptSidePanel.add(stageLabel).width(300f).padBottom(6f).row();

        String feature = featureFor(level);
        Label featureLabel = new Label(feature, skin, "main");
        featureLabel.setAlignment(Align.center);
        featureLabel.setWrap(true);
        egyptSidePanel.add(featureLabel).width(300f).padBottom(8f).row();

        Label poolTitle = new Label("ZOMBIES", skin, "main");
        poolTitle.setAlignment(Align.center);
        egyptSidePanel.add(poolTitle).row();

        Table pool = new Table();
        pool.top().left();
        if (level.getZombiePool() != null) {
            for (String alias : level.getZombiePool()) {
                pool.add(new EgyptZombieActor(alias)).size(92f, 100f).pad(3f);
            }
        }

        com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scroll =
                new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane(pool, skin);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setOverscroll(false, false);
        scroll.setCancelTouchFocus(false);
        egyptSidePanel.add(scroll).width(310f).height(430f).fill().row();

        rootStack.add(egyptSidePanel);
        positionSidePanel();
    }

    private String featureFor(Level level) {
        if (level instanceof IntroductionLevel) return "INTRODUCTION\nLearn the basics";
        if (level instanceof ConveyorBeltLevel) return "CONVEYOR BELT\nPlants arrive automatically";
        if (level instanceof LockedPlantsLevel) return "LOCKED PLANTS\nSome plants are unavailable";
        if (level instanceof BossLevel) return "RA'S WRATH\nBOSS BATTLE";
        return "ANCIENT EGYPT\nSURVIVE THE WAVES";
    }

    private void positionSidePanel() {
        if (egyptSidePanel == null || stage == null) return;
        float w = 340f;
        float h = Math.min(690f, stage.getViewport().getWorldHeight() - 100f);
        egyptSidePanel.setSize(w, h);
        egyptSidePanel.setPosition(
                stage.getViewport().getWorldWidth() - w - 18f,
                (stage.getViewport().getWorldHeight() - h) / 2f);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        positionSidePanel();
    }

    @Override
    public void render(float delta) {
        if (textureBank != null) {
            try { textureBank.update(); } catch (Throwable ignored) {}
        }
        super.render(delta);
    }

    @Override
    public void dispose() {
        if (egyptSidePanel != null) egyptSidePanel.remove();
        egyptSidePanel = null;
        textureBank = null;
        pamPlayer = null;
        super.dispose();
    }

    private class EgyptZombieActor extends Stack {
        private final String alias;
        private final String animationPath;
        private float stateTime;

        EgyptZombieActor(String alias) {
            this.alias = alias;
            setSize(92f, 100f);
            setTouchable(Touchable.disabled);

            Table bg = new Table();
            bg.setBackground(skin.getDrawable("card-background"));
            add(bg);

            animationPath = ZombieAnimationRegistry.pathFor(alias);
            if (pamPlayer != null && animationPath != null) {
                add(new Actor() {
                    @Override public void act(float delta) {
                        super.act(delta);
                        stateTime += delta;
                    }

                    @Override public void draw(Batch batch, float parentAlpha) {
                        try {
                            String clipName = AnimationFactory.resolveClipNameForPath(animationPath, "idle");
                            if (clipName == null) return;
                            ClipRef clip = pamPlayer.getClip(animationPath, clipName);
                            if (clip == null) return;

                            Matrix4 original = batch.getTransformMatrix().cpy();
                            float px = getX() + 46f;
                            float py = getY() + 55f;
                            Matrix4 transformed = new Matrix4(original)
                                    .translate(px, py, 0)
                                    .scale(0.32f, 0.32f, 1f)
                                    .translate(-px, -py, 0);
                            batch.setTransformMatrix(transformed);
                            pamPlayer.draw(batch, clip, stateTime, getX() + 8f, getY() + 4f, true);
                            batch.setTransformMatrix(original);
                        } catch (Throwable ignored) {}
                    }
                });
            } else {
                Label fallback = new Label(alias.replace("Zombie", ""), skin, "main");
                fallback.setAlignment(Align.center);
                add(fallback);
            }

            Label name = new Label(alias.replace("Zombie", ""), skin, "main");
            name.setAlignment(Align.center);
            add(name);
        }
    }
}
