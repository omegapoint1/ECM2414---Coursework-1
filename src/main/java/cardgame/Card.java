package cardgame;

/**
 * Represents a single playing card with a non-negative integer value.
 * Immutable and thread-safe.
 */
public class Card {

    // The value of this card (denomination)
    private final int value;

    /**
     * Creates a card with the specified value.
     *
     * @param value non-negative integer for card denomination
     * @throws IllegalArgumentException if value is negative
     */
    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value must be non-negative.");
        }
        this.value = value;
    }

    /**
     * Returns the card's value.
     *
     * @return integer value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a string representation of the card (its value).
     *
     * @return value as string
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Compares this card to another object for equality.
     * Two cards are equal if their values are the same.
     *
     * @param obj object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Card)) {
            return false;
        }

        Card other = (Card) obj;
        return value == other.value;
    }

    /**
     * Returns the hash code of this card.
     * Consistent with equals().
     *
     * @return integer hash code
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
