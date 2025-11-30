package autoclicker;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Autoclicker {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new AutoClickerFrame().setVisible(true);
        });
    }
}
