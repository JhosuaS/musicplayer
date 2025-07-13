package model;

import java.util.List;

public class Playlist {
    private List<Song> playlistName = new java.util.ArrayList<>();

    public List<Song> getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(List<Song> playlistName) {
        this.playlistName = playlistName;
    }

    public void addSong(Song song) {
        if (song != null && !playlistName.contains(song)) {
            playlistName.add(song);
        }
    }

    public void removeSong(Song song) {
        playlistName.remove(song);
    }
}