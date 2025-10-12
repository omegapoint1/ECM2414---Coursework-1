package cardgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardValue() {
        Card c = new Card(5);
        assertEquals(5, c.getValue());
    }

    @Test
    void testNegativeValueThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Card(-1));
    }

    @Test
    void testEqualsAndHashCode() {
        Card c1 = new Card(3);
        Card c2 = new Card(3);
        Card c3 = new Card(4);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        Card c = new Card(7);
        assertEquals("7", c.toString());
    }
}
