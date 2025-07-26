package data;

import model.Song;
import java.sql.*;
import java.util.*;

public class SongDAO {
    private final String dbPath = "jdbc:sqlite:src/data/music.db";

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbPath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM song")) {
            while (resultSet.next()) {
                Song song = new Song(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getFloat("duration"),
                    resultSet.getString("path")
                );
                songs.add(song);
            }
        } catch (SQLException e) {
            System.err.println("Error reading songs: " + e.getMessage());
        }
        return songs;
    } 
}