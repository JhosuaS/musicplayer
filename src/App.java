import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import controller.JPlayer;
import model.*;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Song currentSong = new Song();
        try {
         ObjectMapper mapper = new ObjectMapper();
         List<Album> albums = mapper.readValue(new File("src/data/album.json"), new TypeReference<List<Album>>() {});
        
         for (Album album : albums) {
             System.out.println("Album: " + album.getAlbumName() + ", Release Year: " + album.getReleaseYear());
             for (Song song : album.getSongs()) {
                 System.out.println("  Song: " + song.getTitle() + ", Duration: " + song.getDuration() + " minutes");
                 currentSong = song;
                 break; // Assuming we want to play the first song of the first album TODO cambiar logica de reproduccion
             }
         }
         JPlayer player = new JPlayer();
         player.play(currentSong);
         //player.pause();
         //top();
      } catch (Exception e) {
         e.printStackTrace();
      }
    }
}
