package ui;

import data.Card;
import data.ChipsDatabase;
import logic.Deck;
import logic.Hand;
import static ui.UIConstants.*;

import javax.swing.*;
import javax.sound.sampled.*;

import static java.lang.Thread.sleep;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class BlackjackGUI extends JFrame implements ActionListener {

    // Card layout to switch between menu and game
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel gamePanel; // Store reference to update background

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;

    // Database
    private final ChipsDatabase database;

    // Betting and stats
    private int chips = 100;
    private int currentBet = 0;
    private int winStreak = 0;

    // Background settings
    private String currentBackground = "default"; // default, green_table
    private boolean ownGreenTable = false;

    // Game settings
    private int difficulty = 1; // 1 = Easy, 2 = Medium, 3 = Hard
    private int luckLevel = 1; // 1 = Normal, 2 = Lucky, 3 = Very Lucky

    // GAME screen components
    private final JPanel dealerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JLabel dealerValueLabel = new JLabel("");

    private final JPanel playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JLabel playerValueLabel = new JLabel("");

    private final JLabel statusLabel = new JLabel("Place your bet!");
    private final JLabel chipsLabel = new JLabel("Chips: $100");
    private final JLabel winStreakLabel = new JLabel("Win Streak: 0");
    private final JLabel betLabel = new JLabel("Current Bet: $0");

    private final RedButton hitButton = new RedButton("Hit");
    private final RedButton standButton = new RedButton("Stand");
    private final RedButton doubleButton = new RedButton("Double Down");
    private final RedButton dealButton = new RedButton("Deal Cards");

    // Betting buttons
    private final RedButton bet5Button = new RedButton("Bet $5");
    private final RedButton bet10Button = new RedButton("Bet $10");
    private final RedButton bet25Button = new RedButton("Bet $25");
    private final RedButton bet50Button = new RedButton("Bet $50");
    private final RedButton clearBetButton = new RedButton("Clear Bet");

    private boolean roundOver = false;
    private boolean roundStarted = false;

    public BlackjackGUI() {
        super("Blackjack");

        // Initialize database and load saved data
        database = new ChipsDatabase();
        chips = database.getChips();
        ownGreenTable = database.getOwnGreenTable();
        currentBackground = database.getCurrentBackground();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Fullscreen dark window
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Allow ESC to exit
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitApp");
        getRootPane().getActionMap().put("exitApp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel();
        gamePanel = createGamePanel();

        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(gamePanel, "GAME");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "MENU");

        // Apply saved background
        updateGameBackground();

        setVisible(true);
    }

    // ---------- MENU UI ----------
    private JPanel createMenuPanel() {
        AnimatedBackgroundPanel panel = new AnimatedBackgroundPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("BLACKDUCK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_TITLE));
        titleLabel.setForeground(Color.WHITE);

        // Logo
        JLabel logoLabel = new JLabel("", SwingConstants.CENTER);
        try {
            // Try to load from classpath first
            java.net.URL logoUrl = getClass().getResource("/data/logo.png");
            if (logoUrl != null) {
                ImageIcon logoIcon = new ImageIcon(logoUrl);
                logoLabel.setIcon(logoIcon);
            } else {
                // Fallback to file path
                ImageIcon logoIcon = new ImageIcon("src/data/logo.png");
                if (logoIcon.getIconWidth() == -1) {
                    // If still not found, set placeholder text
                    logoLabel.setText("BLACKDUCK");
                    logoLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
                    logoLabel.setForeground(Color.WHITE);
                } else {
                    logoLabel.setIcon(logoIcon);
                }
            }
        } catch (Exception ex) {
            // If loading fails, show text instead
            logoLabel.setText("BLACKDUCK");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
        }

        // Buttons
        RedButton startButton = new RedButton("Start Game");
        RedButton settingsButton = new RedButton("Settings");
        RedButton quitButton = new RedButton("Quit");

        startButton.addActionListener(e -> {
            updateStatsDisplay(); // Update chips display with current value from database
            cardLayout.show(cardPanel, "GAME");
        });

        settingsButton.addActionListener(e -> {
            // Remove old settings panel if it exists
            try {
                cardPanel.remove(2); // Settings is at index 2
            } catch (Exception ex) {
                // Ignore if doesn't exist
            }
            // Create fresh settings panel with current chip values
            JPanel settingsPanel = createSettingsPanel();
            cardPanel.add(settingsPanel, "SETTINGS");
            cardLayout.show(cardPanel, "SETTINGS");
        });

        quitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(quitButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- GAME UI ----------
    private JPanel createGamePanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with stats and exit button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Stats panel (center)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsPanel.setOpaque(false);

        chipsLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        chipsLabel.setForeground(CHIPS_GOLD); // Gold color

        winStreakLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        winStreakLabel.setForeground(WIN_STREAK_GREEN); // Lime green

        betLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        betLabel.setForeground(BET_RED); // Light red

        statsPanel.add(chipsLabel);
        statsPanel.add(winStreakLabel);
        statsPanel.add(betLabel);

        // Menu button (top right)
        RedButton menuButton = new RedButton("Menu");
        menuButton.setPreferredSize(new Dimension(80, 35));
        menuButton.addActionListener(e -> {
            // Reset current round state only (keep chips and background)
            roundStarted = false;
            roundOver = false;
            currentBet = 0;
            winStreak = 0;

            // Clear cards
            playerCardsPanel.removeAll();
            dealerCardsPanel.removeAll();
            playerCardsPanel.revalidate();
            playerCardsPanel.repaint();
            dealerCardsPanel.revalidate();
            dealerCardsPanel.repaint();
            playerValueLabel.setText("");
            dealerValueLabel.setText("");

            // Reset buttons
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            doubleButton.setEnabled(false);
            dealButton.setEnabled(false);
            bet5Button.setEnabled(true);
            bet10Button.setEnabled(true);
            bet25Button.setEnabled(true);
            bet50Button.setEnabled(true);
            clearBetButton.setEnabled(true);

            // Reset labels
            updateStatsDisplay();
            statusLabel.setText("Place your bet!");

            // Go back to menu
            cardLayout.show(cardPanel, "MENU");
        });

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exitPanel.setOpaque(false);
        exitPanel.add(menuButton);

        topPanel.add(statsPanel, BorderLayout.CENTER);
        topPanel.add(exitPanel, BorderLayout.EAST);

        // Dealer panel
        JPanel dealerPanel = new JPanel(new BorderLayout());
        dealerPanel.setOpaque(false);
        JLabel dealerLabel = new JLabel("Dealer");
        dealerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        dealerLabel.setForeground(Color.WHITE);
        dealerCardsPanel.setOpaque(false);
        dealerValueLabel.setForeground(Color.LIGHT_GRAY);
        dealerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));

        dealerPanel.add(dealerLabel, BorderLayout.NORTH);
        dealerPanel.add(dealerCardsPanel, BorderLayout.CENTER);
        dealerPanel.add(dealerValueLabel, BorderLayout.SOUTH);

        // Player panel
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setOpaque(false);
        JLabel playerLabel = new JLabel("Player");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        playerLabel.setForeground(Color.WHITE);
        playerCardsPanel.setOpaque(false);
        playerValueLabel.setForeground(Color.LIGHT_GRAY);
        playerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));

        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        playerPanel.add(playerValueLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(dealerPanel);
        centerPanel.add(playerPanel);

        // Betting controls
        JPanel bettingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bettingPanel.setOpaque(false);
        bettingPanel.add(bet5Button);
        bettingPanel.add(bet10Button);
        bettingPanel.add(bet25Button);
        bettingPanel.add(bet50Button);
        bettingPanel.add(clearBetButton);

        // Game controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setOpaque(false);
        controlsPanel.add(dealButton);
        controlsPanel.add(hitButton);
        controlsPanel.add(doubleButton);
        controlsPanel.add(standButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(bettingPanel, BorderLayout.CENTER);
        bottomPanel.add(controlsPanel, BorderLayout.SOUTH);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        doubleButton.addActionListener(this);
        dealButton.addActionListener(this);
        bet5Button.addActionListener(this);
        bet10Button.addActionListener(this);
        bet25Button.addActionListener(this);
        bet50Button.addActionListener(this);
        clearBetButton.addActionListener(this);

        // Initial state on game screen
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        dealButton.setEnabled(false);

        return root;
    }

    // ---------- SETTINGS UI ----------
    private JPanel createSettingsPanel() {
        AnimatedBackgroundPanel panel = new AnimatedBackgroundPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("SETTINGS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_SUBTITLE));
        titleLabel.setForeground(Color.WHITE);

        // Chips display
        JLabel settingsChipsLabel = new JLabel("Your Chips: $" + chips, SwingConstants.CENTER);
        settingsChipsLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        settingsChipsLabel.setForeground(CHIPS_GOLD);

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(titleLabel);
        topPanel.add(settingsChipsLabel);

        // Main options panel with scrolling
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Game Settings Section
        JPanel gameSettingsPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        gameSettingsPanel.setOpaque(false);
        gameSettingsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Difficulty Setting
        JPanel difficultyPanel = new JPanel(new BorderLayout(10, 10));
        difficultyPanel.setOpaque(false);
        difficultyPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JLabel difficultyTitleLabel = new JLabel("Difficulty", SwingConstants.CENTER);
        difficultyTitleLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        difficultyTitleLabel.setForeground(Color.WHITE);
        difficultyTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel difficultyDescLabel = new JLabel("<html><center>Higher difficulty: Dealer plays smarter</center></html>", SwingConstants.CENTER);
        difficultyDescLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        difficultyDescLabel.setForeground(Color.LIGHT_GRAY);

        String[] difficultyLevels = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficultyLevels);
        difficultyCombo.setSelectedIndex(difficulty - 1);
        difficultyCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        difficultyCombo.addActionListener(e -> {
            difficulty = difficultyCombo.getSelectedIndex() + 1;
        });

        JPanel difficultyContentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        difficultyContentPanel.setOpaque(false);
        difficultyContentPanel.add(difficultyTitleLabel);
        difficultyContentPanel.add(difficultyDescLabel);

        JPanel difficultyComboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        difficultyComboPanel.setOpaque(false);
        difficultyComboPanel.add(difficultyCombo);

        difficultyPanel.add(difficultyContentPanel, BorderLayout.CENTER);
        difficultyPanel.add(difficultyComboPanel, BorderLayout.SOUTH);

        // Luck Setting
        JPanel luckPanel = new JPanel(new BorderLayout(10, 10));
        luckPanel.setOpaque(false);
        luckPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JLabel luckTitleLabel = new JLabel("Luck Level", SwingConstants.CENTER);
        luckTitleLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        luckTitleLabel.setForeground(Color.WHITE);
        luckTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel luckDescLabel = new JLabel("<html><center>Higher luck: Better odds for player</center></html>", SwingConstants.CENTER);
        luckDescLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        luckDescLabel.setForeground(Color.LIGHT_GRAY);

        String[] luckLevels = {"Normal", "Lucky", "Very Lucky"};
        JComboBox<String> luckCombo = new JComboBox<>(luckLevels);
        luckCombo.setSelectedIndex(luckLevel - 1);
        luckCombo.setFont(new Font("SansSerif", Font.BOLD, 16));
        luckCombo.addActionListener(e -> {
            luckLevel = luckCombo.getSelectedIndex() + 1;
        });

        JPanel luckContentPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        luckContentPanel.setOpaque(false);
        luckContentPanel.add(luckTitleLabel);
        luckContentPanel.add(luckDescLabel);

        JPanel luckComboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        luckComboPanel.setOpaque(false);
        luckComboPanel.add(luckCombo);

        luckPanel.add(luckContentPanel, BorderLayout.CENTER);
        luckPanel.add(luckComboPanel, BorderLayout.SOUTH);

        gameSettingsPanel.add(difficultyPanel);
        gameSettingsPanel.add(luckPanel);

        // Background options panel
        JPanel backgroundPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        backgroundPanel.setOpaque(false);

        // Green Table option
        JPanel greenTablePanel = new JPanel(new BorderLayout(10, 10));
        greenTablePanel.setOpaque(false);
        greenTablePanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        greenTablePanel.setBackground(new Color(30, 30, 30, 150));

        JLabel greenTableLabel = new JLabel("<html><center>Green Poker Table<br/>Cost: $150</center></html>", SwingConstants.CENTER);
        greenTableLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        greenTableLabel.setForeground(Color.WHITE);
        greenTableLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RedButton purchaseGreenButton = new RedButton("Purchase");
        RedButton equipGreenButton = new RedButton("Equip");
        RedButton unequipGreenButton = new RedButton("Unequip");

        JPanel greenButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        greenButtonPanel.setOpaque(false);

        // Initially show purchase button if not owned
        if (!ownGreenTable) {
            greenButtonPanel.add(purchaseGreenButton);
        } else {
            if (currentBackground.equals("green_table")) {
                greenButtonPanel.add(unequipGreenButton);
            } else {
                greenButtonPanel.add(equipGreenButton);
            }
        }

        purchaseGreenButton.addActionListener(e -> {
            if (chips >= 150 && !ownGreenTable) {
                chips -= 150;
                ownGreenTable = true;
                database.saveOwnGreenTable(true);
                settingsChipsLabel.setText("Your Chips: $" + chips);
                updateStatsDisplay(); // Update game UI immediately
                greenButtonPanel.removeAll();
                greenButtonPanel.add(equipGreenButton);
                greenButtonPanel.revalidate();
                greenButtonPanel.repaint();
                showSuccess("Green Poker Table purchased!");
            } else if (chips < 150) {
                showWarning("Not enough chips! You need $150.");
            }
        });

        equipGreenButton.addActionListener(e -> {
            currentBackground = "green_table";
            database.saveCurrentBackground("green_table");
            updateGameBackground();
            greenButtonPanel.removeAll();
            greenButtonPanel.add(unequipGreenButton);
            greenButtonPanel.revalidate();
            greenButtonPanel.repaint();
            showSuccess("Green Poker Table equipped!");
        });

        unequipGreenButton.addActionListener(e -> {
            currentBackground = "default";
            database.saveCurrentBackground("default");
            updateGameBackground();
            greenButtonPanel.removeAll();
            greenButtonPanel.add(equipGreenButton);
            greenButtonPanel.revalidate();
            greenButtonPanel.repaint();
            showSuccess("Background reset to default.");
        });

        greenTablePanel.add(greenTableLabel, BorderLayout.CENTER);
        greenTablePanel.add(greenButtonPanel, BorderLayout.SOUTH);

        // Default background option
        JPanel defaultPanel = new JPanel(new BorderLayout(10, 10));
        defaultPanel.setOpaque(false);
        defaultPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JLabel defaultLabel = new JLabel("<html><center>Default Dark Background<br/>Always Available</center></html>", SwingConstants.CENTER);
        defaultLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        defaultLabel.setForeground(Color.WHITE);
        defaultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RedButton equipDefaultButton = new RedButton("Equip");
        JPanel defaultButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        defaultButtonPanel.setOpaque(false);

        if (currentBackground.equals("default")) {
            JLabel equippedLabel = new JLabel("Currently Equipped");
            equippedLabel.setForeground(WIN_STREAK_GREEN);
            equippedLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            defaultButtonPanel.add(equippedLabel);
        } else {
            defaultButtonPanel.add(equipDefaultButton);
        }

        equipDefaultButton.addActionListener(e -> {
            currentBackground = "default";
            database.saveCurrentBackground("default");
            updateGameBackground();
            showSuccess("Default background equipped!");
            // Refresh settings panel
            cardLayout.show(cardPanel, "MENU");
            cardLayout.show(cardPanel, "SETTINGS");
        });

        defaultPanel.add(defaultLabel, BorderLayout.CENTER);
        defaultPanel.add(defaultButtonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(defaultPanel);
        backgroundPanel.add(greenTablePanel);

        // Add all sections to main options panel
        optionsPanel.add(gameSettingsPanel);
        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(backgroundPanel);

        // Purchase Chips Section
        JPanel purchaseChipsPanel = new JPanel(new BorderLayout(10, 10));
        purchaseChipsPanel.setOpaque(false);
        purchaseChipsPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        purchaseChipsPanel.setBackground(new Color(30, 30, 30, 150));

        JLabel purchaseLabel = new JLabel("<html><center>Purchase Virtual Chips<br/>Buy chips with test payment</center></html>", SwingConstants.CENTER);
        purchaseLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        purchaseLabel.setForeground(CHIPS_GOLD);
        purchaseLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RedButton buyChipsButton = new RedButton("Buy Chips");
        buyChipsButton.setPreferredSize(new Dimension(150, 40));

        buyChipsButton.addActionListener(e -> {
            PaymentDialog paymentDialog = new PaymentDialog(this);
            paymentDialog.setVisible(true);

            if (paymentDialog.wasSuccessful()) {
                chips += paymentDialog.getChipsToAdd();
                settingsChipsLabel.setText("Your Chips: $" + chips);
                updateStatsDisplay();
            }
        });

        JPanel purchaseButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        purchaseButtonPanel.setOpaque(false);
        purchaseButtonPanel.add(buyChipsButton);

        purchaseChipsPanel.add(purchaseLabel, BorderLayout.CENTER);
        purchaseChipsPanel.add(purchaseButtonPanel, BorderLayout.SOUTH);

        optionsPanel.add(Box.createVerticalStrut(20));
        optionsPanel.add(purchaseChipsPanel);

        // Back button
        RedButton backButton = new RedButton("Back to Menu");
        backButton.addActionListener(e -> {
            // Update game UI with current chip amount
            updateStatsDisplay();
            cardLayout.show(cardPanel, "MENU");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);

        // Wrap optionsPanel in a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateGameBackground() {
        if (currentBackground.equals("green_table")) {
            gamePanel.setBackground(POKER_TABLE_GREEN);
        } else {
            gamePanel.setBackground(BACKGROUND_MEDIUM_DARK);
        }
        gamePanel.repaint();
    }

    // Helper methods to reduce JOptionPane repetition
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    // ---------- GAME LOGIC / ACTIONS ----------

    private void placeBet(int amount) {
        if (roundStarted) return;

        if (chips >= amount) {
            currentBet += amount;
            chips -= amount;
            updateStatsDisplay();

            if (currentBet > 0) {
                dealButton.setEnabled(true);
                statusLabel.setText("Click 'Deal Cards' to start!");
            }
        } else {
            statusLabel.setText("Not enough chips!");
        }
    }

    private void clearBet() {
        if (roundStarted) return;

        chips += currentBet;
        currentBet = 0;
        dealButton.setEnabled(false);
        updateStatsDisplay();
        statusLabel.setText("Place your bet!");
    }

    private void updateStatsDisplay() {
        chipsLabel.setText("Chips: $" + chips);
        betLabel.setText("Current Bet: $" + currentBet);
        winStreakLabel.setText("Win Streak: " + winStreak);
        // Save chips to database whenever stats update
        database.saveChips(chips);
    }

    private void dealInitialCards() {
        if (currentBet == 0) {
            statusLabel.setText("Place a bet first!");
            return;
        }

        deck = new Deck();
        deck.shuffle();
        playerHand = new Hand();
        dealerHand = new Hand();
        roundOver = false;
        roundStarted = true;

        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        updateLabels(false);

        // Disable betting during round
        bet5Button.setEnabled(false);
        bet10Button.setEnabled(false);
        bet25Button.setEnabled(false);
        bet50Button.setEnabled(false);
        clearBetButton.setEnabled(false);
        dealButton.setEnabled(false);

        // Enable playing
        hitButton.setEnabled(true);
        standButton.setEnabled(true);

        // Enable double down only if player has enough chips and exactly 2 cards
        if (chips >= currentBet && playerHand.getCards().size() == 2) {
            doubleButton.setEnabled(true);
            statusLabel.setText("Hit, Double Down, or Stand?");
        } else {
            doubleButton.setEnabled(false);
            statusLabel.setText("Your move: Hit or Stand?");
        }
    }

    private void updateLabels(boolean showDealerFull) {
        // PLAYER cards
        playerCardsPanel.removeAll();
        for (Card c : playerHand.getCards()) {
            JLabel lbl = new JLabel(CardImages.getIcon(c));
            lbl.setToolTipText(c.toString());
            playerCardsPanel.add(lbl);
        }
        playerValueLabel.setText("Value: " + playerHand.getValue());
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();

        // DEALER cards
        dealerCardsPanel.removeAll();
        List<Card> dCards = dealerHand.getCards();
        if (showDealerFull) {
            for (Card c : dCards) {
                JLabel lbl = new JLabel(CardImages.getIcon(c));
                lbl.setToolTipText(c.toString());
                dealerCardsPanel.add(lbl);
            }
            dealerValueLabel.setText("Value: " + dealerHand.getValue());
        } else {
            if (!dCards.isEmpty()) {
                JLabel first = new JLabel(CardImages.getIcon(dCards.get(0)));
                first.setToolTipText(dCards.get(0).toString());
                dealerCardsPanel.add(first);
                if (dCards.size() > 1) {
                    // hidden card(s)
                    JLabel hidden = new JLabel(CardImages.getBackIcon());
                    dealerCardsPanel.add(hidden);
                }
            }
            dealerValueLabel.setText("Value: ?");
        }
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
    }

    private void playerHit() {
        if (roundOver || !roundStarted) return;

        Card drawn = deck.drawCard();
        playerHand.addCard(drawn);
        updateLabels(false);

        // After first hit, can't double down anymore
        doubleButton.setEnabled(false);

        if (playerHand.isBust()) {
            roundOver = true;
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            updateLabels(true);
            int lostAmount = currentBet;
            endRound(false);
            statusLabel.setText("You busted! Dealer wins. Lost $" + lostAmount);
        } else if (playerHand.getValue() == 21) {
            playerStand();
        }
    }

    private void playerDouble() {
        if (roundOver || !roundStarted) return;
        if (chips < currentBet) {
            statusLabel.setText("Not enough chips to double!");
            return;
        }

        // Double the bet
        chips -= currentBet;
        currentBet *= 2;
        updateStatsDisplay();

        // Draw exactly one card
        Card drawn = deck.drawCard();
        playerHand.addCard(drawn);
        updateLabels(false);

        // Disable all actions
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);

        if (playerHand.isBust()) {
            roundOver = true;
            updateLabels(true);
            int lostAmount = currentBet;
            endRound(false);
            statusLabel.setText("You busted! Dealer wins. Lost $" + lostAmount);
        } else {
            // Automatically stand after doubling
            dealerTurn();
            roundOver = true;
        }
    }

    private void playerStand() {
        if (roundOver || !roundStarted) return;

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);

        dealerTurn();
        roundOver = true;
    }

    /**
     * 
     */
    private void dealerTurn() {
        updateLabels(true);

        if (playerHand.isBlackjack() || dealerHand.isBlackjack()) {
            if (playerHand.isBlackjack() && dealerHand.isBlackjack()) {
                int pushAmount = currentBet;
                endRound(null);  // Push
                statusLabel.setText("Both have Blackjack! It's a push. Returned $" + pushAmount);
            } else if (playerHand.isBlackjack()) {
                double luckMultiplier = 1.0;
                if (luckLevel == 2) luckMultiplier = 1.1;
                else if (luckLevel == 3) luckMultiplier = 1.25;
                int winAmount = (int)(currentBet * 2.5 * luckMultiplier); // Blackjack pays 3:2 + luck bonus
                endRound(true);
                statusLabel.setText("Blackjack! You win $" + winAmount + "!");
            } else {
                int lostAmount = currentBet;
                endRound(false);
                statusLabel.setText("Dealer has Blackjack! You lose. Lost $" + lostAmount);
            }
            return;
        }

        // Dealer hit threshold based on difficulty
        int dealerThreshold = 17;
        switch (difficulty) {
            case 1 -> dealerThreshold = 16; // Easy: Dealer hits on 16 or less
            case 2 -> dealerThreshold = 17; // Medium: Standard blackjack rules
            case 3 -> dealerThreshold = 17; // Hard: Dealer is smarter with soft hands
            default -> {
            }
        }

        while (dealerHand.getValue() < dealerThreshold) {
            Card drawn = deck.drawCard();
            dealerHand.addCard(drawn);
            updateLabels(true);
            try {
                sleep(250);
            } catch (InterruptedException ignored) {}
        }

        // On hard difficulty, dealer might hit on soft 17
        if (difficulty == 3 && dealerHand.getValue() == 17) {
            // Check if dealer has an Ace counting as 11 (soft 17)
            boolean hasSoftAce = false;
            int valueWithoutAce = 0;
            for (Card c : dealerHand.getCards()) {
                if (c.getRank().getValue() == 1) { // Ace
                    valueWithoutAce = dealerHand.getValue() - 11;
                    if (valueWithoutAce + 1 <= 10) {
                        hasSoftAce = true;
                        break;
                    }
                }
            }
            if (hasSoftAce && dealerHand.getValue() == 17) {
                Card drawn = deck.drawCard();
                dealerHand.addCard(drawn);
                updateLabels(true);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ignored) {}
            }
        }

        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();

        updateLabels(true);

        if (dealerHand.isBust()) {
            double luckMultiplier = 1.0;
            if (luckLevel == 2) luckMultiplier = 1.1;
            else if (luckLevel == 3) luckMultiplier = 1.25;
            int winAmount = (int)(currentBet * 2 * luckMultiplier);
            endRound(true);
            statusLabel.setText("Dealer busted! You win $" + winAmount + "!");
        } else if (playerValue > dealerValue) {
            double luckMultiplier = 1.0;
            if (luckLevel == 2) luckMultiplier = 1.1;
            else if (luckLevel == 3) luckMultiplier = 1.25;
            int winAmount = (int)(currentBet * 2 * luckMultiplier);
            endRound(true);
            statusLabel.setText("You win! (" + playerValue + " vs " + dealerValue + ") Won $" + winAmount);
        } else if (playerValue < dealerValue) {
            int lostAmount = currentBet;
            endRound(false);
            statusLabel.setText("Dealer wins. (" + playerValue + " vs " + dealerValue + ") Lost $" + lostAmount);
        } else {
            int pushAmount = currentBet;
            endRound(null);  // Push
            statusLabel.setText("It's a push (tie). (" + playerValue + " vs " + dealerValue + ") Returned $" + pushAmount);
        }
    }

    private void endRound(Boolean playerWon) {
        roundStarted = false;

        if (playerWon == null) {
            // Push - return bet
            chips += currentBet;
            // Luck bonus: On Lucky or Very Lucky, sometimes win on a push
            if (luckLevel >= 2) {
                double pushWinChance = (luckLevel == 2) ? 0.15 : 0.25; // 15% for Lucky, 25% for Very Lucky
                if (Math.random() < pushWinChance) {
                    chips += currentBet; // Get an extra bet as bonus
                    statusLabel.setText(statusLabel.getText() + " - Lucky bonus!");
                }
            }
        } else if (playerWon) {
            // Player wins
            double luckMultiplier = 1.0;
            if (luckLevel == 2) {
                luckMultiplier = 1.1; // 10% bonus on Lucky
            } else if (luckLevel == 3) {
                luckMultiplier = 1.25; // 25% bonus on Very Lucky
            }

            if (playerHand.isBlackjack()) {
                chips += (int)(currentBet * 2.5 * luckMultiplier); // Blackjack pays 3:2 + luck bonus
            } else {
                chips += (int)(currentBet * 2 * luckMultiplier);
            }
            winStreak++;
        } else {
            // Player loses
            // Luck can sometimes save you from a loss
            if (luckLevel == 3 && Math.random() < 0.1) { // 10% chance on Very Lucky
                chips += currentBet; // Get bet back
                statusLabel.setText(statusLabel.getText() + " - Very Lucky! Bet returned!");
            }
            winStreak = 0;
        }

        currentBet = 0;

        // Reset for next round
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        dealButton.setEnabled(false);

        // Clear cards
        playerCardsPanel.removeAll();
        dealerCardsPanel.removeAll();
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
        playerValueLabel.setText("");
        dealerValueLabel.setText("");

        updateStatsDisplay();

        // Check if player is out of chips
        if (chips == 0) {
            statusLabel.setText("Out of chips! Game Over.");
            bet5Button.setEnabled(false);
            bet10Button.setEnabled(false);
            bet25Button.setEnabled(false);
            bet50Button.setEnabled(false);
            clearBetButton.setEnabled(false);
        } else {
            // Enable betting for next round
            bet5Button.setEnabled(true);
            bet10Button.setEnabled(true);
            bet25Button.setEnabled(true);
            bet50Button.setEnabled(true);
            clearBetButton.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == hitButton) {
            playerHit();
        } else if (src == standButton) {
            playerStand();
        } else if (src == doubleButton) {
            playerDouble();
        } else if (src == dealButton) {
            dealInitialCards();
        } else if (src == bet5Button) {
            placeBet(5);
        } else if (src == bet10Button) {
            placeBet(10);
        } else if (src == bet25Button) {
            placeBet(25);
        } else if (src == bet50Button) {
            placeBet(50);
        } else if (src == clearBetButton) {
            clearBet();
        }
    }

    // ---------- Sound helper ----------
    public static void playClickSound() {
        new Thread(() -> {
            try {
                File f = new File("click.wav");
                if (!f.exists()) return;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BlackjackGUI::new);
    }
}