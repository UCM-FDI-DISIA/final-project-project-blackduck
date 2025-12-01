package ui;

import data.Card;
import network.GameServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static ui.UIConstants.*;

/**
 * GUI for the server (dealer) in multiplayer mode
 */
public class MultiplayerServerGUI extends JFrame implements GameServer.ServerListener {

    private GameServer server;
    private JLabel statusLabel;
    private JLabel serverInfoLabel;
    private JTextArea logArea;
    private JPanel dealerCardsPanel;
    private JPanel playerCardsPanel;
    private JLabel dealerValueLabel;
    private JLabel playerValueLabel;
    private JLabel betLabel;

    private RedButton stopServerButton;
    private RedButton startGameButton;
    private RedButton dealerHitButton;
    private RedButton dealerStandButton;

    private boolean isPlayerConnected = false;
    private boolean isDealerTurn = false;

    public MultiplayerServerGUI() {
        super("Blackjack - Dealer Server");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Allow ESC to exit
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitApp");
        getRootPane().getActionMap().put("exitApp", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                stopServer();
                System.exit(0);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(20, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("DEALER SERVER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_SUBTITLE));
        titleLabel.setForeground(Color.WHITE);

        serverInfoLabel = new JLabel("Starting server...", SwingConstants.CENTER);
        serverInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));
        serverInfoLabel.setForeground(CHIPS_GOLD);

        stopServerButton = new RedButton("Stop Server & Exit");
        stopServerButton.addActionListener(e -> {
            stopServer();
            System.exit(0);
        });

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(serverInfoLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(stopServerButton);

        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Center panel with game view
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setOpaque(false);

        // Dealer panel
        JPanel dealerPanel = new JPanel(new BorderLayout());
        dealerPanel.setOpaque(false);
        dealerPanel.setBorder(BorderFactory.createLineBorder(WIN_STREAK_GREEN, 3));

        JLabel dealerLabel = new JLabel("Dealer (You)", SwingConstants.CENTER);
        dealerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        dealerLabel.setForeground(WIN_STREAK_GREEN);

        dealerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        dealerCardsPanel.setOpaque(false);
        dealerCardsPanel.setPreferredSize(new Dimension(800, 150));

        dealerValueLabel = new JLabel("Value: -", SwingConstants.CENTER);
        dealerValueLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        dealerValueLabel.setForeground(Color.WHITE);

        dealerPanel.add(dealerLabel, BorderLayout.NORTH);
        dealerPanel.add(dealerCardsPanel, BorderLayout.CENTER);
        dealerPanel.add(dealerValueLabel, BorderLayout.SOUTH);

        // Player panel
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setOpaque(false);
        playerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        JLabel playerLabel = new JLabel("Player", SwingConstants.CENTER);
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        playerLabel.setForeground(Color.WHITE);

        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        playerCardsPanel.setOpaque(false);
        playerCardsPanel.setPreferredSize(new Dimension(800, 150));

        playerValueLabel = new JLabel("Value: -", SwingConstants.CENTER);
        playerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_LARGE));
        playerValueLabel.setForeground(Color.LIGHT_GRAY);

        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        playerPanel.add(playerValueLabel, BorderLayout.SOUTH);

        centerPanel.add(dealerPanel);
        centerPanel.add(playerPanel);

        // Bottom panel with controls, status and log
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        // Status and bet panel
        JPanel statusBetPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusBetPanel.setOpaque(false);

        statusLabel = new JLabel("Waiting for player to connect...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        statusLabel.setForeground(Color.WHITE);

        betLabel = new JLabel("Current Bet: $0", SwingConstants.CENTER);
        betLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_NORMAL));
        betLabel.setForeground(BET_RED);

        statusBetPanel.add(statusLabel);
        statusBetPanel.add(betLabel);

        // Control buttons
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setOpaque(false);

        startGameButton = new RedButton("Start Game");
        startGameButton.setEnabled(false);
        startGameButton.setPreferredSize(new Dimension(150, 40));
        startGameButton.addActionListener(e -> {
            server.startGame();
            startGameButton.setEnabled(false);
        });

        dealerHitButton = new RedButton("Hit");
        dealerHitButton.setEnabled(false);
        dealerHitButton.addActionListener(e -> {
            server.dealerHit();
        });

        dealerStandButton = new RedButton("Stand");
        dealerStandButton.setEnabled(false);
        dealerStandButton.addActionListener(e -> {
            server.dealerStand();
            dealerHitButton.setEnabled(false);
            dealerStandButton.setEnabled(false);
        });

        controlsPanel.add(startGameButton);
        controlsPanel.add(dealerHitButton);
        controlsPanel.add(dealerStandButton);

        // Log area
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setForeground(Color.LIGHT_GRAY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Server Log",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE));

        JPanel topBottomPanel = new JPanel(new BorderLayout(5, 5));
        topBottomPanel.setOpaque(false);
        topBottomPanel.add(statusBetPanel, BorderLayout.NORTH);
        topBottomPanel.add(controlsPanel, BorderLayout.CENTER);

        bottomPanel.add(topBottomPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);

        // Start the server
        startServer();
    }

    private void startServer() {
        server = new GameServer(this);
        try {
            server.start();
        } catch (Exception e) {
            onError("Failed to start server: " + e.getMessage());
        }
    }

    private void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public void onClientConnected(String clientAddress) {
        SwingUtilities.invokeLater(() -> {
            isPlayerConnected = true;
            statusLabel.setText("Player connected! Waiting for bet...");
            serverInfoLabel.setText("Server IP: " + server.getIPAddress() + " | Player: " + clientAddress);
            addLog("Player connected: " + clientAddress);
        });
    }

    @Override
    public void onClientDisconnected() {
        SwingUtilities.invokeLater(() -> {
            isPlayerConnected = false;
            statusLabel.setText("Player disconnected. Waiting for new player...");
            addLog("Player disconnected");
            clearCards();
            startGameButton.setEnabled(false);
            dealerHitButton.setEnabled(false);
            dealerStandButton.setEnabled(false);
        });
    }

    @Override
    public void onGameStateChanged(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            addLog(status);

            // Enable start button when bet is placed
            if (status.contains("placed bet") && isPlayerConnected) {
                startGameButton.setEnabled(true);
                betLabel.setText("Current Bet: " + status.substring(status.lastIndexOf("$")));
            }
        });
    }

    @Override
    public void onCardsDealt(List<Card> dealerCards, List<Card> playerCards) {
        SwingUtilities.invokeLater(() -> {
            // Show dealer's cards
            dealerCardsPanel.removeAll();
            int dealerValue = 0;
            for (Card card : dealerCards) {
                JLabel lbl = new JLabel(CardImages.getIcon(card));
                lbl.setToolTipText(card.toString());
                dealerCardsPanel.add(lbl);
                dealerValue += card.getRank().getValue();
            }
            dealerValueLabel.setText("Value: " + dealerValue);
            dealerCardsPanel.revalidate();
            dealerCardsPanel.repaint();

            // Show player's cards
            playerCardsPanel.removeAll();
            int playerValue = 0;
            for (Card card : playerCards) {
                JLabel lbl = new JLabel(CardImages.getIcon(card));
                lbl.setToolTipText(card.toString());
                playerCardsPanel.add(lbl);
                playerValue += card.getRank().getValue();
            }
            playerValueLabel.setText("Value: " + playerValue);
            playerCardsPanel.revalidate();
            playerCardsPanel.repaint();
        });
    }

    @Override
    public void onDealerCardReceived(Card card) {
        SwingUtilities.invokeLater(() -> {
            JLabel lbl = new JLabel(CardImages.getIcon(card));
            lbl.setToolTipText(card.toString());
            dealerCardsPanel.add(lbl);
            dealerCardsPanel.revalidate();
            dealerCardsPanel.repaint();

            // Update value display
            updateDealerValue();
        });
    }

    @Override
    public void onPlayerCardReceived(Card card) {
        SwingUtilities.invokeLater(() -> {
            JLabel lbl = new JLabel(CardImages.getIcon(card));
            lbl.setToolTipText(card.toString());
            playerCardsPanel.add(lbl);
            playerCardsPanel.revalidate();
            playerCardsPanel.repaint();

            // Update value display
            updatePlayerValue();
        });
    }

    @Override
    public void onTurnChanged(boolean isDealerTurn) {
        SwingUtilities.invokeLater(() -> {
            this.isDealerTurn = isDealerTurn;
            if (isDealerTurn) {
                dealerHitButton.setEnabled(true);
                dealerStandButton.setEnabled(true);
                statusLabel.setText(statusLabel.getText() + " - YOUR TURN!");
            } else {
                dealerHitButton.setEnabled(false);
                dealerStandButton.setEnabled(false);
            }
        });
    }

    @Override
    public void onError(String error) {
        SwingUtilities.invokeLater(() -> {
            addLog("ERROR: " + error);
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void updateDealerValue() {
        int value = 0;
        Component[] components = dealerCardsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                String tooltip = ((JLabel) comp).getToolTipText();
                if (tooltip != null && !tooltip.isEmpty()) {
                    // Simple value calculation (doesn't handle Aces properly, but close enough for display)
                    String[] parts = tooltip.split(" ");
                    if (parts.length > 0) {
                        String rank = parts[0];
                        if (rank.equals("ACE")) value += 11;
                        else if (rank.equals("KING") || rank.equals("QUEEN") || rank.equals("JACK")) value += 10;
                        else if (rank.equals("TEN")) value += 10;
                        else if (rank.equals("NINE")) value += 9;
                        else if (rank.equals("EIGHT")) value += 8;
                        else if (rank.equals("SEVEN")) value += 7;
                        else if (rank.equals("SIX")) value += 6;
                        else if (rank.equals("FIVE")) value += 5;
                        else if (rank.equals("FOUR")) value += 4;
                        else if (rank.equals("THREE")) value += 3;
                        else if (rank.equals("TWO")) value += 2;
                    }
                }
            }
        }
        dealerValueLabel.setText("Value: " + value);
    }

    private void updatePlayerValue() {
        int value = 0;
        Component[] components = playerCardsPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                String tooltip = ((JLabel) comp).getToolTipText();
                if (tooltip != null && !tooltip.isEmpty()) {
                    String[] parts = tooltip.split(" ");
                    if (parts.length > 0) {
                        String rank = parts[0];
                        if (rank.equals("ACE")) value += 11;
                        else if (rank.equals("KING") || rank.equals("QUEEN") || rank.equals("JACK")) value += 10;
                        else if (rank.equals("TEN")) value += 10;
                        else if (rank.equals("NINE")) value += 9;
                        else if (rank.equals("EIGHT")) value += 8;
                        else if (rank.equals("SEVEN")) value += 7;
                        else if (rank.equals("SIX")) value += 6;
                        else if (rank.equals("FIVE")) value += 5;
                        else if (rank.equals("FOUR")) value += 4;
                        else if (rank.equals("THREE")) value += 3;
                        else if (rank.equals("TWO")) value += 2;
                    }
                }
            }
        }
        playerValueLabel.setText("Value: " + value);
    }

    private void addLog(String message) {
        logArea.append("[" + java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void clearCards() {
        dealerCardsPanel.removeAll();
        playerCardsPanel.removeAll();
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
        dealerValueLabel.setText("Value: -");
        playerValueLabel.setText("Value: -");
        betLabel.setText("Current Bet: $0");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MultiplayerServerGUI::new);
    }
}
