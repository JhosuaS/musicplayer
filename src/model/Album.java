package model;

import java.util.List;

public class Album {
    private String albumName;
    private int releaseYear;
    private List<Artist> artists;
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
    public Album(String albumName, int releaseYear, List<Artist> artists, List<Song> songs) {
        this.albumName = albumName;
        this.releaseYear = releaseYear;
        this.artists = artists;
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
    public List<Artist> getArtists() {
        return artists;
    }
    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
    public List<Song> getSongs() {
        return songs;
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    } 
}