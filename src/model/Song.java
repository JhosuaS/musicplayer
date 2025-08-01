package model;

public class Song {
    private int id;
    private String title;
    private float duration;
    private float currentPlayTime;
    private String path;
    private int albumId;
    private String artistName;

    /**
     * Default constructor for Song, builds an empty Song.
     */
    public Song () {}

    /**
     * Constructor for Song, builds a Song with a given id, title, duration, and path.
     * @param id
     * @param title
     * @param duration
     * @param path
     */
    public Song(int id, String title, float duration, String path, int albumId, String artistName) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.currentPlayTime = 0f;
        this.path = path;
        this.albumId = albumId;
        this.artistName = artistName;
    }

    /**
     * Getters and Setters for Song
     * @return
     */
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public float getDuration() {
        return duration;
    }
    public void setDuration(float duration) {
        this.duration = duration;
    }
    public float getCurrentPlayTime() {
        return currentPlayTime;
    }
    public void setCurrentPlayTime(float currentPlayTime) {
        this.currentPlayTime = currentPlayTime;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public int getAlbumId() {
        return albumId;
    }
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
    public String getArtistName() {
        return artistName;
    }
}