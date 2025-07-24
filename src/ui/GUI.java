package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import model.*;
import controller.JPlayer;

public class GUI {
    private JPlayer player;
    private JFrame frame;
    private JLabel songInfoLabel;
    private JList<String> songList;
    private String[][] cancionesReales;
    private JButton playButton, volumeUpButton, volumeDownButton;
    private JButton repeatButton, nextButton, prevButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel mainView;
    private JPanel playerView;
    private JProgressBar progressBar;
    private JSlider volumeSlider;
    private Timer progressTimer;

    public GUI(JPlayer player) {
        this.player = player;
        this.cancionesReales = obtenerCanciones();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Reproductor de Música");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        mainView = createMainView();
        playerView = createPlayerView();

        cardPanel.add(mainView, "main");
        cardPanel.add(playerView, "player");

        frame.add(cardPanel);
        setupProgressTimer();
    }

    private JPanel createMainView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Lista de Canciones", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] nombresCanciones = Arrays.stream(cancionesReales)
                                        .map(c -> c[0])
                                        .toArray(String[]::new);
        songList = new JList<>(nombresCanciones);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setFont(new Font("Arial", Font.PLAIN, 18));
        songList.setSelectedIndex(0);
        
        songList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    playSelectedSong();
                    cardLayout.show(cardPanel, "player");
                }
            }
        });
        
        panel.add(new JScrollPane(songList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlayerView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        songInfoLabel = new JLabel("", SwingConstants.CENTER);
        songInfoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(songInfoLabel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("← Volver a la lista");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));
        panel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel controlsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        prevButton = new JButton("⏮");
        playButton = new JButton("▶");
        nextButton = new JButton("⏭");
        repeatButton = new JButton("🔁");
        volumeDownButton = new JButton("🔉");
        volumeUpButton = new JButton("🔊");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Dimension buttonSize = new Dimension(120, 40);
        
        Arrays.asList(prevButton, playButton, nextButton, repeatButton, volumeDownButton, volumeUpButton).forEach(button -> {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        });

        configurarBotones();

        volumeSlider = new JSlider(0, 100, (int)(player.getCurrentVolume() * 100));
        volumeSlider.setPreferredSize(new Dimension(120, 20));
        volumeSlider.setPaintTrack(true);
        volumeSlider.addChangeListener(e -> {
            if (!volumeSlider.getValueIsAdjusting()) {
                player.setVolume(volumeSlider.getValue() / 100f);
            }
        });

        JPanel volumeControlPanel = new JPanel(new BorderLayout(5, 0));
        volumeControlPanel.add(volumeDownButton, BorderLayout.WEST);
        volumeControlPanel.add(volumeSlider, BorderLayout.CENTER);
        volumeControlPanel.add(volumeUpButton, BorderLayout.EAST);

        controlsPanel.add(prevButton);
        controlsPanel.add(playButton);
        controlsPanel.add(nextButton);
        controlsPanel.add(volumeDownButton);
        controlsPanel.add(volumeControlPanel);
        controlsPanel.add(volumeUpButton);
        controlsPanel.add(new JLabel());
        controlsPanel.add(repeatButton);
        controlsPanel.add(new JLabel());

        volumeUpButton.addActionListener(e -> {
            player.volumeUp();
            volumeSlider.setValue((int)(player.getCurrentVolume() * 100));
        });

        volumeDownButton.addActionListener(e -> {
            player.volumeDown();
            volumeSlider.setValue((int)(player.getCurrentVolume() * 100));
        });

        JPanel controlsWrapper = new JPanel(new BorderLayout());
        controlsWrapper.add(controlsPanel, BorderLayout.NORTH);
        controlsWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

        panel.add(controlsWrapper, BorderLayout.SOUTH);

        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(0, 150, 255));
        progressBar.setBackground(new Color(220, 220, 220));
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(600, 30));
        progressBar.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        progressPanel.add(progressBar);
        panel.add(progressPanel, BorderLayout.CENTER);

        return panel;
    }

    private void configurarBotones() {
        playButton.addActionListener(e -> togglePlayPause());
        
        nextButton.addActionListener(e -> {
            int nextIndex = songList.getSelectedIndex() + 1;
            if (nextIndex < songList.getModel().getSize()) {
                songList.setSelectedIndex(nextIndex);
                playSelectedSong();
            }
        });

        prevButton.addActionListener(e -> {
            int prevIndex = songList.getSelectedIndex() - 1;
            if (prevIndex >= 0) {
                songList.setSelectedIndex(prevIndex);
                playSelectedSong();
            }
        });

        repeatButton.addActionListener(e -> player.repeat());
        volumeUpButton.addActionListener(e -> player.volumeUp());
        volumeDownButton.addActionListener(e -> player.volumeDown());
    }

    private void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            playButton.setText("▶ Reproducir");
            progressTimer.stop();
        } else {
            playSelectedSong();
        }
    }

    private void playSelectedSong() {
        int selectedIndex = songList.getSelectedIndex();
        Song selectedSong = crearSongDesdeDatos(selectedIndex);
        
        player.play(selectedSong);
        playButton.setText("⏸ Pausa");
        songInfoLabel.setText("Reproduciendo: " + selectedSong.getTitle());
        
        progressTimer.start();
    }

    private void setupProgressTimer() {
        progressTimer = new Timer(500, e -> {
            if (player.isPlaying()) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                
                progressBar.setMaximum((int)duration);
                progressBar.setValue((int)position);
                
                String timeText = String.format("%d:%02d / %d:%02d",
                    (position / 60000), ((position % 60000) / 1000),
                    (duration / 60000), ((duration % 60000) / 1000));
                progressBar.setString(timeText);
            }
        });
    }

    private Song crearSongDesdeDatos(int index) {
        return new Song(
            cancionesReales[index][0],
            0,
            cancionesReales[index][1]
        );
    }

    private String[][] obtenerCanciones() {
        return new String[][] {
            {"Canción 1", "C:/Users/cancion1.mp3"},
            {"Canción 2", "C:/Users/cancion2.mp3"},
            {"Canción 3", "C:/Users/everlong.mp3"}
        };
    }

    public void show() {
        frame.setVisible(true);
    }
}