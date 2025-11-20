import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Blackjack with Swing GUI + main menu, dark theme, sounds,
 * animated background, fullscreen and card graphics.
 */
public class BlackjackGUI extends JFrame implements ActionListener {

    // Card layout to switch between menu and game
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;

    // GAME screen components
    private final JPanel dealerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JLabel dealerValueLabel = new JLabel("");

    private final JPanel playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private final JLabel playerValueLabel = new JLabel("");

    private final JLabel statusLabel = new JLabel("Click \"New Round\" to start.");

    private final RedButton hitButton = new RedButton("Hit");
    private final RedButton standButton = new RedButton("Stand");
    private final RedButton newRoundButton = new RedButton("New Round");

    private boolean roundOver = false;

    public BlackjackGUI() {
        super("Blackjack");

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
        JPanel gamePanel = createGamePanel();

        cardPanel.add(menuPanel, "MENU");
        cardPanel.add(gamePanel, "GAME");

        setContentPane(cardPanel);
        cardLayout.show(cardPanel, "MENU");

        setVisible(true);
    }

    // ---------- MENU UI (dark theme + animated background) ----------
    private JPanel createMenuPanel() {
        AnimatedBackgroundPanel panel = new AnimatedBackgroundPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("BLACKJACK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);

        // Logo (optional)
        JLabel logoLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon logoIcon = new ImageIcon("logo.png"); // put logo.png next to this file
        logoLabel.setIcon(logoIcon);

        // Buttons
        RedButton startButton = new RedButton("Start Game");
        RedButton quitButton = new RedButton("Quit");

        startButton.addActionListener(e -> {
            startNewRound();
            cardLayout.show(cardPanel, "GAME");
        });

        quitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(logoLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ---------- GAME UI (dark theme + card graphics) ----------
    private JPanel createGamePanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Dealer panel
        JPanel dealerPanel = new JPanel(new BorderLayout());
        dealerPanel.setOpaque(false);
        JLabel dealerLabel = new JLabel("Dealer");
        dealerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        dealerLabel.setForeground(Color.WHITE);
        dealerCardsPanel.setOpaque(false);
        dealerValueLabel.setForeground(Color.LIGHT_GRAY);
        dealerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        dealerPanel.add(dealerLabel, BorderLayout.NORTH);
        dealerPanel.add(dealerCardsPanel, BorderLayout.CENTER);
        dealerPanel.add(dealerValueLabel, BorderLayout.SOUTH);

        // Player panel
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setOpaque(false);
        JLabel playerLabel = new JLabel("Player");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        playerLabel.setForeground(Color.WHITE);
        playerCardsPanel.setOpaque(false);
        playerValueLabel.setForeground(Color.LIGHT_GRAY);
        playerValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerCardsPanel, BorderLayout.CENTER);
        playerPanel.add(playerValueLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(dealerPanel);
        centerPanel.add(playerPanel);

        // Controls + status
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsPanel.setOpaque(false);
        controlsPanel.add(hitButton);
        controlsPanel.add(standButton);
        controlsPanel.add(newRoundButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(controlsPanel, BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        newRoundButton.addActionListener(this);

        // Initial state on game screen
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        return root;
    }

    // ---------- GAME LOGIC / ACTIONS ----------

    private void startNewRound() {
        deck = new Deck();
        deck.shuffle();
        playerHand = new Hand();
        dealerHand = new Hand();
        roundOver = false;

        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        updateLabels(false);

        statusLabel.setText("Your move: Hit or Stand?");
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
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
        if (roundOver) return;

        Card drawn = deck.drawCard();
        playerHand.addCard(drawn);
        updateLabels(false);

        if (playerHand.isBust()) {
            roundOver = true;
            hitButton.setEnabled(false);
            standButton.setEnabled(false);
            updateLabels(true);
            statusLabel.setText("You busted! Dealer wins.");
        } else if (playerHand.getValue() == 21) {
            playerStand();
        }
    }

    private void playerStand() {
        if (roundOver) return;
        hitButton.setEnabled(false);
        standButton.setEnabled(false);

        dealerTurn();
        roundOver = true;
    }

    private void dealerTurn() {
        updateLabels(true);

        if (playerHand.isBlackjack() || dealerHand.isBlackjack()) {
            String result;
            if (playerHand.isBlackjack() && dealerHand.isBlackjack()) {
                result = "Both have Blackjack! It's a push.";
            } else if (playerHand.isBlackjack()) {
                result = "You have Blackjack! You win!";
            } else {
                result = "Dealer has Blackjack! You lose.";
            }
            statusLabel.setText(result);
            return;
        }

        while (dealerHand.getValue() < 17) {
            Card drawn = deck.drawCard();
            dealerHand.addCard(drawn);
            updateLabels(true);
            try {
                Thread.sleep(250); // tiny delay so you can see cards appear
            } catch (InterruptedException ignored) {}
        }

        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();

        updateLabels(true);

        String result;
        if (dealerHand.isBust()) {
            result = "Dealer busted! You win!";
        } else if (playerValue > dealerValue) {
            result = "You win! (" + playerValue + " vs " + dealerValue + ")";
        } else if (playerValue < dealerValue) {
            result = "Dealer wins. (" + playerValue + " vs " + dealerValue + ")";
        } else {
            result = "It's a push (tie). (" + playerValue + " vs " + dealerValue + ")";
        }

        statusLabel.setText(result);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == hitButton) {
            playerHit();
        } else if (src == standButton) {
            playerStand();
        } else if (src == newRoundButton) {
            startNewRound();
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

/* ===== Custom rounded red button ===== */

class RedButton extends JButton {

    public RedButton(String text) {
        super(text);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setBackground(new Color(200, 0, 0));
        setFont(new Font("SansSerif", Font.BOLD, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        setContentAreaFilled(false);
        setOpaque(false);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(230, 0, 0));
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(200, 0, 0));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        BlackjackGUI.playClickSound();
        super.fireActionPerformed(event);
    }

    @Override
    public void paintBorder(Graphics g) {
        // No border
    }
}

/* ===== Animated menu background ===== */

class AnimatedBackgroundPanel extends JPanel implements ActionListener {

    private final Timer timer;
    private double angle = 0;

    public AnimatedBackgroundPanel() {
        setBackground(new Color(10, 10, 10));
        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(new Color(10, 10, 10));
        g2.fillRect(0, 0, w, h);

        int radius = 250;
        int x1 = (int) (w / 2 + Math.cos(angle) * w / 4) - radius / 2;
        int y1 = (int) (h / 2 + Math.sin(angle) * h / 4) - radius / 2;

        Color c1 = new Color(120, 0, 0, 120);
        Color c2 = new Color(0, 120, 0, 120);
        Color c3 = new Color(0, 0, 120, 120);

        g2.setColor(c1);
        g2.fillOval(x1, y1, radius, radius);
        g2.setColor(c2);
        g2.fillOval(w - x1 - radius, y1, radius, radius);
        g2.setColor(c3);
        g2.fillOval(w / 2 - radius / 2, h - y1 - radius, radius, radius);

        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angle += 0.02;
        repaint();
    }
}

/* ===== Game Logic Classes ===== */

class Deck {
    private final List<Card> cards;
    private int currentIndex = 0;

    public Deck() {
        cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
        currentIndex = 0;
    }

    public Card drawCard() {
        if (currentIndex >= cards.size()) {
            throw new IllegalStateException("No more cards in the deck.");
        }
        return cards.get(currentIndex++);
    }
}

class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getValue() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            int value = card.getRank().getValue();
            total += value;
            if (card.getRank() == Rank.ACE) {
                aces++;
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isBust() {
        return getValue() > 21;
    }
}

enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10),
    ACE(11);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

class Card {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank.name() + " of " + suit.name();
    }
}

/* ===== Card images helper ===== */

class CardImages {

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static ImageIcon backIcon;

    // card image: cards/AS.png, cards/10D.png, etc.
    public static ImageIcon getIcon(Card card) {
        String code = getCode(card);
        return CACHE.computeIfAbsent(code, key -> loadCardIcon(key));
    }

    public static ImageIcon getBackIcon() {
        if (backIcon == null) {
            File f = new File("cards/back.png");
            if (f.exists()) {
                backIcon = new ImageIcon(f.getPath());
            } else {
                backIcon = createPlaceholder("?", Color.DARK_GRAY);
            }
        }
        return backIcon;
    }

    private static String getCode(Card card) {
        String r;
        switch (card.getRank()) {
            case ACE: r = "A"; break;
            case TWO: r = "2"; break;
            case THREE: r = "3"; break;
            case FOUR: r = "4"; break;
            case FIVE: r = "5"; break;
            case SIX: r = "6"; break;
            case SEVEN: r = "7"; break;
            case EIGHT: r = "8"; break;
            case NINE: r = "9"; break;
            case TEN: r = "10"; break;
            case JACK: r = "J"; break;
            case QUEEN: r = "Q"; break;
            case KING: r = "K"; break;
            default: r = "?";
        }

        String s;
        switch (card.getSuit()) {
            case HEARTS: s = "H"; break;
            case DIAMONDS: s = "D"; break;
            case CLUBS: s = "C"; break;
            case SPADES: s = "S"; break;
            default: s = "?";
        }
        return r + s;
    }

    private static ImageIcon loadCardIcon(String code) {
        String path = "cards/" + code + ".png";
        File f = new File(path);
        if (f.exists()) {
            return new ImageIcon(f.getPath());
        }

        // fallback placeholder
        Color tint = Color.LIGHT_GRAY;
        return createPlaceholder(code, tint);
    }

    private static ImageIcon createPlaceholder(String text, Color tint) {
        int w = 80;
        int h = 120;
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
