package controller;

import model.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class JPlayer {

   public void play (Song song){
         System.out.println("Playing song: " + song.getTitle());
         try {
            InputStream fis = new FileInputStream(song.getPath());
            Player player = new Player(fis);
            new Thread(() -> {
                try {
                    player.play();
                } catch (JavaLayerException e) {
                    System.err.println("Error al reproducir el archivo: " + e.getMessage());
                }
            }).start();
        } catch (FileNotFoundException | JavaLayerException e) {
            System.err.println("Error al abrir o reproducir el archivo: " + e.getMessage());
        }      
   }
   
   public void repeat() {
      //TODO implement repeat functionality
   }

   public void next() {
      //TODO implement pause functionality
   }

   public void back() {
      //TODO implement pause functionality
   }
}
