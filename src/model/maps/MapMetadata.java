package model.maps;

public final class MapMetadata {

    private final int tileWidthPx;
    private final int tileHeightPx;
    private final int lawnOriginX;
    private final int lawnOriginY;

    public MapMetadata(int tileWidthPx, int tileHeightPx, int lawnOriginX, int lawnOriginY) {
        this.tileWidthPx = tileWidthPx;
        this.tileHeightPx = tileHeightPx;
        this.lawnOriginX = lawnOriginX;
        this.lawnOriginY = lawnOriginY;
    }

    public int tileWidthPx() {
        return tileWidthPx;
    }

    public int tileHeightPx() {
        return tileHeightPx;
    }

    public int cellPixelX(int col) {
        return lawnOriginX + col * tileWidthPx;
    }

    public int cellPixelY(int row) {
        return lawnOriginY + row * tileHeightPx;
    }
}
