package view.general_screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

import static view.general_screens.BaseScreen.SCREEN_HEIGHT;
import static view.general_screens.BaseScreen.SCREEN_WIDTH;

/** Drifting decorative particles (petals, motes, falling leaves) for menu backgrounds. */
public class ParticleCreator {

    private static final float MIN_ALPHA = 0.05f, MAX_ALPHA = 0.5f;
    private final int particleCount;
    private final float minSpeed, maxSpeed, fastness, minSize, maxSize;
    private final boolean isRotating;
    private final boolean enabled;

    private static class Particle { // x, y are the center of it
        float x, y, speedX, speedY, size, alpha, alphaSpeed, rotation, rotationSpeed;
        TextureRegion region;
    }

    private final Texture[] textures;
    private final Particle[] particles;

    private boolean worldMode = false;
    private float worldX, worldY, worldW, worldH;

    /** Higher count -> more density. Any path in particleImages that doesn't exist on disk yet
     *  is skipped; if none exists, the whole creator quietly disables itself instead of crashing. */
    public ParticleCreator(String[] particleImages, int count, float minSize, float maxSize,
                            float fastness, boolean isRotating) {
        this.fastness = fastness;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.isRotating = isRotating;
        this.minSpeed = 20f * fastness;
        this.maxSpeed = 60f * fastness;

        List<Texture> loaded = new ArrayList<>();
        if (particleImages != null) {
            for (String path : particleImages) {
                if (path != null && Gdx.files.internal(path).exists()) {
                    loaded.add(new Texture(Gdx.files.internal(path)));
                }
            }
        }
        this.enabled = !loaded.isEmpty();
        this.textures = loaded.toArray(new Texture[0]);
        this.particleCount = enabled ? count : 0;

        particles = new Particle[particleCount];
        for (int i = 0; i < particleCount; i++) {
            particles[i] = new Particle();
            spawn(particles[i], true);
        }
    }

    public ParticleCreator(String[] particleImages, int count, float minSize, float maxSize, float fastness,
                            boolean isRotating, float worldX, float worldY, float worldW, float worldH) {
        this(particleImages, count, minSize, maxSize, fastness, isRotating);
        this.worldMode = true;
        this.worldX = worldX;
        this.worldY = worldY;
        this.worldW = worldW;
        this.worldH = worldH;
        for (Particle p : particles) spawn(p, true);
    }

    public Actor createActor() {
        return new Actor() {
            @Override
            public void act(float delta) {
                update(delta);
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                ParticleCreator.this.draw((SpriteBatch) batch);
            }
        };
    }

    /** Spawns a particle. randomY=true for the initial scatter. */
    private void spawn(Particle p, boolean randomY) {
        if (!enabled) return;
        p.region = new TextureRegion(textures[MathUtils.random(textures.length - 1)]);
        float minX = worldMode ? worldX : 0f, maxX = worldMode ? worldX + worldW : SCREEN_WIDTH;
        float minY = worldMode ? worldY : 0f, maxY = worldMode ? worldY + worldH : SCREEN_HEIGHT;
        p.x = MathUtils.random(minX, maxX);
        p.y = randomY ? MathUtils.random(minY, maxY) : minY - 20f;
        p.speedX = MathUtils.random(-15f, 15f);   // slight horizontal drift
        p.speedY = MathUtils.random(minSpeed, maxSpeed);
        p.size = MathUtils.random(minSize, maxSize);
        p.alpha = MathUtils.random(MIN_ALPHA, MAX_ALPHA);
        p.alphaSpeed = MathUtils.random(0.05f, 0.15f) * (MathUtils.randomBoolean() ? 1 : -1);
        p.rotation = MathUtils.random(0f, 180f);
        p.rotationSpeed = isRotating ? fastness * MathUtils.random(10f, 50f) * (MathUtils.randomBoolean() ? 1 : -1) : 0;
    }

    /** Call once per frame inside your screen's render(), before batch.end(). */
    public void update(float delta) {
        if (!enabled) return;
        float maxX = worldMode ? worldX + worldW : SCREEN_WIDTH;
        float maxY = worldMode ? worldY + worldH : SCREEN_HEIGHT;
        float minX = worldMode ? worldX : 0f;
        for (Particle p : particles) {
            p.y += p.speedY * delta;
            p.x += p.speedX * delta;
            p.alpha += p.alphaSpeed * delta;
            p.rotation += p.rotationSpeed * delta;

            if (p.alpha > MAX_ALPHA) { p.alpha = MAX_ALPHA; p.alphaSpeed *= -1; }
            if (p.alpha < MIN_ALPHA) { p.alpha = MIN_ALPHA; p.alphaSpeed *= -1; }

            if (p.y > maxY + 20 || p.x < minX - 20 || p.x > maxX + 20) {
                spawn(p, false);
            }
        }
    }

    /** Call inside batch.begin() / batch.end(). */
    public void draw(SpriteBatch batch) {
        if (!enabled) return;
        for (Particle p : particles) {
            batch.setColor(1f, 1f, 1f, p.alpha);
            batch.draw(p.region, p.x - p.size / 2f, p.y - p.size / 2f, p.size / 2, p.size / 2,
                    p.size, p.size, 1, 1, p.rotation);
        }
        batch.setColor(1f, 1f, 1f, 1f); // reset color after
    }

    public void dispose() {
        for (Texture t : textures) t.dispose();
    }
}
