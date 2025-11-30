package autoclicker;

import javax.swing.*;
import java.awt.*;

public class AutoClickerFrame extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    
    public AutoClickerFrame() {
        initializeFrame();
        initializeUI();
    }
    
    private void initializeFrame() {
        setTitle("Auto Clicker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
    }
    
    private void initializeUI() {
        // Time settings panel
        JPanel timePanel = createTimePanel();
        
        JPanel mainPanel = new JPanel();
        mainPanel.add(timePanel);
        
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
    
    private JPanel createTimePanel() {
        JPanel timePanel = new JPanel(new GridLayout(2, 4, 5, 5));
        timePanel.setBorder(BorderFactory.createTitledBorder("Click Interval"));
        
        timePanel.add(new JLabel("Hours:", SwingConstants.RIGHT));
        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        timePanel.add(hoursSpinner);
        
        timePanel.add(new JLabel("Minutes:", SwingConstants.RIGHT));
        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(minutesSpinner);
        
        timePanel.add(new JLabel("Seconds:", SwingConstants.RIGHT));
        secondsSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 59, 1));
        timePanel.add(secondsSpinner);
        
        timePanel.add(new JLabel("Milliseconds:", SwingConstants.RIGHT));
        millisecondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 50));
        timePanel.add(millisecondsSpinner);
        
        return timePanel;
    }
}