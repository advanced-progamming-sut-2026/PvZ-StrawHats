package service.card_factory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * A single finished seed packet card: a seed packet background with a plant's
 * icon set on top of it, grouped together into one actor.
 * <p>
 * The card is named after the plant whose icon it uses - e.g. the card built
 * from "akee.png" is named "akee" (see {@link #getName()}).
 */
public class SeedPacketCard extends Stack {

    private final String name;
    private final String plantIconFile;
    private final String packetSkinFile;

    SeedPacketCard(String name, String plantIconFile, String packetSkinFile,
                   Texture packetTexture, Texture plantTexture,
                   float cardWidth, float cardHeight) {
        this.name = name;
        this.plantIconFile = plantIconFile;
        this.packetSkinFile = packetSkinFile;

        setSize(cardWidth, cardHeight);

        // The seed packet is the background and fills the whole card.
        Image packetImage = new Image(new TextureRegionDrawable(packetTexture));
        add(packetImage);

        // The plant icon sits on top of the packet, scaled down and nudged
        // toward the upper part of the packet (the usual seed-packet layout:
        // plant art on top, cost/name area left clear near the bottom).
        Image plantImage = new Image(new TextureRegionDrawable(plantTexture));
        plantImage.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        plantImage.setAlign(Align.center);

        Container<Image> plantContainer = new Container<>(plantImage);
        plantContainer.size(cardWidth * 0.62f, cardHeight * 0.55f);
        plantContainer.align(Align.top);
        plantContainer.padTop(cardHeight * 0.12f);

        add(plantContainer);
    }

    /** The plant name this card is named after (e.g. "akee" for "akee.png"). */
    public String getName() {
        return name;
    }

    /** File name (inside assets/images/ui/plants_ui) of the plant icon used for this card. */
    public String getPlantIconFile() {
        return plantIconFile;
    }

    /** File name (inside assets/images/ui/seedpackets_ui) of the packet background used for this card. */
    public String getPacketSkinFile() {
        return packetSkinFile;
    }
}
