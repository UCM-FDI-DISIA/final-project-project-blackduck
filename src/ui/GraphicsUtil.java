package ui;

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

    private GraphicsUtil() {
        // Prevent instantiation
    }
}
