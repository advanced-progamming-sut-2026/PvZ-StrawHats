package model.match_mechanisms;

import model.collections.zombie.Zombie;

import java.util.List;

public class ZombieWave {
    private List<Zombie> waveZombies;
    private int delay;
    private int waveTurn;

    public List<Zombie> getWaveZombies() {
        return waveZombies;
    }

    public void setWaveZombies(List<Zombie> waveZombies) {
        this.waveZombies = waveZombies;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getWaveTurn() {
        return waveTurn;
    }

    public void setWaveTurn(int waveTurn) {
        this.waveTurn = waveTurn;
    }
}
