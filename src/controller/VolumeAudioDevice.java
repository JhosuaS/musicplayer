package controller;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;

public class VolumeAudioDevice extends JavaSoundAudioDevice {
    private float volume = 1.0f;

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    @Override
    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
        for (int i = offs; i < offs + len; i++) {
            samples[i] = (short) (samples[i] * volume);
        }
        super.writeImpl(samples, offs, len);
    }
}
