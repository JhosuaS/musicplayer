package data;

import model.Playlist;
import model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private final String dbPath = "jdbc:sqlite:src/data/music.db";

    /**
     * Creates a new playlist in the database.
     * @param name
     * @return The ID of the created playlist, or -1 if creation failed.
     * @throws SQLException
     */
    public int createPlaylist(String name) {
        String sql = "INSERT INTO playlist (name) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(dbPath);
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Adds a song to a playlist in the database.
     * @param playlistId
     * @param songId
     * @throws SQLException
     */
    public void addSongToPlaylist(int playlistId, int songId) {
        String sql = "INSERT OR IGNORE INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a song from a playlist in the database.
     * @param playlistId
     * @param songId
     * @throws SQLException
     */
    public void removeSongFromPlaylist(int playlistId, int songId){
        String sql = "DELETE FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all playlists from the database.
     * @return A list of all playlists.
     * @throws SQLException
     */
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlist";
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                playlists.add(new Playlist(resultSet.getInt("id"), resultSet.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    /**
     * Retrieves all songs from a specific playlist.
     * @param playlistId
     * @return A list of songs in the specified playlist.
     * @throws SQLException
     */
    public List<Song> getSongsFromPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String sql = """
            SELECT s.*
            FROM song s
            INNER JOIN playlist_song ps ON s.id = ps.song_id
            WHERE ps.playlist_id = ?
        """;
        try (Connection conn = DriverManager.getConnection(dbPath);
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, playlistId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                songs.add(new Song(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getFloat("duration"),
                    resultSet.getString("path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    /**
     * Gets the next available playlist number based on the current count of playlists.
     * @return The next playlist number, returns 1 if no playlists exist.
     * @throws SQLException
     */
    public int getNextPlaylistNumber() {
        String sql = "SELECT COUNT(*) AS count FROM playlist";
        try (Connection conn = DriverManager.getConnection(dbPath);
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt("count") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
    }
        return 1;
    }
}

