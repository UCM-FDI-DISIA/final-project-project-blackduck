package network;

import java.io.*;
import java.net.Socket;

/**
 * Client class for connecting to a blackjack game server
 */
public class GameClient {
    private static final int DEFAULT_PORT = 7777;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private final ClientListener listener;

    public interface ClientListener {
        void onConnected();
        void onConnectionFailed(String reason);
        void onDisconnected();
        void onMessageReceived(GameMessage message);
        void onError(String error);
    }

    public GameClient(ClientListener listener) {
        this.listener = listener;
        this.connected = false;
    }

    /**
     * Connect to the game server
     */
    public void connect(String host) {
        connect(host, DEFAULT_PORT);
    }

    public void connect(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;

                // Send connection request
                GameMessage connectMsg = new GameMessage(GameMessage.MessageType.CONNECT_REQUEST);
                sendMessage(connectMsg);

                listener.onConnected();

                // Start listening for messages
                listen();

            } catch (IOException e) {
                listener.onConnectionFailed("Failed to connect: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Listen for incoming messages from the server
     */
    private void listen() {
        new Thread(() -> {
            try {
                while (connected && !socket.isClosed()) {
                    GameMessage message = (GameMessage) in.readObject();
                    listener.onMessageReceived(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                if (connected) {
                    listener.onDisconnected();
                    connected = false;
                }
            }
        }).start();
    }

    /**
     * Send a bet to the server
     */
    public void placeBet(int amount) {
        GameMessage msg = new GameMessage(GameMessage.MessageType.PLACE_BET);
        msg.setBetAmount(amount);
        sendMessage(msg);
    }

    /**
     * Send player action (Hit, Stand, Double Down)
     */
    public void sendAction(GameMessage.PlayerAction action) {
        GameMessage msg = new GameMessage(GameMessage.MessageType.PLAYER_ACTION);
        msg.setAction(action);
        sendMessage(msg);
    }

    /**
     * Send a message to the server
     */
    private void sendMessage(GameMessage message) {
        try {
            if (out != null && connected) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            listener.onError("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Disconnect from the server
     */
    public void disconnect() {
        if (connected) {
            GameMessage msg = new GameMessage(GameMessage.MessageType.DISCONNECT);
            sendMessage(msg);
        }

        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            listener.onError("Error disconnecting: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
