package controller;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;

/**
 * Custom audio device implementation that adds volume control capability.
 * Extends JavaSoundAudioDevice to modify audio samples with volume adjustment.
 */
public class VolumeAudioDevice extends JavaSoundAudioDevice {
    private float volume = 1.0f;

    /**
     * Sets the playback volume level.
     * 
     * @param volume The volume level to set (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Writes audio samples to the device with volume adjustment applied.
     * 
     * @param samples The array of audio samples
     * @param offs The offset in the array where samples start
     * @param len The number of samples to write
     * @throws JavaLayerException If an error occurs during writing
     */
    @Override
    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
        for (int i = offs; i < offs + len; i++) {
            samples[i] = (short) (samples[i] * volume);
        }
        super.writeImpl(samples, offs, len);
    }
}
