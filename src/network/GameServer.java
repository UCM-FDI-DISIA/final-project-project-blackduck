package network;

import data.Card;
import logic.Deck;
import logic.Hand;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class for hosting a blackjack game (acts as the dealer)
 */
public class GameServer {
    private static final int DEFAULT_PORT = 7777;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running;
    private ServerListener listener;

    private Deck deck;
    private Hand playerHand;
    private Hand dealerHand;
    private int currentBet;
    private boolean gameStarted = false;
    private boolean isPlayerTurn = false;

    public interface ServerListener {
        void onClientConnected(String clientAddress);
        void onClientDisconnected();
        void onGameStateChanged(String status);
        void onError(String error);
        void onCardsDealt(List<Card> dealerCards, List<Card> playerCards);
        void onDealerCardReceived(Card card);
        void onPlayerCardReceived(Card card);
        void onTurnChanged(boolean isDealerTurn);
    }

    public GameServer(ServerListener listener) {
        this.listener = listener;
        this.running = false;
    }

    /**
     * Start the server and wait for a client connection
     */
    public void start() throws IOException {
        start(DEFAULT_PORT);
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;

        new Thread(() -> {
            try {
                listener.onGameStateChanged("Server started on port " + port + ". Waiting for player...");

                clientSocket = serverSocket.accept();
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                listener.onClientConnected(clientSocket.getInetAddress().getHostAddress());

                // Send connection acceptance
                GameMessage acceptMsg = new GameMessage(GameMessage.MessageType.CONNECT_ACCEPT);
                acceptMsg.setData("Welcome to Blackjack!");
                sendMessage(acceptMsg);

                // Start listening for messages
                listen();

            } catch (IOException e) {
                if (running) {
                    listener.onError("Server error: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Listen for incoming messages from the client
     */
    private void listen() {
        new Thread(() -> {
            try {
                while (running && !clientSocket.isClosed()) {
                    GameMessage message = (GameMessage) in.readObject();
                    handleMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    listener.onClientDisconnected();
                }
            }
        }).start();
    }

    /**
     * Handle incoming messages from the client
     */
    private void handleMessage(GameMessage message) {
        switch (message.getType()) {
            case PLACE_BET:
                currentBet = message.getBetAmount();
                listener.onGameStateChanged("Player placed bet: $" + currentBet);
                break;

            case DEAL_CARDS:
                // Player requests to deal - ignored, dealer controls this now
                break;

            case PLAYER_ACTION:
                handlePlayerAction(message.getAction());
                break;

            case DISCONNECT:
                listener.onClientDisconnected();
                break;

            default:
                break;
        }
    }

    /**
     * Start the game - called by dealer when ready
     */
    public void startGame() {
        if (!gameStarted && currentBet > 0) {
            dealInitialCards();
        }
    }

    /**
     * Deal initial cards to player and dealer
     */
    private void dealInitialCards() {
        deck = new Deck();
        deck.shuffle();
        playerHand = new Hand();
        dealerHand = new Hand();

        // Deal 2 cards to player and dealer
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());
        playerHand.addCard(deck.drawCard());
        dealerHand.addCard(deck.drawCard());

        gameStarted = true;
        isPlayerTurn = true;

        // Notify dealer GUI
        listener.onCardsDealt(new ArrayList<>(dealerHand.getCards()),
                              new ArrayList<>(playerHand.getCards()));
        listener.onTurnChanged(false); // Player's turn

        // Send initial cards to player
        GameMessage msg = new GameMessage(GameMessage.MessageType.UPDATE_GAME_STATE);
        msg.setCards(new ArrayList<>(playerHand.getCards()));
        msg.setPlayerValue(playerHand.getValue());

        // Only send first dealer card
        List<Card> dealerFirstCard = new ArrayList<>();
        dealerFirstCard.add(dealerHand.getCards().get(0));
        msg.setDealerCards(dealerFirstCard);
        msg.setStatusMessage("Your turn! Hit or Stand?");
        msg.setPlayerTurn(true);
        msg.setDealerTurn(false);

        sendMessage(msg);
        listener.onGameStateChanged("Cards dealt. Player's turn. Player value: " + playerHand.getValue());
    }

    /**
     * Handle player actions (Hit, Stand, Double Down)
     */
    private void handlePlayerAction(GameMessage.PlayerAction action) {
        if (action == null) return;

        switch (action) {
            case HIT:
                Card drawnCard = deck.drawCard();
                playerHand.addCard(drawnCard);

                listener.onPlayerCardReceived(drawnCard);

                GameMessage hitMsg = new GameMessage(GameMessage.MessageType.CARD_DEALT);
                hitMsg.setSingleCard(drawnCard);
                hitMsg.setPlayerValue(playerHand.getValue());
                hitMsg.setPlayerBust(playerHand.isBust());
                hitMsg.setPlayerTurn(!playerHand.isBust());

                if (playerHand.isBust()) {
                    hitMsg.setRoundOver(true);
                    hitMsg.setStatusMessage("Bust! Dealer wins.");
                    isPlayerTurn = false;
                    gameStarted = false;
                    listener.onGameStateChanged("Player busted! You win!");
                    listener.onTurnChanged(true);
                } else {
                    hitMsg.setStatusMessage("Your turn! Hit or Stand?");
                }

                sendMessage(hitMsg);
                break;

            case STAND:
                isPlayerTurn = false;
                listener.onGameStateChanged("Player stands. Your turn!");
                listener.onTurnChanged(true); // Dealer's turn

                // Notify player to wait
                GameMessage waitMsg = new GameMessage(GameMessage.MessageType.TURN_CHANGED);
                waitMsg.setStatusMessage("Waiting for dealer's move...");
                waitMsg.setPlayerTurn(false);
                waitMsg.setDealerTurn(true);
                sendMessage(waitMsg);
                break;

            case DOUBLE_DOWN:
                Card doubleCard = deck.drawCard();
                playerHand.addCard(doubleCard);
                currentBet *= 2;

                GameMessage doubleMsg = new GameMessage(GameMessage.MessageType.CARD_DEALT);
                doubleMsg.setSingleCard(doubleCard);
                doubleMsg.setPlayerValue(playerHand.getValue());
                doubleMsg.setPlayerBust(playerHand.isBust());
                doubleMsg.setBetAmount(currentBet);

                listener.onPlayerCardReceived(doubleCard);

                if (playerHand.isBust()) {
                    doubleMsg.setRoundOver(true);
                    doubleMsg.setStatusMessage("Bust! Dealer wins.");
                    doubleMsg.setPlayerTurn(false);
                    isPlayerTurn = false;
                    gameStarted = false;
                    listener.onGameStateChanged("Player busted after doubling down! You win!");
                    listener.onTurnChanged(true);
                    sendMessage(doubleMsg);
                } else {
                    doubleMsg.setPlayerTurn(false);
                    sendMessage(doubleMsg);
                    isPlayerTurn = false;
                    listener.onGameStateChanged("Player doubled down. Your turn!");
                    listener.onTurnChanged(true);

                    // Notify player to wait
                    GameMessage waitMsg2 = new GameMessage(GameMessage.MessageType.TURN_CHANGED);
                    waitMsg2.setStatusMessage("Waiting for dealer's move...");
                    waitMsg2.setPlayerTurn(false);
                    waitMsg2.setDealerTurn(true);
                    sendMessage(waitMsg2);
                }
                break;
        }
    }

    /**
     * Dealer hits - called by dealer GUI
     */
    public void dealerHit() {
        if (!isPlayerTurn && gameStarted) {
            Card dealerCard = deck.drawCard();
            dealerHand.addCard(dealerCard);
            listener.onDealerCardReceived(dealerCard);

            // Send card to player
            GameMessage msg = new GameMessage(GameMessage.MessageType.DEALER_CARD_DEALT);
            msg.setSingleCard(dealerCard);
            msg.setDealerCards(new ArrayList<>(dealerHand.getCards()));
            msg.setDealerValue(dealerHand.getValue());
            msg.setStatusMessage("Dealer hits. Waiting for dealer...");
            sendMessage(msg);

            if (dealerHand.isBust()) {
                listener.onGameStateChanged("You busted! Player wins!");
                calculateResults(true); // Player wins
            }
        }
    }

    /**
     * Dealer stands - called by dealer GUI
     */
    public void dealerStand() {
        if (!isPlayerTurn && gameStarted) {
            listener.onGameStateChanged("You stand. Calculating results...");
            calculateResults(null); // Calculate based on values
        }
    }

    /**
     * Calculate and send round results
     */
    private void calculateResults(Boolean playerWon) {
        int playerValue = playerHand.getValue();
        int dealerValue = dealerHand.getValue();
        String result;

        if (playerWon == null) {
            // Calculate based on values
            if (dealerHand.isBust()) {
                result = "Dealer busted! You win!";
                playerWon = true;
            } else if (playerValue > dealerValue) {
                result = "You win! (" + playerValue + " vs " + dealerValue + ")";
                playerWon = true;
            } else if (playerValue < dealerValue) {
                result = "Dealer wins. (" + playerValue + " vs " + dealerValue + ")";
                playerWon = false;
            } else {
                result = "Push (tie). (" + playerValue + " vs " + dealerValue + ")";
                playerWon = null;
            }
        } else {
            if (playerWon) {
                result = "Player wins!";
            } else {
                result = "Dealer wins!";
            }
        }

        // Send round end message
        GameMessage endMsg = new GameMessage(GameMessage.MessageType.ROUND_END);
        endMsg.setDealerCards(new ArrayList<>(dealerHand.getCards()));
        endMsg.setPlayerValue(playerValue);
        endMsg.setDealerValue(dealerValue);
        endMsg.setDealerBust(dealerHand.isBust());
        endMsg.setStatusMessage(result);
        endMsg.setRoundOver(true);

        if (playerWon != null && playerWon) {
            endMsg.setBetAmount(currentBet * 2);
        } else if (playerWon == null) {
            endMsg.setBetAmount(currentBet); // Push - return bet
        } else {
            endMsg.setBetAmount(0);
        }

        sendMessage(endMsg);
        listener.onGameStateChanged(result);

        gameStarted = false;
        isPlayerTurn = false;
        currentBet = 0;
    }

    /**
     * Send a message to the client
     */
    private void sendMessage(GameMessage message) {
        try {
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            listener.onError("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Helper method to serialize cards as a string
     */
    private String serializeCards(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.toString()).append(";");
        }
        return sb.toString();
    }

    /**
     * Get the server's IP address
     */
    public String getIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }

    /**
     * Stop the server
     */
    public void stop() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            listener.onError("Error stopping server: " + e.getMessage());
        }
    }
}
