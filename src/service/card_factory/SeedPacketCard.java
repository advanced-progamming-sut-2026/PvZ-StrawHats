package service.card_factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class SeedPacketCard extends Stack {

    private static final float PLANT_SCALE_W = 0.83f;
    private static final float PLANT_SCALE_H = 0.83f;
    private static final float OFFSET_X = -40f;
    private static final float OFFSET_Y = 7f;

    private static final float CLIP_INSET = 1f;

    private final String name;
    private final String plantIconFile;
    private final String packetSkinFile;

    SeedPacketCard(String name, String plantIconFile, String packetSkinFile,
                   Texture packetTexture, Texture plantTexture,
                   float cardWidth, float cardHeight) {
        this.name = name;
        this.plantIconFile = plantIconFile;
        this.packetSkinFile = packetSkinFile;

        float aspect = (packetTexture != null && packetTexture.getWidth() > 0)
                ? (float) packetTexture.getHeight() / (float) packetTexture.getWidth()
                : (cardHeight / cardWidth);
        float actualHeight = cardWidth * aspect;

        setSize(cardWidth, actualHeight);

        Image packetImage = new Image(new TextureRegionDrawable(packetTexture));
        add(packetImage);

        Image plantImage = new Image(new TextureRegionDrawable(plantTexture));
        plantImage.setScaling(Scaling.fit);
        plantImage.setAlign(Align.center);

        Container<Image> plantContainer = new Container<>(plantImage);
        plantContainer.size(cardWidth * PLANT_SCALE_W, actualHeight * PLANT_SCALE_H);
        plantContainer.align(Align.top);

        if (OFFSET_X < 0) {
            plantContainer.padRight(-OFFSET_X);
        } else {
            plantContainer.padLeft(OFFSET_X);
        }
        plantContainer.padTop(OFFSET_Y);

        add(plantContainer);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        boolean transform = isTransform();

        if (transform) {
            applyTransform(batch, computeTransform());
        }

        // انقباض ۱ واحدی محدوده برش از چهار طرف
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

    public String getName() {
        return name;
    }

    public String getPlantIconFile() {
        return plantIconFile;
    }

    public String getPacketSkinFile() {
        return packetSkinFile;
    }
}