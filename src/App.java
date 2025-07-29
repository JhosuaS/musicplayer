import controller.JPlayer;
import ui.*;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {
        JPlayer player = new JPlayer();
        GUI gui = new GUI(player);
        GUIController guiController = new GUIController(gui);

        try {
            SwingUtilities.invokeLater(() -> {
                gui.show();
            });

      } catch (Exception e) {
         e.printStackTrace();
      }
    }
}
