package service.resource_manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;

public class AudioManager implements Disposable {

    public static final float DEFAULT_VOLUME = 0.8f;

    private final Map<AudioEnum, Music> musicCache;

    private AudioEnum currentAudio;
    private Music currentMusic;

    private float volume;
    private boolean muted;
    private boolean enabled;
    private boolean looping;

    public AudioManager() {
        this.musicCache = new EnumMap<>(AudioEnum.class);
        this.volume = DEFAULT_VOLUME;
        this.muted = false;
        this.enabled = true;
        this.looping = true;
    }

    public void loadAll() {
        for (AudioEnum audio : AudioEnum.values()) {
            load(audio);
        }
    }

    public void load(AudioEnum audio) {
        if (!musicCache.containsKey(audio)) {
            if (Gdx.files.internal(audio.getFilePath()).exists()) {
                Music music = Gdx.audio.newMusic(Gdx.files.internal(audio.getFilePath()));
                musicCache.put(audio, music);
            }
        }
    }

    public void play(AudioEnum audio) {
        play(audio, this.looping);
    }

    public void play(AudioEnum audio, boolean loop) {
        if (!enabled || muted) {
            this.currentAudio = audio;
            return;
        }

        if (currentAudio == audio && currentMusic != null && currentMusic.isPlaying()) {
            return;
        }

        stopCurrent();

        Music music = musicCache.get(audio);
        if (music == null) {
            load(audio);
            music = musicCache.get(audio);
        }

        if (music != null) {
            this.currentAudio = audio;
            this.currentMusic = music;
            this.currentMusic.setVolume(this.volume);
            this.currentMusic.setLooping(loop);
            this.currentMusic.play();
        }
    }

    public void playLooping(AudioEnum audio) {
        play(audio, true);
    }

    public void pause() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    public void resume() {
        if (enabled && !muted && currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    public void stop() {
        stopCurrent();
        this.currentAudio = null;
    }

    public void stopAll() {
        for (Music music : musicCache.values()) {
            if (music.isPlaying()) {
                music.stop();
            }
        }
        this.currentMusic = null;
        this.currentAudio = null;
    }

    private void stopCurrent() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void increaseVolume(float delta) {
        setVolume(this.volume + delta);
    }

    public void decreaseVolume(float delta) {
        setVolume(this.volume - delta);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(this.volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void mute() {
        this.muted = true;
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    public void unmute() {
        this.muted = false;
        if (enabled && currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopCurrent();
        } else if (currentAudio != null && !muted) {
            play(currentAudio, looping);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        if (currentMusic != null) {
            currentMusic.setLooping(looping);
        }
    }

    public boolean isLooping() {
        return looping;
    }

    public void resetToDefault() {
        this.volume = DEFAULT_VOLUME;
        this.muted = false;
        this.enabled = true;
        this.looping = true;
        if (currentMusic != null) {
            currentMusic.setVolume(DEFAULT_VOLUME);
        }
    }

    public void update(float delta) {
    }

    @Override
    public void dispose() {
        stopAll();
        for (Music music : musicCache.values()) {
            music.dispose();
        }
        musicCache.clear();
    }
}