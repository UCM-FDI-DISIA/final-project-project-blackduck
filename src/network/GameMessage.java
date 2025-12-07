package network;

import data.Card;
import java.io.Serializable;
import java.util.List;

/**
 * Message class for network communication between dealer and player
 */
public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        // Connection messages
        CONNECT_REQUEST,
        CONNECT_ACCEPT,
        DISCONNECT,

        // Game flow messages
        PLACE_BET,
        DEAL_CARDS,
        PLAYER_ACTION,
        ROUND_END,

        // State updates
        UPDATE_GAME_STATE,
        CARD_DEALT,
        DEALER_CARD_DEALT,
        TURN_CHANGED
    }

    public enum PlayerAction {
        HIT,
        STAND,
        DOUBLE_DOWN
    }

    private MessageType type;
    private String data;
    private int betAmount;
    private List<Card> cards;
    private Card singleCard;
    private int playerValue;
    private int dealerValue;
    private boolean playerBust;
    private boolean dealerBust;
    private boolean roundOver;
    private String statusMessage;
    private PlayerAction action;
    private boolean isPlayerTurn;
    private boolean isDealerTurn;
    private List<Card> dealerCards;

    public GameMessage(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Card getSingleCard() {
        return singleCard;
    }

    public void setSingleCard(Card card) {
        this.singleCard = card;
    }

    public int getPlayerValue() {
        return playerValue;
    }

    public void setPlayerValue(int playerValue) {
        this.playerValue = playerValue;
    }

    public int getDealerValue() {
        return dealerValue;
    }

    public void setDealerValue(int dealerValue) {
        this.dealerValue = dealerValue;
    }

    public boolean isPlayerBust() {
        return playerBust;
    }

    public void setPlayerBust(boolean playerBust) {
        this.playerBust = playerBust;
    }

    public boolean isDealerBust() {
        return dealerBust;
    }

    public void setDealerBust(boolean dealerBust) {
        this.dealerBust = dealerBust;
    }

    public boolean isRoundOver() {
        return roundOver;
    }

    public void setRoundOver(boolean roundOver) {
        this.roundOver = roundOver;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public PlayerAction getAction() {
        return action;
    }

    public void setAction(PlayerAction action) {
        this.action = action;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public boolean isDealerTurn() {
        return isDealerTurn;
    }

    public void setDealerTurn(boolean dealerTurn) {
        isDealerTurn = dealerTurn;
    }

    public List<Card> getDealerCards() {
        return dealerCards;
    }

    public void setDealerCards(List<Card> dealerCards) {
        this.dealerCards = dealerCards;
    }
}
