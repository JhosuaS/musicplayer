package model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Singer.class, name = "Singer"),
  @JsonSubTypes.Type(value = Writer.class, name = "Writer"),
  @JsonSubTypes.Type(value = Composer.class, name = "Composer")
})

public abstract class Artist {
    private String name;

    public Artist() {}

    public Artist(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
} 
