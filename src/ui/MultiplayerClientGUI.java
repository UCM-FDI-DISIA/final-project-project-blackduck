package ui;

import data.Card;
import network.GameClient;
import network.GameMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static ui.UIConstants.*;

/**
 * GUI for the client (player) in multiplayer mode
 */
public class MultiplayerClientGUI extends JFrame implements GameClient.ClientListener {

    private GameClient client;
    private String serverIP;

    private JLabel statusLabel;
    private JLabel connectionLabel;
    private JLabel chipsLabel;
    private JLabel betLabel;
    private JPanel playerCardsPanel;
    private JPanel dealerCardsPanel;
    private JLabel playerValueLabel;
    private JLabel dealerValueLabel;

    private RedButton hitButton;
    private RedButton standButton;
    private RedButton doubleButton;
    private RedButton dealButton;
    private RedButton bet5Button;
    private RedButton bet10Button;
    private RedButton bet25Button;
    private RedButton bet50Button;
    private RedButton clearBetButton;

    private int chips = 100;
    private int currentBet = 0;
    private boolean roundStarted = false;

    public MultiplayerClientGUI(String serverIP) {
        super("Blackjack - Multiplayer Player");
        this.serverIP = serverIP;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Allow ESC to exit
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitApp");
        getRootPane().getActionMap().put("exitApp", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (client != null) {
                    client.disconnect();
                }
                System.exit(0);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(20, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        statsPanel.setOpaque(false);

        chipsLabel = new JLabel("Chips: $" + chips);
        chipsLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        chipsLabel.setForeground(CHIPS_GOLD);

        betLabel = new JLabel("Current Bet: $0");
        betLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_LARGE));
        betLabel.setForeground(BET_RED);

        connectionLabel = new JLabel("Connecting...");
        connectionLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_NORMAL));
        connectionLabel.setForeground(Color.YELLOW);

        statsPanel.add(chipsLabel);
        statsPanel.add(betLabel);
        statsPanel.add(connectionLabel);

        RedButton disconnectButton = new RedButton("Disconnect");
        disconnectButton.addActionListener(e -> {
            if (client != null) {
                client.disconnect();
            }
            System.exit(0);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(disconnectButton);

        topPanel.add(statsPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Center panel with cards
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setOpaque(false);

        // Dealer panel
        JPanel dealerPanel = new JPanel(new BorderLayout());
        dealerPanel.setOpaque(false);

        JLabel dealerLabel = new JLabel("Dealer");
        dealerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        dealerLabel.setForeground(Color.WHITE);

        dealerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        dealerCardsPanel.setOpaque(false);

        dealerValueLabel = new JLabel("");
        dealerValueLabel.setForeground(Color.LIGHT_GRAY);
        dealerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));

        dealerPanel.add(dealerLabel, BorderLayout.NORTH);
        dealerPanel.add(dealerCardsPanel, BorderLayout.CENTER);
        dealerPanel.add(dealerValueLabel, BorderLayout.SOUTH);

        // Player panel
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setOpaque(false);

        JLabel playerLabel = new JLabel("You");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, FONT_SIZE_MEDIUM));
        playerLabel.setForeground(Color.WHITE);

        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        playerCardsPanel.setOpaque(false);

        playerValueLabel = new JLabel("");
        playerValueLabel.setForeground(Color.LIGHT_GRAY);
        playerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));

        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        playerPanel.add(playerValueLabel, BorderLayout.SOUTH);

        centerPanel.add(dealerPanel);
        centerPanel.add(playerPanel);

        // Bottom panel with controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        statusLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, FONT_SIZE_NORMAL));

        // Betting controls
        JPanel bettingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bettingPanel.setOpaque(false);

        bet5Button = new RedButton("Bet $5");
        bet10Button = new RedButton("Bet $10");
        bet25Button = new RedButton("Bet $25");
        bet50Button = new RedButton("Bet $50");
        clearBetButton = new RedButton("Clear Bet");

        bet5Button.addActionListener(e -> placeBet(5));
        bet10Button.addActionListener(e -> placeBet(10));
        bet25Button.addActionListener(e -> placeBet(25));
        bet50Button.addActionListener(e -> placeBet(50));
        clearBetButton.addActionListener(e -> clearBet());

        bettingPanel.add(bet5Button);
        bettingPanel.add(bet10Button);
        bettingPanel.add(bet25Button);
        bettingPanel.add(bet50Button);
        bettingPanel.add(clearBetButton);

        // Game controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setOpaque(false);

        dealButton = new RedButton("Deal Cards");
        hitButton = new RedButton("Hit");
        doubleButton = new RedButton("Double Down");
        standButton = new RedButton("Stand");

        dealButton.addActionListener(e -> requestDeal());
        hitButton.addActionListener(e -> sendAction(GameMessage.PlayerAction.HIT));
        doubleButton.addActionListener(e -> sendAction(GameMessage.PlayerAction.DOUBLE_DOWN));
        standButton.addActionListener(e -> sendAction(GameMessage.PlayerAction.STAND));

        controlsPanel.add(dealButton);
        controlsPanel.add(hitButton);
        controlsPanel.add(doubleButton);
        controlsPanel.add(standButton);

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(bettingPanel, BorderLayout.CENTER);
        bottomPanel.add(controlsPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Initial button states
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        dealButton.setEnabled(false);
        disableBetting();

        setContentPane(mainPanel);
        setVisible(true);

        // Connect to server
        connectToServer();
    }

    private void connectToServer() {
        client = new GameClient(this);
        client.connect(serverIP);
    }

    private void placeBet(int amount) {
        if (roundStarted) return;

        if (chips >= amount) {
            currentBet += amount;
            chips -= amount;
            updateStatsDisplay();

            client.placeBet(amount);

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

    private void requestDeal() {
        if (currentBet == 0) {
            statusLabel.setText("Place a bet first!");
            return;
        }

        // Send bet and wait for dealer to start game
        client.placeBet(currentBet);
        roundStarted = true;
        disableBetting();
        dealButton.setEnabled(false);
        statusLabel.setText("Bet placed! Waiting for dealer to start game...");
    }

    private void sendAction(GameMessage.PlayerAction action) {
        client.sendAction(action);

        if (action == GameMessage.PlayerAction.DOUBLE_DOWN) {
            if (chips >= currentBet) {
                chips -= currentBet;
                currentBet *= 2;
                updateStatsDisplay();
            }
        }

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
    }

    private void updateStatsDisplay() {
        chipsLabel.setText("Chips: $" + chips);
        betLabel.setText("Current Bet: $" + currentBet);
    }

    private void disableBetting() {
        bet5Button.setEnabled(false);
        bet10Button.setEnabled(false);
        bet25Button.setEnabled(false);
        bet50Button.setEnabled(false);
        clearBetButton.setEnabled(false);
    }

    private void enableBetting() {
        bet5Button.setEnabled(true);
        bet10Button.setEnabled(true);
        bet25Button.setEnabled(true);
        bet50Button.setEnabled(true);
        clearBetButton.setEnabled(true);
    }

    @Override
    public void onConnected() {
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("Connected");
            connectionLabel.setForeground(WIN_STREAK_GREEN);
            statusLabel.setText("Connected! Place your bet to start.");
            enableBetting();
        });
    }

    @Override
    public void onConnectionFailed(String reason) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    "Connection failed: " + reason,
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        });
    }

    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("Disconnected");
            connectionLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Disconnected from server",
                    "Connection Lost",
                    JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        });
    }

    @Override
    public void onMessageReceived(GameMessage message) {
        SwingUtilities.invokeLater(() -> handleMessage(message));
    }

    private void handleMessage(GameMessage message) {
        switch (message.getType()) {
            case CONNECT_ACCEPT:
                statusLabel.setText(message.getData());
                break;

            case TURN_CHANGED:
                // Turn changed - show waiting message
                statusLabel.setText(message.getStatusMessage());
                if (message.isDealerTurn()) {
                    // Disable player controls
                    hitButton.setEnabled(false);
                    standButton.setEnabled(false);
                    doubleButton.setEnabled(false);
                }
                break;

            case DEALER_CARD_DEALT:
                // Dealer drew a card
                if (message.getSingleCard() != null) {
                    addDealerCard(message.getSingleCard());
                }
                if (message.getDealerCards() != null && !message.getDealerCards().isEmpty()) {
                    updateDealerCards(message.getDealerCards());
                    dealerValueLabel.setText("Value: " + message.getDealerValue());
                }
                statusLabel.setText(message.getStatusMessage());
                break;

            case UPDATE_GAME_STATE:
                // Initial cards dealt
                updatePlayerCards(message.getCards());
                playerValueLabel.setText("Value: " + message.getPlayerValue());

                // Show dealer's first card (only one visible)
                if (message.getDealerCards() != null && !message.getDealerCards().isEmpty()) {
                    updateDealerCards(message.getDealerCards());
                    dealerValueLabel.setText("Value: ?");
                }

                statusLabel.setText(message.getStatusMessage());

                if (message.isPlayerTurn()) {
                    hitButton.setEnabled(true);
                    standButton.setEnabled(true);

                    if (chips >= currentBet) {
                        doubleButton.setEnabled(true);
                    }
                } else {
                    statusLabel.setText("Waiting for dealer...");
                }
                break;

            case CARD_DEALT:
                // Player received a card
                if (message.getSingleCard() != null) {
                    addPlayerCard(message.getSingleCard());
                    playerValueLabel.setText("Value: " + message.getPlayerValue());
                }

                statusLabel.setText(message.getStatusMessage());

                if (message.isPlayerBust() || message.isRoundOver()) {
                    hitButton.setEnabled(false);
                    standButton.setEnabled(false);
                    doubleButton.setEnabled(false);
                    if (!message.isRoundOver()) {
                        // Just busted, but round not over yet
                        statusLabel.setText(message.getStatusMessage());
                    } else {
                        endRound(message);
                    }
                } else {
                    doubleButton.setEnabled(false);
                }
                break;

            case ROUND_END:
                // Show all dealer cards
                if (message.getDealerCards() != null && !message.getDealerCards().isEmpty()) {
                    updateDealerCards(message.getDealerCards());
                }
                dealerValueLabel.setText("Value: " + message.getDealerValue());

                statusLabel.setText(message.getStatusMessage());

                // Update chips based on result
                int winnings = message.getBetAmount();
                if (winnings > 0) {
                    chips += winnings;
                } else {
                    // Check for push
                    if (message.getPlayerValue() == message.getDealerValue() &&
                            !message.isPlayerBust() && !message.isDealerBust()) {
                        chips += currentBet; // Return bet
                    }
                }

                endRound(message);
                break;

            default:
                // Handle other message types that don't need client action
                break;
        }
    }

    private void updatePlayerCards(List<Card> cards) {
        playerCardsPanel.removeAll();
        for (Card card : cards) {
            JLabel lbl = new JLabel(CardImages.getIcon(card));
            lbl.setToolTipText(card.toString());
            playerCardsPanel.add(lbl);
        }
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    private void addPlayerCard(Card card) {
        JLabel lbl = new JLabel(CardImages.getIcon(card));
        lbl.setToolTipText(card.toString());
        playerCardsPanel.add(lbl);
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
    }

    private void updateDealerCards(List<Card> cards) {
        dealerCardsPanel.removeAll();
        for (Card card : cards) {
            JLabel lbl = new JLabel(CardImages.getIcon(card));
            lbl.setToolTipText(card.toString());
            dealerCardsPanel.add(lbl);
        }
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
    }

    private void addDealerCard(Card card) {
        JLabel lbl = new JLabel(CardImages.getIcon(card));
        lbl.setToolTipText(card.toString());
        dealerCardsPanel.add(lbl);
        dealerCardsPanel.revalidate();
        dealerCardsPanel.repaint();
    }

    private void endRound(GameMessage message) {
        roundStarted = false;
        currentBet = 0;

        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        dealButton.setEnabled(false);

        updateStatsDisplay();

        if (chips == 0) {
            statusLabel.setText("Out of chips! Game Over.");
        } else {
            enableBetting();
        }
    }

    @Override
    public void onError(String error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: MultiplayerClientGUI <server-ip>");
            System.exit(1);
        }
        String serverIP = args[0];
        SwingUtilities.invokeLater(() -> new MultiplayerClientGUI(serverIP));
    }
}
