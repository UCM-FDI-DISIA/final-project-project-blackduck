package ui;

import data.Card;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CardImages {

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static ImageIcon backIcon;

    // Base paths to try for card images
    private static final String[] BASE_PATHS = {
        "src/data/PNG-cards-1.3/",
        "data/PNG-cards-1.3/",
        "../src/data/PNG-cards-1.3/",
        "./src/data/PNG-cards-1.3/"
    };

    public static ImageIcon getIcon(Card card) {
        String code = getCode(card);
        return CACHE.computeIfAbsent(code, key -> loadCardIcon(key));
    }

    public static ImageIcon getBackIcon() {
        if (backIcon == null) {
            backIcon = createCardBack();
        }
        return backIcon;
    }

    private static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private static ImageIcon createCardBack() {
        int w = UIConstants.CARD_WIDTH;
        int h = UIConstants.CARD_HEIGHT;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = GraphicsUtil.createAntialiasedGraphics(img.createGraphics());

        // Draw card with blue background
        GraphicsUtil.drawCard(g2, w, h, UIConstants.CARD_CORNER_RADIUS, UIConstants.CARD_BACK_BLUE);

        // Add a pattern
        g2.setColor(UIConstants.CARD_BACK_RED);
        for (int i = 10; i < w - 10; i += 15) {
            for (int j = 10; j < h - 10; j += 15) {
                g2.fillOval(i, j, 8, 8);
            }
        }

        g2.dispose();

        return new ImageIcon(img);
    }

    private static File findCardFile(String filename) {
        for (String basePath : BASE_PATHS) {
            File f = new File(basePath + filename);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }

    private static String getCode(Card card) {
        return card.getRank().getDisplayName() + "_of_" + card.getSuit().getDisplayName();
    }

    private static ImageIcon loadCardIcon(String code) {
        String filename = code + ".png";
        File f = findCardFile(filename);

        if (f != null && f.exists()) {
            ImageIcon icon = new ImageIcon(f.getPath());
            return scaleIcon(icon, UIConstants.CARD_WIDTH, UIConstants.CARD_HEIGHT);
        } else {
            // Debug: print when file not found and working directory
            System.err.println("Card image not found: " + filename);
            System.err.println("Working directory: " + System.getProperty("user.dir"));
        }
        Color tint = Color.LIGHT_GRAY;
        return createPlaceholder(code, tint);
    }

    private static ImageIcon createPlaceholder(String text, Color tint) {
        int w = UIConstants.CARD_WIDTH;
        int h = UIConstants.CARD_HEIGHT;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = GraphicsUtil.createAntialiasedGraphics(img.createGraphics());

        // Draw card with tint color
        GraphicsUtil.drawCard(g2, w, h, UIConstants.CARD_CORNER_RADIUS, tint);

        // Draw text
        g2.setFont(new Font("SansSerif", Font.BOLD, UIConstants.FONT_SIZE_NORMAL));
        g2.setColor(Color.WHITE);
        int strW = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (w - strW) / 2, h / 2);

        g2.dispose();
        return new ImageIcon(img);
    }
}