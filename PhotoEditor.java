
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class PhotoEditor extends JFrame {
    private JLabel imageLabel;
    private BufferedImage image;
    private BufferedImage drawingLayer;
    private Color penColor = Color.RED;
    private int penSize = 5;

    public PhotoEditor() {
        setTitle("VIZ IT UP!-Photo Editor(Your vizion, but cooler!)");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(new Color(153, 169, 143));
        ImageIcon image = new ImageIcon("logooo.jpg");
        setIconImage(image.getImage());
        setLayout(new BorderLayout());

        imageLabel = new JLabel();
        DrawingPanel drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(new Color(153, 169, 143));
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);

        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(saveItem);

        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem cropItem = new JMenuItem("Crop");
        cropItem.addActionListener(e -> cropImage());
        editMenu.add(cropItem);

        JMenuItem brightnessItem = new JMenuItem("Adjust Brightness");
        brightnessItem.addActionListener(e -> adjustBrightness());
        editMenu.add(brightnessItem);

        JMenu filterMenu = new JMenu("Filters");
        JMenuItem grayscaleItem = new JMenuItem("Grayscale");
        grayscaleItem.addActionListener(e -> applyGrayscaleFilter());
        filterMenu.add(grayscaleItem);

        JMenuItem blurItem = new JMenuItem("Blur");
        blurItem.addActionListener(e -> applyBlurFilter());
        filterMenu.add(blurItem);

        JMenuItem sharpenItem = new JMenuItem("Sharpen");
        sharpenItem.addActionListener(e -> applySharpenFilter());
        filterMenu.add(sharpenItem);

        editMenu.add(filterMenu);
        menuBar.add(editMenu);

        JMenu drawMenu = new JMenu("Draw");
        JMenuItem penColorItem = new JMenuItem("Pen Color");
        penColorItem.addActionListener(e -> choosePenColor());
        drawMenu.add(penColorItem);

        JMenuItem penSizeItem = new JMenuItem("Pen Size");
        penSizeItem.addActionListener(e -> choosePenSize());
        drawMenu.add(penSizeItem);

        menuBar.add(drawMenu);
        setJMenuBar(menuBar);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                drawingLayer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = drawingLayer.createGraphics();
                g.drawImage(image, 0, 0, null);
                g.dispose();
                imageLabel.setIcon(new ImageIcon(drawingLayer));
                repaint();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (drawingLayer != null) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(drawingLayer, "png", fileToSave);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void cropImage() {
        if (drawingLayer != null) {
            int width = drawingLayer.getWidth() / 2;
            int height = drawingLayer.getHeight() / 2;
            drawingLayer = drawingLayer.getSubimage(width / 2, height / 2, width, height);
            imageLabel.setIcon(new ImageIcon(drawingLayer));
        }
    }

    private void adjustBrightness() {
        if (drawingLayer != null) {
            float scaleFactor = 1.2f;
            RescaleOp op = new RescaleOp(scaleFactor, 0, null);
            drawingLayer = op.filter(drawingLayer, null);
            imageLabel.setIcon(new ImageIcon(drawingLayer));
        }
    }

    private void applyGrayscaleFilter() {
        if (drawingLayer != null) {
            BufferedImage grayscaleImage = new BufferedImage(drawingLayer.getWidth(), drawingLayer.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayscaleImage.getGraphics();
            g.drawImage(drawingLayer, 0, 0, null);
            g.dispose();
            drawingLayer = grayscaleImage;
            imageLabel.setIcon(new ImageIcon(drawingLayer));
        }
    }

    private void applyBlurFilter() {
        if (drawingLayer != null) {
            float[] matrix = {
                    1 / 9f, 1 / 9f, 1 / 9f,
                    1 / 9f, 1 / 9f, 1 / 9f,
                    1 / 9f, 1 / 9f, 1 / 9f
            };
            BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
            drawingLayer = op.filter(drawingLayer, null);
            imageLabel.setIcon(new ImageIcon(drawingLayer));
        }
    }

    private void applySharpenFilter() {
        if (drawingLayer != null) {
            float[] matrix = {
                    0, -1, 0,
                    -1, 5, -1,
                    0, -1, 0
            };
            BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
            drawingLayer = op.filter(drawingLayer, null);
            imageLabel.setIcon(new ImageIcon(drawingLayer));
        }
    }

    private void choosePenColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Pen Color", penColor);
        if (newColor != null) {
            penColor = newColor;
        }
    }

    private void choosePenSize() {
        String input = JOptionPane.showInputDialog(this, "Enter Pen Size:", penSize);
        if (input != null) {
            try {
                int newSize = Integer.parseInt(input);
                if (newSize > 0) {
                    penSize = newSize;
                } else {
                    JOptionPane.showMessageDialog(this, "Pen size must be positive.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DrawingPanel extends JPanel {
        private Point startPoint;

        public DrawingPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    startPoint = e.getPoint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    startPoint = null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (startPoint != null && drawingLayer != null) {
                        Graphics2D g2d = drawingLayer.createGraphics();
                        g2d.setColor(penColor);
                        g2d.setStroke(new BasicStroke(penSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(startPoint.x, startPoint.y, e.getX(), e.getY());
                        g2d.dispose();
                        startPoint = e.getPoint();
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (drawingLayer != null) {
                g.drawImage(drawingLayer, 0, 0, this);
            }
        }
    }

    public static void main(String[] args) {
        PhotoEditor editor = new PhotoEditor();
        editor.setVisible(true);
    }
}





