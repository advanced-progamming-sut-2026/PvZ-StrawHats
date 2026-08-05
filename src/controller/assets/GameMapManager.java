package controller.assets;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import model.maps.MapMetadata;
import model.match.main.levels.Level;
import model.match.main.season.Season;

import java.util.HashMap;
import java.util.Map;

public final class GameMapManager {

    private static GameMapManager instance;

    private final TmxMapLoader mapLoader = new TmxMapLoader();
    private final Map<String, TiledMap> loadedMaps = new HashMap<>();

    private GameMapManager() {
    }

    public static GameMapManager getInstance() {
        if (instance == null) {
            instance = new GameMapManager();
        }
        return instance;
    }

    public TiledMap loadForSeason(Season season) {
        return loadMap(AssetPaths.seasonMapPath(season.getName()));
    }

    public TiledMap loadForLevel(Level level) {
        return loadForSeason(level.getSeason());
    }

    public TiledMap loadMap(String tmxPath) {
        return loadedMaps.computeIfAbsent(tmxPath, mapLoader::load);
    }

    public TiledMap getIfLoaded(String tmxPath) {
        return loadedMaps.get(tmxPath);
    }

    public boolean isLoaded(String tmxPath) {
        return loadedMaps.containsKey(tmxPath);
    }

    public MapMetadata extractMetadata(TiledMap map) {
        var props = map.getProperties();
        int tileWidth = props.get("tilewidth", 0, Integer.class);
        int tileHeight = props.get("tileheight", 0, Integer.class);
        int lawnOriginX = props.get("lawnOriginX", 0, Integer.class);
        int lawnOriginY = props.get("lawnOriginY", 0, Integer.class);
        return new MapMetadata(tileWidth, tileHeight, lawnOriginX, lawnOriginY);
    }

    public void unload(String tmxPath) {
        TiledMap map = loadedMaps.remove(tmxPath);
        if (map != null) {
            map.dispose();
        }
    }

    public void disposeAll() {
        loadedMaps.values().forEach(TiledMap::dispose);
        loadedMaps.clear();
    }
}
