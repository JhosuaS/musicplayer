package infra;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;


public class GamepadManager implements Runnable {

    private ControllerManager controllerManager;
    private boolean running = false;
    private GamepadListener listener;

    /**
     * Constructs a new GamepadManager with the specified listener.
     * Initializes the SDL gamepad system.
     * 
     * @param listener The GamepadListener to receive controller events
     */
    public GamepadManager(GamepadListener listener) {
        this.listener = listener;
        controllerManager = new ControllerManager();
        controllerManager.initSDLGamepad();
    }

    /**
     * Starts the gamepad monitoring thread.
     * The thread will begin polling controller state until stopped.
     */
    public void start() {
        running = true;
        new Thread(this).start();
    }

    /**
     * Stops the gamepad monitoring thread.
     * Shuts down the SDL gamepad system.
     */
    public void stop() {
        running = false;
        controllerManager.quitSDLGamepad();
    }

    /**
     * Main gamepad polling loop.
     * Continuously checks controller state and triggers appropriate listener callbacks.
     * Runs at approximately 10Hz (100ms sleep between polls).
     */
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