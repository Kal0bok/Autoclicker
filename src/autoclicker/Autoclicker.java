package autoclicker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Autoclicker extends JFrame {
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner, millisecondsSpinner;
    private JComboBox<String> clickButtonCombo;
    private JTextField startKeyField, stopKeyField;
    private JButton startButton, stopButton;
    private JLabel statusLabel;
    
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Robot robot;
    private Thread clickThread;
    
    // Коды клавиш для кликов
    private String[] clickButtons = {"ЛКМ", "ПКМ", "СКМ"};
    private int[] clickButtonCodes = {InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON3_DOWN_MASK, InputEvent.BUTTON2_DOWN_MASK};
    
    public Autoclicker() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(this, "Ошибка создания Robot: " + e.getMessage());
            System.exit(1);
        }
        
        initializeUI();
        setupEventListeners();
    }
    
    private void initializeUI() {
        setTitle("Автокликер");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Панель настроек времени
        JPanel timePanel = new JPanel(new GridLayout(2, 4, 5, 5));
        timePanel.setBorder(new TitledBorder("Интервал кликов"));
        
        timePanel.add(new JLabel("Часы:", SwingConstants.RIGHT));
        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        timePanel.add(hoursSpinner);
        
        timePanel.add(new JLabel("Минуты:", SwingConstants.RIGHT));
        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(minutesSpinner);
        
        timePanel.add(new JLabel("Секунды:", SwingConstants.RIGHT));
        secondsSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 59, 1));
        timePanel.add(secondsSpinner);
        
        timePanel.add(new JLabel("Милисекунды:", SwingConstants.RIGHT));
        millisecondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 50));
        timePanel.add(millisecondsSpinner);
        
        // Панель выбора кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(new TitledBorder("Настройки клика"));
        
        buttonPanel.add(new JLabel("Кнопка мыши:"));
        clickButtonCombo = new JComboBox<>(clickButtons);
        buttonPanel.add(clickButtonCombo);
        
        // Панель горячих клавиш
        JPanel hotkeyPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        hotkeyPanel.setBorder(new TitledBorder("Горячие клавиши"));
        
        hotkeyPanel.add(new JLabel("Клавиша старта:"));
        startKeyField = new JTextField("F6");
        startKeyField.setEditable(false);
        hotkeyPanel.add(startKeyField);
        
        hotkeyPanel.add(new JLabel("Клавиша остановки:"));
        stopKeyField = new JTextField("F7");
        stopKeyField.setEditable(false);
        hotkeyPanel.add(stopKeyField);
        
        // Добавляем слушатели для захвата клавиш
        setupKeyCapture(startKeyField, "start");
        setupKeyCapture(stopKeyField, "stop");
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton = new JButton("Старт (F6)");
        stopButton = new JButton("Стоп (F7)");
        stopButton.setEnabled(false);
        
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        
        // Статусная панель
        statusLabel = new JLabel("Готов к работе");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Собираем интерфейс
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(timePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(hotkeyPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(controlPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void setupEventListeners() {
        startButton.addActionListener(e -> startClicking());
        stopButton.addActionListener(e -> stopClicking());
        
        // Глобальные горячие клавиши
        setupGlobalHotkeys();
    }
    
    private void setupKeyCapture(JTextField field, String type) {
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                field.setText("Нажмите клавишу...");
                field.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        String keyText = KeyEvent.getKeyText(e.getKeyCode());
                        field.setText(keyText);
                        field.removeKeyListener(this);
                        
                        // Обновляем текст кнопок
                        if (type.equals("start")) {
                            startButton.setText("Старт (" + keyText + ")");
                        } else {
                            stopButton.setText("Стоп (" + keyText + ")");
                        }
                    }
                });
            }
        });
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
            JOptionPane.showMessageDialog(this, "Установите интервал больше 0!");
            return;
        }
        
        isRunning.set(true);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        statusLabel.setText("Работает...");
        
        int buttonIndex = clickButtonCombo.getSelectedIndex();
        final int buttonMask = clickButtonCodes[buttonIndex];
        
        clickThread = new Thread(() -> {
            try {
                while (isRunning.get()) {
                    robot.mousePress(buttonMask);
                    Thread.sleep(50); // Короткая задержка между нажатием и отпусканием
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
        statusLabel.setText("Остановлено");
        
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
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new Autoclicker().setVisible(true);
        });
    }
}