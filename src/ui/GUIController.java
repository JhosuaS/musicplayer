package ui;

import infra.GamepadListener;
import infra.GamepadManager;

public class GUIController implements GamepadListener {
    private GUI gui;
    private GamepadManager gamepadManager;

    public GUIController(GUI gui) {
        this.gui = gui;
        this.gamepadManager = new GamepadManager(this);
        this.gamepadManager.start();
    }

    @Override
    public void onCrossUp(){
        gui.scrollUp();
    }
    @Override
    public void onCrossDown(){
        gui.scrollDown();
    }

    @Override
    public void onVolDown() {
        gui.volumeDownButton();
    }

    @Override
    public void onVolUp() {
        gui.volumeUpButton();
    }

    @Override
    public void onPreviousPag() {
        gui.backButton();
    }
    @Override
    public void onSelect(){
        gui.playSelectedSongGamepad();
    }
    
    @Override
    public void onStop() {
        gui.stopButton();
    }

    @Override
    public void onNext() {
        gui.nextMusic();
    }

    @Override
    public void onPrevious() {
        gui.prevMusic();
    }

    public void shutdown() {
        gamepadManager.stop();
    }
}


