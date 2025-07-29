package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import model.*;
import data.SongDAO;
import java.util.List;
import controller.JPlayer;

public class GUI {
    private JPlayer player;
    private JFrame frame;
    private JLabel songInfoLabel;
    private JList<String> songList;
    private List<Song> allSongs;
    private JButton playButton, volumeUpButton, volumeDownButton;
    private JButton repeatButton, nextButton, prevButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel mainView;
    private JPanel playerView;
    private JProgressBar progressBar;
    private JSlider volumeSlider;
    private Timer progressTimer;

    /**
     * Constructs a new GUI with the specified player controller.
     * 
     * @param player The JPlayer instance to control playback
     */
    public GUI(JPlayer player) {
        this.player = player;
        initialize();
    }

    /**
     * Loads all songs from the database and sets them in the player.
     */
    private void loadSongsFromDatabase() {
        SongDAO songDAO = new SongDAO();
        allSongs = songDAO.getAllSongs();
        player.setSongList(allSongs);
    }

    /**
     * Initializes the GUI components and layout.
     */
    private void initialize() {
        loadSongsFromDatabase();
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

    /**
     * Creates the main view panel showing all available songs.
     * 
     * @return The configured JPanel containing the song list
     */
    private JPanel createMainView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultListModel<String> allSongsModel = new DefaultListModel<>();
        for (Song s : allSongs)
            allSongsModel.addElement(s.getTitle());
        songList = new JList<>(allSongsModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane allSongsScroll = new JScrollPane(songList);

        songList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    playSelectedSong();
                    cardLayout.show(cardPanel, "player");
                }
            }
        });

        panel.add(new JLabel("All the songs", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(allSongsScroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the player view panel with playback controls.
     * 
     * @return The configured JPanel containing player controls
     */
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

        Arrays.asList(prevButton, playButton, nextButton, repeatButton, volumeDownButton, volumeUpButton)
                .forEach(button -> {
                    button.setFont(buttonFont);
                    button.setPreferredSize(buttonSize);
                });

        configureButtons();

        volumeSlider = new JSlider(0, 100, (int) (player.getCurrentVolume() * 100));
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
            volumeSlider.setValue((int) (player.getCurrentVolume() * 100));
            new Timer(300, evt -> volumeUpButton.setEnabled(true)).start();
        });

        volumeDownButton.addActionListener(e -> {
            volumeDownButton.setEnabled(false);
            player.volumeDown();
            volumeSlider.setValue((int) (player.getCurrentVolume() * 100));
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

    /**
     * Configures the action listeners for all control buttons.
     */
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

    /**
     * Toggles between play and pause states.
     */
    private void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            playButton.setText("▶ Resart");
            progressTimer.stop();
        } else if (player.isPaused()) {
            player.resume();
            player.stop();
            progressTimer.start();
        } else {
            playSelectedSong();
        }
    }

    /**
     * Plays the currently selected song in the list.
     */
    private void playSelectedSong() {
        int selectedIndex = songList.getSelectedIndex();
        Song selectedSong = createSongFromData(selectedIndex);

        player.play(selectedSong);
        playButton.setText("Stop");
        songInfoLabel.setText("Now playing: " + selectedSong.getTitle() + " / " + selectedSong.getArtistName());

        progressTimer.start();
    }

    /**
     * Sets up the timer for updating the progress bar.
     */
    private void setupProgressTimer() {
        progressTimer = new Timer(500, e -> {
            if (player.isPlaying()) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();
                progressBar.setMaximum((int) duration);
                progressBar.setValue((int) position);

                String timeText = String.format("%d:%02d / %d:%02d",
                        (position / 60000), ((position % 60000) / 1000),
                        (duration / 60000), ((duration % 60000) / 1000));
                progressBar.setString(timeText);
            }
        });
    }

    /**
     * Gets the Song object from the data at the specified index.
     * 
     * @param index The index of the song in the list
     * @return The Song object at the specified index
     */
    private Song createSongFromData(int index) {
        return allSongs.get(index);
    }

    /**
     * Makes the GUI visible.
     */
    public void show() {
        frame.setVisible(true);
    }

}