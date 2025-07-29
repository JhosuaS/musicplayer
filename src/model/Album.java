package model;

import java.util.List;

public class Album {
    private String albumName;
    private int releaseYear;
    private int artist_id;
    private List<Song> songs;
    
    /**
     * Default constructor for Album, builds an empty album. 
     */
    public Album() {}

    /**
     * Constructor for Album, builds an album with the specified parameters.
     * @param albumName
     * @param releaseYear
     * @param artists
     * @param songs
     */
    public Album(String albumName, int releaseYear, int artist_id, List<Song> songs) {
        this.albumName = albumName;
        this.releaseYear = releaseYear;
        this.artist_id = artist_id;
        this.songs = songs;
    }
    
    /**
     * Getters and setters for Album attributes.
     */
    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public int getReleaseYear() {
        return releaseYear;
    }
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    public int getArtist() {
        return artist_id;
    }
    public void setArtists(int artist_id) {
        this.artist_id = artist_id;
    }
    public List<Song> getSongs() {
        return songs;
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    } 
}