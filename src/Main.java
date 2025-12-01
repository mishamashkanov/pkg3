import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

// Главный класс для запуска приложения
public class Main {
    public static void main(String[] args) {
        // Запускаем приложение в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Устанавливаем современный Look and Feel
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                // Если Nimbus не доступен, используем системный стиль
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // Создаем и показываем главное окно
            new ModernImageLab();
        });
    }
}

// Главное окно приложения
class ModernImageLab extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage processedImage;
    private ImagePanel originalPanel;
    private ImagePanel processedPanel;
    private ControlPanel controlPanel;
    private HistogramPanel histogramPanel;

    public ModernImageLab() {
        initUI();
    }

    private void initUI() {
        setTitle("ImageLab Pro - Обработка изображений (Вариант 16)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 800));

        // Устанавливаем иконку приложения
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
        } catch (Exception e) {
            // Иконка не найдена - используем стандартную
        }

        // Создание главной панели с градиентным фоном
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(28, 28, 30),
                        getWidth(), getHeight(), new Color(45, 45, 48));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Создание верхней панели с логотипом
        JPanel topPanel = createTopPanel();

        // Создание центральной панели с изображениями
        JPanel centerPanel = createCenterPanel();

        // Создание панели управления
        controlPanel = new ControlPanel(this);

        // Создание панели гистограммы
        histogramPanel = new HistogramPanel();

        // Правая панель с элементами управления
        JPanel rightPanel = new JPanel(new BorderLayout(15, 15));
        rightPanel.setOpaque(false);
        rightPanel.add(controlPanel, BorderLayout.NORTH);
        rightPanel.add(histogramPanel, BorderLayout.CENTER);

        // Добавление всех компонентов
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);

        // Центрирование и отображение окна
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Логотип и заголовок
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        // Иконка приложения
        JLabel iconLabel = new JLabel("🖼️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        titlePanel.add(iconLabel);

        // Заголовок
        JLabel titleLabel = new JLabel("ImageLab Pro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(66, 134, 244));
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Лабораторная работа 3 - Вариант 16");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(180, 180, 180));
        titlePanel.add(subtitleLabel);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton loadBtn = createModernButton("📁 Загрузить", new Color(66, 134, 244));
        JButton saveBtn = createModernButton("💾 Сохранить", new Color(52, 168, 83));
        JButton resetBtn = createModernButton("↻ Сбросить", new Color(249, 167, 62));
        JButton helpBtn = createModernButton("❓ Помощь", new Color(155, 89, 182));

        loadBtn.addActionListener(e -> loadImage());
        saveBtn.addActionListener(e -> saveImage());
        resetBtn.addActionListener(e -> resetImage());
        helpBtn.addActionListener(e -> showHelpDialog());

        buttonPanel.add(loadBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(helpBtn);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(baseColor.brighter());
                } else {
                    g2.setColor(baseColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);

        originalPanel = new ImagePanel("Оригинальное изображение");
        processedPanel = new ImagePanel("Обработанное изображение");

        panel.add(originalPanel);
        panel.add(processedPanel);

        return panel;
    }

    // Методы для работы с изображениями
    public void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Изображения", "jpg", "jpeg", "png", "bmp", "gif"));
        fileChooser.setDialogTitle("Выберите изображение");
        fileChooser.setApproveButtonText("Открыть");

        // Настраиваем внешний вид JFileChooser
        UIManager.put("FileChooser.background", new Color(45, 45, 48));
        UIManager.put("FileChooser.foreground", Color.WHITE);
        SwingUtilities.updateComponentTreeUI(fileChooser);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                processedImage = deepCopy(originalImage);

                originalPanel.setImage(originalImage);
                processedPanel.setImage(processedImage);

                histogramPanel.calculateHistogram(processedImage);
                histogramPanel.repaint();

                controlPanel.enableControls(true);

                // Показываем уведомление
                showNotification("Изображение успешно загружено!", new Color(52, 168, 83));

            } catch (IOException e) {
                showErrorDialog("Ошибка загрузки изображения", e.getMessage());
            } catch (Exception e) {
                showErrorDialog("Ошибка", "Неверный формат изображения");
            }
        }
    }

    public void saveImage() {
        if (processedImage == null) {
            showWarningDialog("Нет обработанного изображения для сохранения");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить изображение");
        fileChooser.setSelectedFile(new File("обработанное_изображение.png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG изображения", "png"));
        fileChooser.setApproveButtonText("Сохранить");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".png")) {
                    file = new File(file.getAbsolutePath() + ".png");
                }

                ImageIO.write(processedImage, "png", file);
                showNotification("Изображение успешно сохранено!", new Color(52, 168, 83));

            } catch (IOException e) {
                showErrorDialog("Ошибка сохранения", e.getMessage());
            }
        }
    }

    public void resetImage() {
        if (originalImage != null) {
            processedImage = deepCopy(originalImage);
            processedPanel.setImage(processedImage);
            histogramPanel.calculateHistogram(processedImage);
            histogramPanel.repaint();
            controlPanel.resetControls();
            showNotification("Изображение сброшено", new Color(249, 167, 62));
        }
    }

    public void applyContrast(float contrast, int brightness) {
        if (originalImage == null) return;

        processedImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        long startTime = System.currentTimeMillis();

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Color color = new Color(originalImage.getRGB(x, y));

                // Линейное контрастирование: I_out = a * I_in + b
                int red = clamp((int)(contrast * color.getRed() + brightness));
                int green = clamp((int)(contrast * color.getGreen() + brightness));
                int blue = clamp((int)(contrast * color.getBlue() + brightness));

                processedImage.setRGB(x, y, new Color(red, green, blue).getRGB());
            }
        }

        long endTime = System.currentTimeMillis();
        processedPanel.setImage(processedImage);
        histogramPanel.calculateHistogram(processedImage);
        histogramPanel.repaint();

        showNotification(String.format("Контрастирование применено (%.1f сек)",
                (endTime - startTime) / 1000.0), new Color(66, 134, 244));
    }

    public void applyMorphology(String operation, String shape, int size) {
        if (originalImage == null) return;

        long startTime = System.currentTimeMillis();

        // Конвертируем в оттенки серого для морфологических операций
        BufferedImage grayImage = convertToGrayscale(originalImage);

        // Создаем структурирующий элемент (ядро)
        float[][] kernel = createKernel(shape, size);

        // Применяем выбранную операцию
        switch (operation) {
            case "Эрозия":
                processedImage = applyErosion(grayImage, kernel);
                break;
            case "Дилатация":
                processedImage = applyDilation(grayImage, kernel);
                break;
            case "Открытие":
                processedImage = applyOpening(grayImage, kernel);
                break;
            case "Закрытие":
                processedImage = applyClosing(grayImage, kernel);
                break;
            default:
                processedImage = deepCopy(originalImage);
        }

        long endTime = System.currentTimeMillis();
        processedPanel.setImage(processedImage);
        histogramPanel.calculateHistogram(processedImage);
        histogramPanel.repaint();

        showNotification(String.format("Морфология '%s' применена (%.1f сек)",
                operation, (endTime - startTime) / 1000.0), new Color(52, 168, 83));
    }

    private BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage gray = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics g = gray.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return gray;
    }

    private float[][] createKernel(String shape, int size) {
        float[][] kernel = new float[size][size];
        int center = size / 2;

        switch (shape) {
            case "Квадрат":
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        kernel[i][j] = 1.0f;
                    }
                }
                break;

            case "Круг":
                float radius = size / 2.0f;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        float distance = (float) Math.sqrt(
                                Math.pow(i - center, 2) + Math.pow(j - center, 2));
                        kernel[i][j] = (distance <= radius) ? 1.0f : 0.0f;
                    }
                }
                break;

            case "Крест":
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        kernel[i][j] = (i == center || j == center) ? 1.0f : 0.0f;
                    }
                }
                break;
        }

        return kernel;
    }

    private BufferedImage applyErosion(BufferedImage image, float[][] kernel) {
        return applyMorphologicalOperation(image, kernel, true);
    }

    private BufferedImage applyDilation(BufferedImage image, float[][] kernel) {
        return applyMorphologicalOperation(image, kernel, false);
    }

    private BufferedImage applyOpening(BufferedImage image, float[][] kernel) {
        BufferedImage eroded = applyErosion(image, kernel);
        return applyDilation(eroded, kernel);
    }

    private BufferedImage applyClosing(BufferedImage image, float[][] kernel) {
        BufferedImage dilated = applyDilation(image, kernel);
        return applyErosion(dilated, kernel);
    }

    private BufferedImage applyMorphologicalOperation(BufferedImage image, float[][] kernel, boolean isErosion) {
        int width = image.getWidth();
        int height = image.getHeight();
        int kernelSize = kernel.length;
        int kernelRadius = kernelSize / 2;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = kernelRadius; y < height - kernelRadius; y++) {
            for (int x = kernelRadius; x < width - kernelRadius; x++) {
                int min = 255;
                int max = 0;

                // Проходим по области ядра
                for (int ky = -kernelRadius; ky <= kernelRadius; ky++) {
                    for (int kx = -kernelRadius; kx <= kernelRadius; kx++) {
                        if (kernel[ky + kernelRadius][kx + kernelRadius] > 0) {
                            Color color = new Color(image.getRGB(x + kx, y + ky));
                            int gray = color.getRed(); // Для grayscale все каналы одинаковы

                            if (isErosion) {
                                min = Math.min(min, gray);
                            } else {
                                max = Math.max(max, gray);
                            }
                        }
                    }
                }

                int value = isErosion ? min : max;
                Color newColor = new Color(value, value, value);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private void showHelpDialog() {
        String helpText = "<html><div style='font-family: Segoe UI;'>" +
                "<h2 style='color: #4286f4;'>ImageLab Pro - Справка</h2>" +
                "<p><b>Линейное контрастирование:</b><br>" +
                "• <b>Контраст (a):</b> Увеличивает/уменьшает разницу между светлыми и тёмными областями<br>" +
                "• <b>Яркость (b):</b> Добавляет постоянное значение ко всем пикселям<br>" +
                "Формула: I<sub>out</sub> = a × I<sub>in</sub> + b</p>" +
                "<p><b>Морфологические операции:</b><br>" +
                "• <b>Эрозия:</b> Уменьшает объекты, удаляет мелкие детали<br>" +
                "• <b>Дилатация:</b> Увеличивает объекты, заполняет мелкие разрывы<br>" +
                "• <b>Открытие:</b> Эрозия + Дилатация (удаление шума)<br>" +
                "• <b>Закрытие:</b> Дилатация + Эрозия (заполнение отверстий)</p>" +
                "<p><b>Рекомендации:</b><br>" +
                "• Для контрастирования используйте тёмные/светлые изображения<br>" +
                "• Для морфологии используйте бинарные или текстурные изображения</p></div></html>";

        JOptionPane.showMessageDialog(this, helpText, "Справка",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showNotification(String message, Color color) {
        // Можно реализовать красивые уведомления, но для простоты используем JOptionPane
        JOptionPane.showMessageDialog(this, message, "Уведомление",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private BufferedImage deepCopy(BufferedImage image) {
        return new BufferedImage(
                image.getColorModel(),
                image.copyData(null),
                image.isAlphaPremultiplied(),
                null);
    }
}

// Панель для отображения изображений
class ImagePanel extends JPanel {
    private BufferedImage image;
    private String title;

    public ImagePanel(String title) {
        this.title = title;
        setPreferredSize(new Dimension(600, 500));
        setBackground(new Color(35, 35, 38));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Рисуем фон с градиентом
        GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 43),
                getWidth(), getHeight(), new Color(35, 35, 38));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Рисуем заголовок
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);

        // Фон для заголовка
        g2d.setColor(new Color(66, 134, 244, 100));
        g2d.fillRoundRect((getWidth() - titleWidth - 30) / 2, 15,
                titleWidth + 30, 30, 15, 15);

        g2d.setColor(Color.WHITE);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 35);

        // Рисуем изображение
        if (image != null) {
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            // Вычисляем масштаб для вписывания в панель
            float scale = Math.min(
                    (float)(getWidth() - 60) / imgWidth,
                    (float)(getHeight() - 80) / imgHeight
            );

            int scaledWidth = (int)(imgWidth * scale);
            int scaledHeight = (int)(imgHeight * scale);
            int x = (getWidth() - scaledWidth) / 2;
            int y = (getHeight() - scaledHeight) / 2 + 20;

            // Тень
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRoundRect(x + 5, y + 5, scaledWidth, scaledHeight, 10, 10);

            // Изображение
            g2d.drawImage(image, x, y, scaledWidth, scaledHeight, this);

            // Рамка
            g2d.setColor(new Color(66, 134, 244));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, scaledWidth, scaledHeight, 10, 10);

            // Информация об изображении
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String info = String.format("%d × %d пикселей | %s",
                    imgWidth, imgHeight,
                    image.getColorModel().hasAlpha() ? "RGB+Alpha" : "RGB");

            int infoWidth = g2d.getFontMetrics().stringWidth(info);
            g2d.setColor(new Color(180, 180, 180));
            g2d.drawString(info, (getWidth() - infoWidth) / 2, y + scaledHeight + 25);

        } else {
            // Сообщение при отсутствии изображения
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            String message = "Изображение не загружено";
            FontMetrics fm2 = g2d.getFontMetrics();
            int msgWidth = fm2.stringWidth(message);

            g2d.setColor(new Color(120, 120, 120));
            g2d.drawString(message, (getWidth() - msgWidth) / 2, getHeight() / 2);

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            String hint = "Нажмите 'Загрузить' для выбора изображения";
            int hintWidth = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, (getWidth() - hintWidth) / 2, getHeight() / 2 + 30);
        }
    }
}

// Панель управления с настройками
class ControlPanel extends JPanel {
    private ModernImageLab parent;
    private JSlider contrastSlider;
    private JSlider brightnessSlider;
    private JComboBox<String> operationCombo;
    private JComboBox<String> shapeCombo;
    private JSlider kernelSizeSlider;

    public ControlPanel(ModernImageLab parent) {
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Панель контрастирования
        add(createStyledPanel("🎨 Линейное контрастирование", createContrastControls(),
                new Color(66, 134, 244)));

        add(Box.createRigidArea(new Dimension(0, 20)));

        // Панель морфологических операций
        add(createStyledPanel("🔧 Морфологические операции", createMorphologyControls(),
                new Color(52, 168, 83)));

        // Изначально отключаем элементы управления
        enableControls(false);
    }

    private JPanel createStyledPanel(String title, JComponent content, Color accentColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Фон панели
                g2d.setColor(new Color(50, 50, 55));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Верхняя полоска-акцент
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 5, 20, 20);
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setOpaque(false);

        // Заголовок
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createContrastControls() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Слайдер контраста
        JPanel contrastPanel = new JPanel(new BorderLayout(10, 0));
        contrastPanel.setOpaque(false);

        JLabel contrastLabel = new JLabel("Контраст (a):");
        contrastLabel.setForeground(new Color(200, 200, 200));
        contrastLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contrastPanel.add(contrastLabel, BorderLayout.WEST);

        contrastSlider = createStyledSlider(5, 30, 10);
        contrastSlider.setMajorTickSpacing(5);
        contrastPanel.add(contrastSlider, BorderLayout.CENTER);

        JLabel contrastValue = new JLabel("1.0");
        contrastValue.setForeground(Color.WHITE);
        contrastValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        contrastValue.setPreferredSize(new Dimension(40, 20));
        contrastPanel.add(contrastValue, BorderLayout.EAST);

        contrastSlider.addChangeListener(e -> {
            float value = contrastSlider.getValue() / 10.0f;
            contrastValue.setText(String.format("%.1f", value));
        });

        // Слайдер яркости
        JPanel brightnessPanel = new JPanel(new BorderLayout(10, 0));
        brightnessPanel.setOpaque(false);
        brightnessPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel brightnessLabel = new JLabel("Яркость (b):");
        brightnessLabel.setForeground(new Color(200, 200, 200));
        brightnessLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brightnessPanel.add(brightnessLabel, BorderLayout.WEST);

        brightnessSlider = createStyledSlider(-100, 100, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessPanel.add(brightnessSlider, BorderLayout.CENTER);

        JLabel brightnessValue = new JLabel("0");
        brightnessValue.setForeground(Color.WHITE);
        brightnessValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        brightnessValue.setPreferredSize(new Dimension(40, 20));
        brightnessPanel.add(brightnessValue, BorderLayout.EAST);

        brightnessSlider.addChangeListener(e -> {
            brightnessValue.setText(String.valueOf(brightnessSlider.getValue()));
        });

        // Кнопка применения
        JButton applyButton = new JButton("Применить контрастирование") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(66, 134, 244).darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(66, 134, 244).brighter());
                } else {
                    g2.setColor(new Color(66, 134, 244));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        applyButton.setContentAreaFilled(false);
        applyButton.setFocusPainted(false);
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyButton.addActionListener(e -> {
            float contrast = contrastSlider.getValue() / 10.0f;
            int brightness = brightnessSlider.getValue();
            parent.applyContrast(contrast, brightness);
        });

        panel.add(contrastPanel);
        panel.add(brightnessPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(applyButton);

        return panel;
    }

    private JPanel createMorphologyControls() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Выбор операции
        JPanel operationPanel = new JPanel(new BorderLayout(10, 0));
        operationPanel.setOpaque(false);

        JLabel opLabel = new JLabel("Операция:");
        opLabel.setForeground(new Color(200, 200, 200));
        opLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        operationPanel.add(opLabel, BorderLayout.WEST);

        operationCombo = createStyledComboBox(new String[]{
                "⚪ Эрозия", "🔴 Дилатация", "🟢 Открытие", "🔵 Закрытие"
        });
        operationPanel.add(operationCombo, BorderLayout.CENTER);

        // Выбор формы ядра
        JPanel shapePanel = new JPanel(new BorderLayout(10, 0));
        shapePanel.setOpaque(false);
        shapePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel shapeLabel = new JLabel("Форма ядра:");
        shapeLabel.setForeground(new Color(200, 200, 200));
        shapeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shapePanel.add(shapeLabel, BorderLayout.WEST);

        shapeCombo = createStyledComboBox(new String[]{
                "⬜ Квадрат", "⭕ Круг", "➕ Крест"
        });
        shapePanel.add(shapeCombo, BorderLayout.CENTER);

        // Размер ядра
        JPanel sizePanel = new JPanel(new BorderLayout(10, 0));
        sizePanel.setOpaque(false);
        sizePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel sizeLabel = new JLabel("Размер ядра:");
        sizeLabel.setForeground(new Color(200, 200, 200));
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sizePanel.add(sizeLabel, BorderLayout.WEST);

        kernelSizeSlider = createStyledSlider(3, 15, 3);
        kernelSizeSlider.setMajorTickSpacing(2);
        kernelSizeSlider.setSnapToTicks(true);
        sizePanel.add(kernelSizeSlider, BorderLayout.CENTER);

        JLabel sizeValue = new JLabel("3×3");
        sizeValue.setForeground(Color.WHITE);
        sizeValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sizeValue.setPreferredSize(new Dimension(40, 20));
        sizePanel.add(sizeValue, BorderLayout.EAST);

        kernelSizeSlider.addChangeListener(e -> {
            int size = kernelSizeSlider.getValue();
            sizeValue.setText(size + "×" + size);
        });

        // Кнопка применения
        JButton applyButton = new JButton("Применить морфологию") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(new Color(52, 168, 83).darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(52, 168, 83).brighter());
                } else {
                    g2.setColor(new Color(52, 168, 83));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };

        applyButton.setForeground(Color.WHITE);
        applyButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        applyButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        applyButton.setContentAreaFilled(false);
        applyButton.setFocusPainted(false);
        applyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        applyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        applyButton.addActionListener(e -> {
            String operation = ((String) operationCombo.getSelectedItem()).substring(2);
            String shape = ((String) shapeCombo.getSelectedItem()).substring(2);
            int size = kernelSizeSlider.getValue();
            parent.applyMorphology(operation, shape, size);
        });

        panel.add(operationPanel);
        panel.add(shapePanel);
        panel.add(sizePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(applyButton);

        return panel;
    }

    private JSlider createStyledSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };

        slider.setBackground(new Color(60, 63, 65));
        slider.setForeground(Color.WHITE);
        slider.setOpaque(false);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        // Стилизация трека
        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle trackBounds = trackRect;
                g2d.setColor(new Color(80, 80, 85));
                g2d.fillRoundRect(trackBounds.x, trackBounds.y + trackBounds.height/2 - 2,
                        trackBounds.width, 4, 2, 2);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle thumbBounds = thumbRect;
                g2d.setColor(new Color(66, 134, 244));
                g2d.fillOval(thumbBounds.x, thumbBounds.y,
                        thumbBounds.width, thumbBounds.height);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(thumbBounds.x, thumbBounds.y,
                        thumbBounds.width, thumbBounds.height);
            }
        });

        return slider;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);

        combo.setBackground(new Color(40, 40, 45));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(66, 134, 244) : new Color(40, 40, 45));
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        // Стилизация стрелки
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(66, 134, 244));
                        g2.fillRect(0, 0, getWidth(), getHeight());

                        // Рисуем стрелку
                        g2.setColor(Color.WHITE);
                        int[] xPoints = {getWidth()/2 - 5, getWidth()/2, getWidth()/2 + 5};
                        int[] yPoints = {getHeight()/2 - 2, getHeight()/2 + 3, getHeight()/2 - 2};
                        g2.fillPolygon(xPoints, yPoints, 3);
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });

        return combo;
    }

    public void enableControls(boolean enabled) {
        contrastSlider.setEnabled(enabled);
        brightnessSlider.setEnabled(enabled);
        operationCombo.setEnabled(enabled);
        shapeCombo.setEnabled(enabled);
        kernelSizeSlider.setEnabled(enabled);
    }

    public void resetControls() {
        contrastSlider.setValue(10);
        brightnessSlider.setValue(0);
        operationCombo.setSelectedIndex(0);
        shapeCombo.setSelectedIndex(0);
        kernelSizeSlider.setValue(3);
    }
}

// Панель для отображения гистограммы
class HistogramPanel extends JPanel {
    private int[] histogram;
    private int maxFrequency;

    public HistogramPanel() {
        histogram = new int[256];
        maxFrequency = 1;
        setPreferredSize(new Dimension(350, 250));
        setBackground(new Color(35, 35, 38));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 65), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
    }

    public void calculateHistogram(BufferedImage image) {
        if (image == null) return;

        histogram = new int[256];
        maxFrequency = 1;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                histogram[gray]++;
                maxFrequency = Math.max(maxFrequency, histogram[gray]);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Фон
        g2d.setColor(new Color(40, 40, 43));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Заголовок
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String title = "📊 Гистограмма яркости";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 20);

        // Рисуем оси
        int marginLeft = 40;
        int marginRight = 20;
        int marginTop = 40;
        int marginBottom = 30;

        int chartWidth = getWidth() - marginLeft - marginRight;
        int chartHeight = getHeight() - marginTop - marginBottom;

        // Оси
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartHeight); // Y ось
        g2d.drawLine(marginLeft, marginTop + chartHeight,
                marginLeft + chartWidth, marginTop + chartHeight); // X ось

        // Подписи осей
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.drawString("Частота", 5, marginTop + chartHeight / 2);
        g2d.drawString("Яркость", marginLeft + chartWidth / 2 - 20, getHeight() - 5);

        // Разметка оси X
        g2d.setColor(new Color(150, 150, 150));
        for (int i = 0; i <= 255; i += 64) {
            int x = marginLeft + (i * chartWidth) / 255;
            g2d.drawLine(x, marginTop + chartHeight, x, marginTop + chartHeight + 5);
            g2d.drawString(String.valueOf(i), x - 5, marginTop + chartHeight + 15);
        }

        // Разметка оси Y
        if (maxFrequency > 0) {
            int step = maxFrequency / 5;
            if (step > 0) {
                for (int i = 0; i <= 5; i++) {
                    int freq = i * step;
                    int y = marginTop + chartHeight - (freq * chartHeight) / maxFrequency;
                    g2d.drawLine(marginLeft - 5, y, marginLeft, y);
                    g2d.drawString(String.format("%d", freq), marginLeft - 35, y + 5);
                }
            }
        }

        // Рисуем гистограмму
        if (maxFrequency > 0) {
            int barWidth = Math.max(1, chartWidth / 256);

            for (int i = 0; i < 256; i++) {
                int barHeight = (int)((double)histogram[i] / maxFrequency * chartHeight);
                int x = marginLeft + (i * chartWidth) / 255;
                int y = marginTop + chartHeight - barHeight;

                // Градиент для столбцов
                GradientPaint gradient = new GradientPaint(x, y, new Color(66, 134, 244),
                        x, y + barHeight, new Color(155, 89, 182));
                g2d.setPaint(gradient);
                g2d.fillRect(x - barWidth/2, y, barWidth, barHeight);

                // Контур столбца
                g2d.setColor(new Color(30, 30, 30, 100));
                g2d.drawRect(x - barWidth/2, y, barWidth, barHeight);
            }
        } else {
            // Сообщение при отсутствии данных
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            g2d.setColor(new Color(150, 150, 150));
            String msg = "Нет данных для гистограммы";
            int msgWidth = g2d.getFontMetrics().stringWidth(msg);
            g2d.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
        }
    }
}