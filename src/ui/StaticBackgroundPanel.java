package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Panel with a static background image.
 * Used for menu panel with customizable background images.
 */
public class StaticBackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;

    public StaticBackgroundPanel(String imagePath) {
        setBackground(UIConstants.BACKGROUND_DARK);
        loadBackgroundImage(imagePath);
    }

    private void loadBackgroundImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            } else {
                System.err.println("Background image not found: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Scale image to fill the panel while maintaining aspect ratio
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Draw image scaled to cover entire panel
            g2.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);

            // Add dark overlay for better text readability
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, panelWidth, panelHeight);
        } else {
            // Fallback to solid color if image not loaded
            g.setColor(UIConstants.BACKGROUND_DARK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
