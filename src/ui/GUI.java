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
    private String[][] realSongs;
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
        this.realSongs = getSongs();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Music Player");
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

        JLabel titleLabel = new JLabel("Song List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] songNames = Arrays.stream(realSongs)
                                .map(c -> c[0])
                                .toArray(String[]::new);
        songList = new JList<>(songNames);
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
        JButton backButton = new JButton("← Back to list");
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        bottomPanel.add(backButton, BorderLayout.WEST);
        backButton.addActionListener(e -> cardLayout.show(cardPanel, "main"));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.setPreferredSize(new Dimension(frame.getWidth(), 70));
        

        JPanel controlsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        prevButton = new JButton("⏮PrevButton");
        playButton = new JButton("▶");
        nextButton = new JButton("⏭NextButton");
        repeatButton = new JButton("🔁RepeatButton");
        volumeDownButton = new JButton("🔉DownVolume");
        volumeUpButton = new JButton("🔊UpVolume");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        Dimension buttonSize = new Dimension(120, 40);
        
        Arrays.asList(prevButton, playButton, nextButton, repeatButton, volumeDownButton, volumeUpButton).forEach(button -> {
            button.setFont(buttonFont);
            button.setPreferredSize(buttonSize);
        });

        configureButtons();

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
            volumeUpButton.setEnabled(false);
            player.volumeUp();
            volumeSlider.setValue((int)(player.getCurrentVolume() * 100));
            new Timer(300, evt -> volumeUpButton.setEnabled(true)).start();
        });

        volumeDownButton.addActionListener(e -> {
            volumeDownButton.setEnabled(false);
            player.volumeDown();
            volumeSlider.setValue((int)(player.getCurrentVolume() * 100));
            new Timer(300, evt -> volumeDownButton.setEnabled(true)).start();
        });

        JPanel controlsWrapper = new JPanel(new BorderLayout());
        controlsWrapper.add(controlsPanel, BorderLayout.NORTH);
        controlsWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.add(controlsWrapper, BorderLayout.NORTH);
        bottomWrapper.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(bottomWrapper, BorderLayout.SOUTH);

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

    private void configureButtons() {
        
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
            playButton.setText("▶ Play");
            progressTimer.stop();
        } else {
            playSelectedSong();
        }
    }

    private void playSelectedSong() {
        int selectedIndex = songList.getSelectedIndex();
        Song selectedSong = createSongFromData(selectedIndex);
        
        player.play(selectedSong);
        playButton.setText("⏸ Pause");
        songInfoLabel.setText("Now playing: " + selectedSong.getTitle());
        
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

    private Song createSongFromData(int index) {
        return new Song(
            realSongs[index][0],
            0,
            realSongs[index][1]
        );
    }

    private String[][] getSongs() {
        return new String[][] {
            {"Song 1", "C:/Users/song1.mp3"},
            {"Song 2", "C:/Users/song2.mp3"},
            {"Song 3", "C:/Users/everlong.mp3"}
        };
    }

    public void show() {
        frame.setVisible(true);
    }
}