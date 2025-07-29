package infra;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;


public class GamepadManager implements Runnable {

    private ControllerManager controllerManager;
    private boolean running = false;
    private GamepadListener listener;

    public GamepadManager(GamepadListener listener) {
        this.listener = listener;
        controllerManager = new ControllerManager();
        controllerManager.initSDLGamepad();
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void stop() {
        running = false;
        controllerManager.quitSDLGamepad();
    }

    @Override
    public void run() {
        while (running) {
            ControllerState state = controllerManager.getState(0);

            if (state.isConnected) {
                if (state.a) listener.onStop();
                
                if(state.rightTrigger > 0.1f) listener.onVolUp();
                if(state.leftTrigger > 0.1f) listener.onVolDown();
               if(state.x) listener.onSelect();
                if (state.dpadLeft) listener.onPrevious();
                if (state.dpadRight) listener.onNext();
                if(state.y) listener.onPreviousPag();
                if(state.dpadDown) listener.onCrossDown();
                if(state.dpadUp) listener.onCrossUp();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}