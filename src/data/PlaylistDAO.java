package data;

import model.Playlist;
import model.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private final String dbPath = "jdbc:sqlite:src/data/music.db";

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

