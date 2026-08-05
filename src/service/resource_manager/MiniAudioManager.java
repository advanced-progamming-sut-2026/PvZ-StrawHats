package service.resource_manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;

public class MiniAudioManager implements Disposable {

    public static final float DEFAULT_VOLUME = 1.0f;

    private final Map<MiniAudioEnum, Sound> soundCache;
    private final Map<MiniAudioEnum, Long> activeSoundIds;

    private float volume;
    private boolean muted;
    private boolean enabled;

    public MiniAudioManager() {
        this.soundCache = new EnumMap<>(MiniAudioEnum.class);
        this.activeSoundIds = new EnumMap<>(MiniAudioEnum.class);
        this.volume = DEFAULT_VOLUME;
        this.muted = false;
        this.enabled = true;
    }

    public void loadAll() {
        for (MiniAudioEnum audio : MiniAudioEnum.values()) {
            load(audio);
        }
    }

    public void load(MiniAudioEnum audio) {
        if (!soundCache.containsKey(audio)) {
            if (Gdx.files.internal(audio.getFilePath()).exists()) {
                Sound sound = Gdx.audio.newSound(Gdx.files.internal(audio.getFilePath()));
                soundCache.put(audio, sound);
            }
        }
    }

    public long play(MiniAudioEnum audio) {
        return play(audio, this.volume, false);
    }

    public long play(MiniAudioEnum audio, float customVolume) {
        return play(audio, customVolume, false);
    }

    public long playLooping(MiniAudioEnum audio) {
        return play(audio, this.volume, true);
    }

    public long play(MiniAudioEnum audio, float playVolume, boolean loop) {
        if (!enabled || muted) {
            return -1;
        }

        Sound sound = soundCache.get(audio);
        if (sound == null) {
            load(audio);
            sound = soundCache.get(audio);
        }

        if (sound != null) {
            float effectiveVolume = Math.max(0.0f, Math.min(1.0f, playVolume));
            long soundId;
            if (loop) {
                soundId = sound.loop(effectiveVolume);
            } else {
                soundId = sound.play(effectiveVolume);
            }
            activeSoundIds.put(audio, soundId);
            return soundId;
        }
        return -1;
    }

    public void stop(MiniAudioEnum audio) {
        Sound sound = soundCache.get(audio);
        if (sound != null) {
            sound.stop();
            activeSoundIds.remove(audio);
        }
    }

    public void stopAll() {
        for (Sound sound : soundCache.values()) {
            sound.stop();
        }
        activeSoundIds.clear();
    }

    public void increaseVolume(float delta) {
        setVolume(this.volume + delta);
    }

    public void decreaseVolume(float delta) {
        setVolume(this.volume - delta);
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        for (Map.Entry<MiniAudioEnum, Long> entry : activeSoundIds.entrySet()) {
            Sound sound = soundCache.get(entry.getKey());
            if (sound != null) {
                sound.setVolume(entry.getValue(), this.volume);
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    public void mute() {
        this.muted = true;
        stopAll();
    }

    public void unmute() {
        this.muted = false;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopAll();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void resetToDefault() {
        this.volume = DEFAULT_VOLUME;
        this.muted = false;
        this.enabled = true;
    }

    public void update(float delta) {
    }

    @Override
    public void dispose() {
        stopAll();
        for (Sound sound : soundCache.values()) {
            sound.dispose();
        }
        soundCache.clear();
        activeSoundIds.clear();
    }
}