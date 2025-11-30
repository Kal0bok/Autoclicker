package autoclicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AutoClickerFrame extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    private JComboBox<String> clickButtonCombo;
    
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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(createTimePanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createButtonPanel());
        
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
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Click Settings"));
        
        buttonPanel.add(new JLabel("Mouse button:"));
        clickButtonCombo = new JComboBox<>(new String[]{"Left Button", "Right Button", "Middle Button"});
        buttonPanel.add(clickButtonCombo);
        
        return buttonPanel;
    }
}