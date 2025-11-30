package autoclicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoClickerFrame extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    private JComboBox<String> clickButtonCombo;
    private JTextField startKeyField, stopKeyField;
    private JButton startButton, stopButton;
    private JLabel statusLabel;
    
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Robot robot;
    private Thread clickThread;
    private int[] clickButtonCodes = {InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON3_DOWN_MASK, InputEvent.BUTTON2_DOWN_MASK};
    
    public AutoClickerFrame() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(this, "Robot creation error: " + e.getMessage());
            System.exit(1);
        }
        initializeFrame();
        initializeUI();
        setupEventListeners();
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
                        
                        // Update button text
                        if (type.equals("start")) {
                            startButton.setText("Start (" + keyText + ")");
                        } else {
                            stopButton.setText("Stop (" + keyText + ")");
                        }
                    }
                });
            }
        });
    }
    
    private void setupEventListeners() {
        startButton.addActionListener(e -> startClicking());
        stopButton.addActionListener(e -> stopClicking());
        setupGlobalHotkeys();
    }
    
    private void setupGlobalHotkeys() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                String currentKey = KeyEvent.getKeyText(e.getKeyCode());
                
                if (currentKey.equals(startKeyField.getText()) && !isRunning.get()) {
                    startClicking();
                } else if (currentKey.equals(stopKeyField.getText()) && isRunning.get()) {
                    stopClicking();
                }
            }
            return false;
        });
    }
    
    private void startClicking() {
        if (isRunning.get()) return;
        
        long totalMillis = calculateTotalMilliseconds();
        if (totalMillis <= 0) {
            JOptionPane.showMessageDialog(this, "Set interval greater than 0!");
            return;
        }
        
        isRunning.set(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        statusLabel.setText("Running...");
        
        int buttonIndex = clickButtonCombo.getSelectedIndex();
        final int buttonMask = clickButtonCodes[buttonIndex];
        
        clickThread = new Thread(() -> {
            try {
                while (isRunning.get()) {
                    robot.mousePress(buttonMask);
                    Thread.sleep(50);
                    robot.mouseRelease(buttonMask);
                    Thread.sleep(totalMillis);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
        
        clickThread.start();
    }
    
    private void stopClicking() {
        isRunning.set(false);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Stopped");
        
        if (clickThread != null && clickThread.isAlive()) {
            clickThread.interrupt();
        }
    }
    
    private long calculateTotalMilliseconds() {
        int hours = (Integer) hoursSpinner.getValue();
        int minutes = (Integer) minutesSpinner.getValue();
        int seconds = (Integer) secondsSpinner.getValue();
        int millis = (Integer) millisecondsSpinner.getValue();
        
        return hours * 3600000L + minutes * 60000L + seconds * 1000L + millis;
    }
}