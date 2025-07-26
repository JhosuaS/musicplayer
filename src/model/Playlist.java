package model;

import java.util.List;
import java.util.ArrayList;

public class Playlist {
    private int id;
    private String name;
    private List<Song> songs = new ArrayList<>();

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

    public void addSong(Song song) {
        if((song != null) && !songs.contains(song)) {
            songs.add(song);
        }
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public Playlist() {}

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
    }

}