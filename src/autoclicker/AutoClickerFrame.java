package autoclicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AutoClickerFrame extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    private JComboBox<String> clickButtonCombo;
    private JTextField startKeyField, stopKeyField;
    
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
        
        add(mainPanel, BorderLayout.CENTER);
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
        JPanel hotkeyPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        hotkeyPanel.setBorder(BorderFactory.createTitledBorder("Hotkeys"));
        
        hotkeyPanel.add(new JLabel("Start key:"));
        startKeyField = new JTextField("F6");
        startKeyField.setEditable(false);
        hotkeyPanel.add(startKeyField);
        
        hotkeyPanel.add(new JLabel("Stop key:"));
        stopKeyField = new JTextField("F7");
        stopKeyField.setEditable(false);
        hotkeyPanel.add(stopKeyField);
        
        setupKeyCapture(startKeyField, "start");
        setupKeyCapture(stopKeyField, "stop");
        
        return hotkeyPanel;
    }
    
    private void setupKeyCapture(JTextField field, String type) {
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                field.setText("Press any key...");
                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        String keyText = KeyEvent.getKeyText(e.getKeyCode());
                        field.setText(keyText);
                        field.removeKeyListener(this);
                    }
                });
            }
        });
    }
}