package controller;

import model.*;
import javazoom.jl.player.Player;
import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javax.sound.sampled.AudioFileFormat;
import java.util.Map;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.decoder.JavaLayerException;

public class JPlayer {
    private Player playerMP3;
    private Thread playbackThread;
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private AtomicBoolean isStopped = new AtomicBoolean(true);
    private float currentVolume = 0.8f;
    private Song currentSong;
    private InputStream currentStream;
    private List<Song> songList;
    private int framesPlayed = 0;
    private final int MS_PER_FRAME = 26;
    private VolumeAudioDevice audioDevice = new VolumeAudioDevice();

    /**
     * Gets the current volume level.
     * 
     * @return Current volume level (0.0 to 1.0)
     */
    public float getCurrentVolume() {
        return currentVolume;
    }

    /**
     * Gets the current playback position in milliseconds.
     * 
     * @return Current position in milliseconds
     */
    public long getCurrentPosition() {
        return framesPlayed * MS_PER_FRAME;
    }

    /**
     * Gets the duration of the current song in milliseconds.
     * 
     * @return Duration in milliseconds, or 0 if no song is loaded
     */
    public long getDuration() {
        if (currentSong == null)
            return 0;
        return (long) (currentSong.getDuration() * 1000);
    }

    /**
     * Sets the list of available songs for playback.
     * 
     * @param songs List of Song objects
     */
    public void setSongList(List<Song> songs) {
        this.songList = songs;
    }

    /**
     * Starts playback of the specified song.
     * 
     * @param song The Song object to play
     */
    public void play(Song song) {
        if (song == null || song.getPath() == null) {
            System.err.println("Invalid song or path");
            return;
        }

        stop();
        currentSong = song;

        try {
            currentSong = song;
            currentSong.setCurrentPlayTime(0);
            long duration = getDurationFromFile(new File(song.getPath()));
            song.setDuration(duration / 1000f);
            framesPlayed = 0;
            currentStream = new BufferedInputStream(new FileInputStream(song.getPath()));
            audioDevice.setVolume(currentVolume);
            playerMP3 = new Player(currentStream, audioDevice);
            isStopped.set(false);
            isPaused.set(false);

            playbackThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Playing: " + song.getTitle());
                        while (!isStopped.get()) {
                            if (!isPaused.get()) {
                                if (playerMP3 == null || !playerMP3.play(1)) {
                                    break;
                                } else {
                                    framesPlayed++;
                                    if (currentSong != null) {
                                        currentSong.setCurrentPlayTime(framesPlayed * MS_PER_FRAME / 1000f);
                                    }
                                }
                            } else {
                                Thread.sleep(100);
                            }
                        }
                    } catch (Exception e) {
                        if (!isStopped.get()) {
                            System.err.println("Playback error: " + e.getMessage());
                        }
                    } finally {
                        closeResources();
                    }
                }
            });

            playbackThread.start();

        } catch (Exception e) {
            System.err.println("Error starting playback: " + e.getMessage());
            closeResources();
        }
    }

    /**
     * Plays the next song in the playlist.
     * Wraps around to the first song if currently playing the last song.
     */
    public void next() {
        if (songList == null || songList.isEmpty()) {
            System.out.println("No song list available");
            return;
        }
        int currentIndex = songList.indexOf(currentSong);
        if (currentIndex < songList.size() - 1) {
            play(songList.get(currentIndex + 1));
        } else {
            play(songList.get(0));
        }
    }

    /**
     * Plays the previous song in the playlist.
     * Wraps around to the last song if currently playing the first song.
     */
    public void previous() {
        if (songList == null || songList.isEmpty()) {
            System.out.println("No song list available");
            return;
        }

        int currentIndex = songList.indexOf(currentSong);
        if (currentIndex > 0) {
            play(songList.get(currentIndex - 1));
        } else {
            play(songList.get(songList.size() - 1));
        }

    }

    /**
     * Gets the duration of an audio file in milliseconds.
     * 
     * @param file The audio file to check
     * @return Duration in milliseconds, or 0 if duration cannot be determined
     */
    public long getDurationFromFile(File file) {
        try {
            MpegAudioFileReader reader = new MpegAudioFileReader();
            AudioFileFormat format = reader.getAudioFileFormat(file);
            Map<?, ?> props = format.properties();
            Long durationMicroseconds = (Long) props.get("duration");
            return durationMicroseconds / 1000;
        } catch (Exception e) {
            System.err.println("Could not read file duration: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Pauses the current playback.
     */
    public void pause() {
        if (playerMP3 != null && !isStopped.get() && !isPaused.get()) {
            isPaused.set(true);
            System.out.println("Standing");
        }
    }

    /**
     * Stops the current playback and releases resources.
     */
    public void stop() {
        isStopped.set(true);
        isPaused.set(false);
        try {
            if (playbackThread != null && playbackThread.isAlive()) {
                playbackThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeResources();
    }

    /**
     * Sets the playback volume.
     * 
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        if (volume < 0f)
            volume = 0f;
        if (volume > 1f)
            volume = 1f;

        currentVolume = volume;
        audioDevice.setVolume(volume);
        System.out.println("Volume set to: " + (int) (volume * 100) + "%");
    }

    /**
     * Increases the volume by 10%.
     */
    public void volumeUp() {
        setVolume(Math.round((currentVolume + 0.1f) * 10) / 10.0f);
    }

    /**
     * Decreases the volume by 10%.
     */
    public void volumeDown() {
        setVolume(Math.round((currentVolume - 0.1f) * 10) / 10.0f);
    }

    /**
     * Repeats the current song by restarting playback.
     */
    public void repeat() {
        if (currentSong != null) {
            play(currentSong);
        }
    }

    /**
     * Closes and releases all playback resources.
     */
    private void closeResources() {
        try {
            if (playerMP3 != null) {
                playerMP3.close();
                playerMP3 = null;
            }
            if (currentStream != null) {
                currentStream.close();
                currentStream = null;
            }
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    /**
     * Checks if playback is currently active.
     * 
     * @return true if playing, false otherwise
     */
    public boolean isPlaying() {
        return !isStopped.get() && !isPaused.get();
    }

    /**
     * Checks if playback is currently paused.
     * 
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused.get();
    }

    /**
     * Gets the currently playing song.
     * 
     * @return Current Song object, or null if no song is playing
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    /**
     * Gets the list of available songs.
     * 
     * @return List of Song objects
     */
    public List<Song> getSongList() {
        return songList;
    }

    public void resume() {
        if (currentSong == null || currentSong.getPath() == null) {
            System.err.println("No song loaded to resume.");
            return;
        }

        if (!isPaused.get()) {
            System.out.println("Player is not paused.");
            return;
        }

        try {
            File audioFile = new File(currentSong.getPath());

            long resumeMs = getCurrentPosition();
            long bytesPerMs = estimateBytesPerMillisecond(audioFile);
            long safeResumeMs = Math.max(0, resumeMs - 1000);
            long skipBytes = safeResumeMs * bytesPerMs;

            System.out.println("Resuming from ~" + resumeMs + "ms, skipping " + skipBytes + " bytes");

            InputStream fileStream = new FileInputStream(audioFile);
            currentStream = new BufferedInputStream(fileStream);

            long actuallySkipped = currentStream.skip(skipBytes);
            if (actuallySkipped < skipBytes) {
                System.err.println("Skipped less than expected: " + actuallySkipped + " bytes");
            }

            playerMP3 = new Player(currentStream);
            isStopped.set(false);
            isPaused.set(false);

            playbackThread = new Thread(() -> {
                try {
                    while (!isStopped.get()) {
                        if (!isPaused.get()) {
                            if (playerMP3 == null || !playerMP3.play(1)) {
                                break;
                            } else {
                                framesPlayed++;
                                if (currentSong != null) {
                                    currentSong.setCurrentPlayTime(framesPlayed * MS_PER_FRAME / 1000f);
                                }
                            }
                        } else {
                            Thread.sleep(100);
                        }
                    }
                } catch (Exception e) {
                    if (!isStopped.get()) {
                        System.err.println("Error during resume playback: " + e.getMessage());
                    }
                } finally {
                    closeResources();
                }
            });

            playbackThread.start();
        } catch (Exception e) {
            System.err.println("Failed to resume: " + e.getMessage());
            closeResources();
        }
    }

    private long estimateBytesPerMillisecond(File file) throws IOException {
        long fileSize = file.length();
        long duration = getDurationFromFile(file);
        if (duration == 0)
            return 0;
        return fileSize / duration;
    }
}