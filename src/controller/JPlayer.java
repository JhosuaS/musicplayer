package controller;

import model.*;
import javazoom.jl.player.Player;
import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import javax.sound.sampled.AudioFileFormat;
import java.util.Map;


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


    public float getCurrentVolume() {
        return currentVolume;
    }

    public long getCurrentPosition() {
        return framesPlayed * MS_PER_FRAME;
    }

    public long getDuration() {
        if (currentSong == null) return 0;
        return (long)(currentSong.getDuration() * 1000);
    }

    public void setSongList(List<Song> songs) {
        this.songList = songs;
    }

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
            playerMP3 = new Player(currentStream);
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

    public void pause() {
        if (playerMP3 != null && !isStopped.get() && !isPaused.get()) {
            isPaused.set(true);
            System.out.println("Standing");
        }
    }

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