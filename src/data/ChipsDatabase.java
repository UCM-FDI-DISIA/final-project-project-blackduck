package data;

import java.io.*;
import java.util.Properties;

/**
 * Simple database to persist player's chips (fish) amount between game sessions.
 * Uses a properties file for lightweight storage.
 */
public class ChipsDatabase {
    private static final String DB_FILE = "gamedata.properties";
    private static final String CHIPS_KEY = "chips";
    private static final String OWN_GREEN_TABLE_KEY = "ownGreenTable";
    private static final String OWN_ANIMATED_BG_KEY = "ownAnimatedBackground";
    private static final String CURRENT_BACKGROUND_KEY = "currentBackground";
    private static final String VOLUME_LEVEL_KEY = "volumeLevel";
    private static final String MUSIC_ENABLED_KEY = "musicEnabled";
    private static final int DEFAULT_CHIPS = 100;
    private static final int DEFAULT_VOLUME = 2; // 0=Off, 1=Low, 2=Medium, 3=High

    private Properties properties;

    public ChipsDatabase() {
        properties = new Properties();
        loadDatabase();
    }

    /**
     * Load database from file. Creates new file if it doesn't exist.
     */
    private void loadDatabase() {
        File file = new File(DB_FILE);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Error loading database: " + e.getMessage());
                initializeDefaults();
            }
        } else {
            initializeDefaults();
            saveDatabase();
        }
    }

    /**
     * Initialize default values
     */
    private void initializeDefaults() {
        properties.setProperty(CHIPS_KEY, String.valueOf(DEFAULT_CHIPS));
        properties.setProperty(OWN_GREEN_TABLE_KEY, "false");
        properties.setProperty(OWN_ANIMATED_BG_KEY, "false");
        properties.setProperty(CURRENT_BACKGROUND_KEY, "default");
        properties.setProperty(VOLUME_LEVEL_KEY, String.valueOf(DEFAULT_VOLUME));
        properties.setProperty(MUSIC_ENABLED_KEY, "true");
    }

    /**
     * Save database to file
     */
    private void saveDatabase() {
        try (FileOutputStream fos = new FileOutputStream(DB_FILE)) {
            properties.store(fos, "Blackjack Game Data");
        } catch (IOException e) {
            System.err.println("Error saving database: " + e.getMessage());
        }
    }

    /**
     * Get the current chips amount
     */
    public int getChips() {
        String chipsStr = properties.getProperty(CHIPS_KEY, String.valueOf(DEFAULT_CHIPS));
        try {
            return Integer.parseInt(chipsStr);
        } catch (NumberFormatException e) {
            return DEFAULT_CHIPS;
        }
    }

    /**
     * Save the chips amount
     */
    public void saveChips(int chips) {
        properties.setProperty(CHIPS_KEY, String.valueOf(chips));
        saveDatabase();
    }

    /**
     * Get whether player owns green table
     */
    public boolean getOwnGreenTable() {
        String value = properties.getProperty(OWN_GREEN_TABLE_KEY, "false");
        return Boolean.parseBoolean(value);
    }

    /**
     * Save whether player owns green table
     */
    public void saveOwnGreenTable(boolean owns) {
        properties.setProperty(OWN_GREEN_TABLE_KEY, String.valueOf(owns));
        saveDatabase();
    }

    /**
     * Get whether player owns animated background
     */
    public boolean getOwnAnimatedBackground() {
        String value = properties.getProperty(OWN_ANIMATED_BG_KEY, "false");
        return Boolean.parseBoolean(value);
    }

    /**
     * Save whether player owns animated background
     */
    public void saveOwnAnimatedBackground(boolean owns) {
        properties.setProperty(OWN_ANIMATED_BG_KEY, String.valueOf(owns));
        saveDatabase();
    }

    /**
     * Get current background
     */
    public String getCurrentBackground() {
        return properties.getProperty(CURRENT_BACKGROUND_KEY, "default");
    }

    /**
     * Save current background
     */
    public void saveCurrentBackground(String background) {
        properties.setProperty(CURRENT_BACKGROUND_KEY, background);
        saveDatabase();
    }

    /**
     * Get volume level (0=Off, 1=Low, 2=Medium, 3=High)
     */
    public int getVolumeLevel() {
        String value = properties.getProperty(VOLUME_LEVEL_KEY, String.valueOf(DEFAULT_VOLUME));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return DEFAULT_VOLUME;
        }
    }

    /**
     * Save volume level
     */
    public void saveVolumeLevel(int level) {
        properties.setProperty(VOLUME_LEVEL_KEY, String.valueOf(level));
        saveDatabase();
    }

    /**
     * Get whether background music is enabled
     */
    public boolean isMusicEnabled() {
        String value = properties.getProperty(MUSIC_ENABLED_KEY, "true");
        return Boolean.parseBoolean(value);
    }

    /**
     * Save whether background music is enabled
     */
    public void saveMusicEnabled(boolean enabled) {
        properties.setProperty(MUSIC_ENABLED_KEY, String.valueOf(enabled));
        saveDatabase();
    }

    /**
     * Reset all data to defaults
     */
    public void resetToDefaults() {
        initializeDefaults();
        saveDatabase();
    }
}
