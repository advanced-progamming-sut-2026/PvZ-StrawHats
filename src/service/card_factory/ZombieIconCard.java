package service.card_factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class ZombieIconCard extends Stack {

    private static final float ICON_SCALE_W = 0.83f;
    private static final float ICON_SCALE_H = 0.83f;

    private static final float CLIP_INSET = 1f;

    private final String alias;
    private final String iconFile;

    ZombieIconCard(String alias, String iconFile, Texture frameTexture, Texture iconTexture,
                   float cardWidth, float cardHeight) {
        this.alias = alias;
        this.iconFile = iconFile;

        float actualHeight = cardHeight;

        setSize(cardWidth, actualHeight);

        if (frameTexture != null) {
            Image frameImage = new Image(new TextureRegionDrawable(frameTexture));
            add(frameImage);
        }

        Image iconImage = new Image(new TextureRegionDrawable(iconTexture));
        iconImage.setScaling(Scaling.fit);
        iconImage.setAlign(Align.center);

        Container<Image> iconContainer = new Container<>(iconImage);
        iconContainer.size(cardWidth * ICON_SCALE_W, actualHeight * ICON_SCALE_H);
        iconContainer.align(Align.center);

        add(iconContainer);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        boolean transform = isTransform();

        if (transform) {
            applyTransform(batch, computeTransform());
        }

        float clipX = (transform ? 0 : getX()) + CLIP_INSET;
        float clipY = (transform ? 0 : getY()) + CLIP_INSET;
        float clipW = Math.max(0, getWidth() - (CLIP_INSET * 2f));
        float clipH = Math.max(0, getHeight() - (CLIP_INSET * 2f));

        if (clipBegin(clipX, clipY, clipW, clipH)) {
            drawChildren(batch, parentAlpha);
            batch.flush();
            clipEnd();
        } else {
            drawChildren(batch, parentAlpha);
        }

        if (transform) {
            resetTransform(batch);
        }
    }

    public String getAlias() {
        return alias;
    }

    public String getIconFile() {
        return iconFile;
    }
}