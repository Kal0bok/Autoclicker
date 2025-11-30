package autoclicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AutoClickerFrame extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    private JComboBox<String> clickButtonCombo;
    private JTextField startKeyField, stopKeyField;
    private JButton startButton, stopButton;
    private JLabel statusLabel;
    
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
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createHotkeyPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createControlPanel());
        
        add(mainPanel, BorderLayout.CENTER);
        add(createStatusLabel(), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JPanel createTimePanel() {
        // ... same as previous commit
    }
    
    private JPanel createButtonPanel() {
        // ... same as previous commit
    }
    
    private JPanel createHotkeyPanel() {
        // ... same as previous commit
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Start (F6)");
        stopButton = new JButton("Stop (F7)");
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        return controlPanel;
    }
    
    private JLabel createStatusLabel() {
        statusLabel = new JLabel("Ready to work");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        return statusLabel;
    }
    
    private void setupKeyCapture(JTextField field, String type) {
        // ... same as previous commit
    }
}