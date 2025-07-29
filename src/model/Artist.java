package model;

public abstract class Artist {
    private String name;
    private int id;

    /**
     * Default constructor for Artist, builds an empty Artist.
     */
    public Artist() {}

    /**
     * Constructor for Artist, builds an Artist with a given name.  
     * @param name
     */
    public Artist(String name, int id) {
        this.name = name;
        this.id = id;
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
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
} 
