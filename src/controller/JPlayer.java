package controller;

import model.*;
import javazoom.jl.player.Player;
import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class JPlayer {
    private Player playerMP3;
    private Thread playbackThread;
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private AtomicBoolean isStopped = new AtomicBoolean(true);
    private float currentVolume = 0.8f;
    private Song currentSong;
    private InputStream currentStream;
    private long pausePosition;
    private boolean shouldResume;
    private List<Song> songList;
    private boolean shuffleMode = false;
    private Random random = new Random();

    public float getCurrentVolume() {
        return currentVolume;
    }

    public long getCurrentPosition() {
        try {
            return playerMP3 != null ? playerMP3.getPosition() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public long getDuration() {
        if (currentSong == null) return 0;
        return (long)(currentSong.getDuration() * 1000);
    }

    public void setSongList(List<Song> songs) {
        this.songList = songs;
    }

    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
        System.out.println("Shuffle mode " + (shuffleMode ? "enabled" : "disabled"));
    }

    public boolean isShuffleMode() {
        return shuffleMode;
    }

    private Song getRandomSong() {
        if (songList == null || songList.isEmpty()) {
            return null;
        }
        
        if (songList.size() == 1) {
            return songList.get(0);
        }
        
        Song randomSong;
        do {
            randomSong = songList.get(random.nextInt(songList.size()));
        } while (randomSong.equals(currentSong) && songList.size() > 1);
        
        return randomSong;
    }

    public void play(Song song) {
        if (song == null || song.getPath() == null) {
            System.err.println("Invalid song or path");
            return;
        }

        if (shouldResume && song.equals(currentSong)) {
            resume();
            return;
        }

        stop();
        shouldResume = false;

        try {
            currentSong = song;
            currentSong.setCurrentPlayTime(0);
            currentStream = new BufferedInputStream(new FileInputStream(song.getPath()));
            playerMP3 = new Player(currentStream);
            isStopped.set(false);
            isPaused.set(false);

            playbackThread = new Thread(() -> {
                try {
                    System.out.println("Playing: " + song.getTitle());
                    while (!isStopped.get()) {
                        if (!isPaused.get()) {
                            if (!playerMP3.play(1)) {
                                if (shuffleMode && songList != null && !songList.isEmpty()) {
                                    play(getRandomSong());
                                }
                                break;
                            }
                            if (currentSong != null) {
                                currentSong.setCurrentPlayTime(playerMP3.getPosition() / 1000f);
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
            });

            playbackThread.start();

        } catch (Exception e) {
            System.err.println("Error starting playback: " + e.getMessage());
            closeResources();
        }
    }

    public void next() {
        if (songList == null || songList.isEmpty()) {
            System.out.println("No song list available");
            return;
        }

        if (shuffleMode) {
            play(getRandomSong());
        } else {
            int currentIndex = songList.indexOf(currentSong);
            if (currentIndex < songList.size() - 1) {
                play(songList.get(currentIndex + 1));
            } else {
                play(songList.get(0));
            }
        }
    }

    public void previous() {
        if (songList == null || songList.isEmpty()) {
            System.out.println("No song list available");
            return;
        }

        if (shuffleMode) {
            play(getRandomSong());
        } else {
            int currentIndex = songList.indexOf(currentSong);
            if (currentIndex > 0) {
                play(songList.get(currentIndex - 1));
            } else {
                play(songList.get(songList.size() - 1));
            }
        }
    }

    private void resume() {
        try {
            currentStream = new BufferedInputStream(new FileInputStream(currentSong.getPath()));
            playerMP3 = new Player(currentStream);
            
            long toSkip = pausePosition;
            while (toSkip > 0) {
                toSkip -= currentStream.skip(toSkip);
            }

            isStopped.set(false);
            isPaused.set(false);
            shouldResume = false;

            playbackThread = new Thread(() -> {
                try {
                    System.out.println("Resuming: " + currentSong.getTitle());
                    while (!isStopped.get()) {
                        if (!isPaused.get()) {
                            if (!playerMP3.play(1)) {
                                break;
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
            });

            playbackThread.start();
            System.out.println("Volume control initialized");

        } catch (Exception e) {
            System.err.println("Error resuming playback: " + e.getMessage());
            closeResources();
        }
    }

    public void pause() {
        if (playerMP3 != null && !isStopped.get() && !isPaused.get()) {
            try {
                pausePosition = playerMP3.getPosition();
                isPaused.set(true);
                shouldResume = true;
                System.out.println("Paused");
            } catch (Exception e) {
                System.err.println("Error pausing: " + e.getMessage());
            }
        }
    }

    public void stop() {
        isStopped.set(true);
        isPaused.set(false);
        shouldResume = false;
        closeResources();
    }

    public void setVolume(float volume) {
        if (volume < 0f) volume = 0f;
        if (volume > 1f) volume = 1f;
        
        currentVolume = volume;
        System.out.println("Volume set to: " + (int)(volume * 100) + "%");
    }

    public void volumeUp() {
        setVolume(Math.round((currentVolume + 0.1f) * 10) / 10.0f); 
    }

    public void volumeDown() {
        setVolume(Math.round((currentVolume - 0.1f) * 10) / 10.0f); 
    }

    public void repeat() {
        if (currentSong != null) {
            play(currentSong);
        }
    }

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

    public boolean isPlaying() {
        return !isStopped.get() && !isPaused.get();
    }

    public boolean isPaused() {
        return isPaused.get();
    }
    
    public Song getCurrentSong() {
        return currentSong;
    }
    
    public List<Song> getSongList() {
        return songList;
    }
}