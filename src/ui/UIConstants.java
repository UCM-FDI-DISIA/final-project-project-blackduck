package ui;

import java.awt.*;

/**
 * Central location for all UI constants including colors, dimensions, and common values.
 * This eliminates repetition of color and size definitions across UI classes.
 */
public class UIConstants {

    // Color palette
    public static final Color BACKGROUND_DARK = new Color(10, 10, 10);
    public static final Color BACKGROUND_MEDIUM_DARK = new Color(20, 20, 20);
    public static final Color BACKGROUND_PANEL = new Color(30, 30, 30);
    public static final Color BACKGROUND_PANEL_LIGHT = new Color(40, 40, 40);

    public static final Color BORDER_GRAY = new Color(100, 100, 100);

    public static final Color BUTTON_RED = new Color(200, 0, 0);
    public static final Color BUTTON_RED_HOVER = new Color(230, 0, 0);

    public static final Color CHIPS_GOLD = new Color(255, 215, 0);
    public static final Color WIN_STREAK_GREEN = new Color(50, 205, 50);
    public static final Color BET_RED = new Color(255, 100, 100);

    public static final Color POKER_TABLE_GREEN = new Color(0, 100, 0);

    public static final Color ANIMATED_RED = new Color(120, 0, 0, 120);
    public static final Color ANIMATED_GREEN = new Color(0, 120, 0, 120);
    public static final Color ANIMATED_BLUE = new Color(0, 0, 120, 120);

    public static final Color CARD_BACK_BLUE = new Color(0, 51, 153);
    public static final Color CARD_BACK_RED = new Color(204, 0, 0);

    // Dimensions
    public static final int CARD_WIDTH = 100;
    public static final int CARD_HEIGHT = 145;
    public static final int CARD_CORNER_RADIUS = 16;

    public static final int BUTTON_CORNER_RADIUS = 30;

    // Border thickness
    public static final int BORDER_THICKNESS = 2;

    // Font sizes
    public static final int FONT_SIZE_TITLE = 42;
    public static final int FONT_SIZE_SUBTITLE = 38;
    public static final int FONT_SIZE_LARGE = 24;
    public static final int FONT_SIZE_MEDIUM = 20;
    public static final int FONT_SIZE_NORMAL = 18;
    public static final int FONT_SIZE_BUTTON = 20;
    public static final int FONT_SIZE_SMALL = 14;

    // Spacing
    public static final int SPACING_SMALL = 10;
    public static final int SPACING_MEDIUM = 20;
    public static final int SPACING_LARGE = 40;

    private UIConstants() {
        // Prevent instantiation
    }
}
