import controller.JPlayer;
import ui.GUI;
import ui.GUIController;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Crea el reproductor real
        JPlayer player = new JPlayer();

        // Crea la GUI principal
        GUI gui = new GUI(player);

        // Crea el controlador del gamepad y pásale la GUI
        GUIController guiController = new GUIController(gui);

        // Ejecuta la GUI en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            gui.show();
        });
    }
}
