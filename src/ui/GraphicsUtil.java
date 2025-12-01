package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for common graphics operations.
 * Eliminates repetition of Graphics2D setup and rendering code.
 */
public class GraphicsUtil {

    /**
     * Creates a Graphics2D object with antialiasing enabled.
     */
    public static Graphics2D createAntialiasedGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        return g2;
    }

    /**
     * Draws a card border (white outline with rounded corners).
     */
    public static void drawCardBorder(Graphics2D g2, int width, int height, int cornerRadius) {
        // White outer border
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Black outline
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
    }

    /**
     * Draws a card with border and inner fill color.
     */
    public static void drawCard(Graphics2D g2, int width, int height, int cornerRadius, Color fillColor) {
        // White outer border
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Inner fill
        g2.setColor(fillColor);
        g2.fillRoundRect(4, 4, width - 9, height - 9, cornerRadius - 2, cornerRadius - 2);

        // Black outline
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
    }

    /**
     * Creates a transparent panel with centered flow layout.
     */
    public static JPanel createTransparentPanel(int hgap, int vgap) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Creates a bordered panel with gray border.
     */
    public static void applyStandardBorder(JComponent component) {
        component.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_GRAY,
                                                          UIConstants.BORDER_THICKNESS));
    }

    private GraphicsUtil() {
        // Prevent instantiation
    }
}
