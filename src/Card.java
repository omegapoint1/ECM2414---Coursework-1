
public class Card {

    private final int value; // card denomination

    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value must be non-negative.");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card c = (Card) o;
        return value == c.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
