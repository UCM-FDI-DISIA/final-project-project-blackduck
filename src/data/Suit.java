package data;

public enum Suit {
    HEARTS("hearts"),
    DIAMONDS("diamonds"),
    CLUBS("clubs"),
    SPADES("spades");

    private final String displayName;

    Suit(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name for card image filenames.
     */
    public String getDisplayName() {
        return displayName;
    }
}