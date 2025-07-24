package ui;

import javax.swing.*;
import java.awt.*;
import model.Song;
import controller.JPlayer;

public class GUI {
    private JPlayer player;
    private JFrame frame;
    private JLabel songInfoLabel;

    public GUI(JPlayer player) {
        this.player = player;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Reproductor de Música");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        songInfoLabel = new JLabel("Selecciona una canción", SwingConstants.CENTER);
        panel.add(songInfoLabel, BorderLayout.NORTH);

        String[] sampleSongs = {"Canción 1", "Canción 2", "Canción 3"};
        JList<String> songList = new JList<>(sampleSongs);
        panel.add(new JScrollPane(songList), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton playButton = new JButton("▶ Reproducir");
        JButton pauseButton = new JButton("⏸ Pausa");
        JButton nextButton = new JButton("⏭ Siguiente");
        JButton prevButton = new JButton("⏮ Anterior");

        playButton.addActionListener(e -> {
            Song demoSong = new Song();
            demoSong.setTitle(songList.getSelectedValue());
            demoSong.setPath("ruta/ejemplo.mp3");
            player.play(demoSong);
            songInfoLabel.setText("Reproduciendo: " + demoSong.getTitle());
        });

        controlPanel.add(prevButton);
        controlPanel.add(playButton);
        controlPanel.add(pauseButton);
        controlPanel.add(nextButton);

        panel.add(controlPanel, BorderLayout.SOUTH);
        frame.add(panel);
    }

    public void show() {
        frame.setVisible(true);
    }
}