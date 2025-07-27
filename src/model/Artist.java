package model;

public abstract class Artist {
    private String name;

    /**
     * Default constructor for Artist, builds an empty Artist.
     */
    public Artist() {}

    /**
     * Constructor for Artist, builds an Artist with a given name.  
     * @param name
     */
    public Artist(String name) {
        this.name = name;
    }
    
    /**
     Getters and Setters for Artist
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
} 
