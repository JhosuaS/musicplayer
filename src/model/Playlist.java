package model;

import java.util.List;
import java.util.ArrayList;

public class Playlist {
    private int id;
    private String name;
    private List<Song> songs = new ArrayList<>();

    /**
     * Adds a song to the playlist if it is not already present.
     * @param song
     */
    public void addSong(Song song) {
        if((song != null) && !songs.contains(song)) {
            songs.add(song);
        }
    }

    /**
     * Removes a song from the playlist.
     * @param song
     */
    public void removeSong(Song song) {
        songs.remove(song);
    }

    /**
     * Default constructor for Playlist, builds an empty Playlist.
     */
    public Playlist() {}

    /**
     * Constructor for Playlist, builds a Playlist with a given id and name.
     * @param id
     * @param name
     */
    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Getters and Setters for Playlist
     * @return
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

}