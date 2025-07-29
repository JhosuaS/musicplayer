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
    gui.scrollListaArriba();
}
@Override
public void onCrossDown(){
    gui.scrollListaAbajo();
}

    @Override
    public void onVolDown() {
        gui.BajarVolumen();
    }

    @Override
    public void onVolUp() {
        gui.SubirVolumen();
    }

    @Override
    public void onPreviousPag() {
        gui.BackMenus();
    }
@Override
public void onSelect(){
    gui.playSelectedSongExternamente();
}
    
    @Override
    public void onStop() {
        gui.PlayPause();
    }

    @Override
    public void onNext() {
        gui.NextMusic();
    }

    @Override
    public void onPrevious() {
        gui.PrevMusic();
    }

    public void shutdown() {
        gamepadManager.stop();
    }
}
