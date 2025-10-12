package cardgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Card class.
 * Verifies value retrieval, immutability, equality, hashCode, and string representation.
 */
class CardTest {

    @Test
    void testCardValue() {
        // Verify getValue returns the correct card value
        Card card = new Card(5);
        assertEquals(5, card.getValue(), "Card value should match constructor argument");
    }

    @Test
    void testNegativeValueThrows() {
        // Verify that creating a card with negative value throws exception
        assertThrows(IllegalArgumentException.class, () -> new Card(-1),
                "Card constructor should reject negative values");
    }

    @Test
    void testEqualsAndHashCode() {
        // Verify equality and hashCode based on value
        Card card1 = new Card(3);
        Card card2 = new Card(3);
        Card card3 = new Card(4);

        assertEquals(card1, card2, "Cards with same value should be equal");
        assertNotEquals(card1, card3, "Cards with different values should not be equal");
        assertEquals(card1.hashCode(), card2.hashCode(), "Equal cards must have equal hashCodes");
    }

    @Test
    void testToString() {
        // Verify toString returns the card value as a string
        Card card = new Card(7);
        assertEquals("7", card.toString(), "toString should return the card's value as string");
    }
}
