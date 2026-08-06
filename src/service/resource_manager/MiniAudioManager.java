package service.resource_manager;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import controller.assets.GameAssetManager;


import java.util.EnumMap;
import java.util.Map;

public class MiniAudioManager implements Disposable {

    private final GameAssetManager gameAssetManager;
    private final Map<MiniAudioEnum, Sound> soundMap;
    private float globalVolume = 1.0f;
    private boolean muted = false;

    public MiniAudioManager() {
        this.gameAssetManager = GameAssetManager.get();
        this.soundMap = new EnumMap<>(MiniAudioEnum.class);
    }

    public void preloadSound(MiniAudioEnum miniAudio) {
        if (miniAudio == null || miniAudio.getFilePath() == null) return;
        gameAssetManager.loadSound(miniAudio.getFilePath());
    }

    public void playSound(MiniAudioEnum miniAudio) {
        playSound(miniAudio, 1.0f);
    }

    public void playSound(MiniAudioEnum miniAudio, float volume) {
        if (muted || miniAudio == null) return;

        String path = miniAudio.getFilePath();
        if (path == null || path.isEmpty()) return;

        Sound sound = soundMap.get(miniAudio);

        if (sound == null) {
            if (!gameAssetManager.isLoaded(path)) {
                gameAssetManager.loadSound(path);
                gameAssetManager.finishLoading();
            }
            sound = gameAssetManager.getSound(path);
            if (sound != null) {
                soundMap.put(miniAudio, sound);
            }
        }

        if (sound != null) {
            float finalVolume = Math.max(0.0f, Math.min(1.0f, globalVolume * volume));
            sound.play(finalVolume);
        }
    }

    public void setGlobalVolume(float volume) {
        this.globalVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public float getGlobalVolume() {
        return globalVolume;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    @Override
    public void dispose() {
        soundMap.clear();
    }
}