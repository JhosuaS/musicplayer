package data;

import model.Song;
import java.sql.*;
import java.util.*;

public class SongDAO {
    private final String dbPath = "jdbc:sqlite:src/data/music.db";

    /**
     * Retrieves all songs from the database.
     * @throws SQLException
     * @return A list of all songs.
     */
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dbPath);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT song.*, artist.name AS artist_name " + 
                                                          "FROM song " +
                                                          "JOIN album_artist ON song.album_id = album_artist.album_id " + 
                                                          "JOIN artist ON album_artist.artist_id = artist.id")) {
            while (resultSet.next()) {
                Song song = new Song(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getFloat("duration"),
                    resultSet.getString("path"),
                    resultSet.getInt("album_id"),
                    resultSet.getString("artist_name")
                );
                songs.add(song);
            }
        } catch (SQLException e) {
            System.err.println("Error reading songs: " + e.getMessage());
        }
        return songs;
    } 
}