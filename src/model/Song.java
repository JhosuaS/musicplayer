package model;

public class Song {
    private int id;
    private String title;
    private float duration;
    private float currentPlayTime;
    private String path;

    public Song () {}

    public Song(int id, String title, float duration, String path) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.currentPlayTime = 0f;
        this.path = path;
    }

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



}