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
        int w = 100;
        int h = 145;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // White border
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);

        // Red/blue pattern background
        g2.setColor(new Color(0, 51, 153)); // Dark blue
        g2.fillRoundRect(4, 4, w - 9, h - 9, 14, 14);

        // Add a pattern
        g2.setColor(new Color(204, 0, 0)); // Red
        for (int i = 10; i < w - 10; i += 15) {
            for (int j = 10; j < h - 10; j += 15) {
                g2.fillOval(i, j, 8, 8);
            }
        }

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

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
        String r;
        switch (card.getRank()) {
            case ACE: r = "ace"; break;
            case TWO: r = "2"; break;
            case THREE: r = "3"; break;
            case FOUR: r = "4"; break;
            case FIVE: r = "5"; break;
            case SIX: r = "6"; break;
            case SEVEN: r = "7"; break;
            case EIGHT: r = "8"; break;
            case NINE: r = "9"; break;
            case TEN: r = "10"; break;
            case JACK: r = "jack"; break;
            case QUEEN: r = "queen"; break;
            case KING: r = "king"; break;
            default: r = "?";
        }

        String s;
        switch (card.getSuit()) {
            case HEARTS: s = "hearts"; break;
            case DIAMONDS: s = "diamonds"; break;
            case CLUBS: s = "clubs"; break;
            case SPADES: s = "spades"; break;
            default: s = "?";
        }
        return r + "_of_" + s;
    }

    private static ImageIcon loadCardIcon(String code) {
        String filename = code + ".png";
        File f = findCardFile(filename);

        if (f != null && f.exists()) {
            ImageIcon icon = new ImageIcon(f.getPath());
            // Scale the image to a reasonable size (100x145 pixels)
            return scaleIcon(icon, 100, 145);
        } else {
            // Debug: print when file not found and working directory
            System.err.println("Card image not found: " + filename);
            System.err.println("Working directory: " + System.getProperty("user.dir"));
        }
        Color tint = Color.LIGHT_GRAY;
        return createPlaceholder(code, tint);
    }

    private static ImageIcon createPlaceholder(String text, Color tint) {
        int w = 100;
        int h = 145;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);

        g2.setColor(tint);
        g2.fillRoundRect(4, 4, w - 9, h - 9, 14, 14);

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        int strW = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (w - strW) / 2, h / 2);

        g2.dispose();
        return new ImageIcon(img);
    }
}