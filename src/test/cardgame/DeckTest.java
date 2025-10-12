package cardgame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class DeckTest {

    @Test
    void testEmptyPoll() {
        Deck deck = new Deck(1);
        assertNull(deck.pollFirstIfAvailable(), "Empty deck should return null");
    }

    @Test
    void testAddAndPoll() {
        Deck deck = new Deck(1);
        Card c1 = new Card(10);
        Card c2 = new Card(20);

        deck.addCard(c1); // uses internal lock
        deck.addCard(c2);

        // poll should return first inserted (FIFO)
        Card drawn1;
        Card drawn2;

        deck.lock();
        try {
            drawn1 = deck.pollFirstIfAvailable();
            drawn2 = deck.pollFirstIfAvailable();
        } finally {
            deck.unlock();
        }

        assertEquals(c1, drawn1, "First card drawn should match first added card");
        assertEquals(c2, drawn2, "Second card drawn should match second added card");
        assertNull(deck.pollFirstIfAvailable(), "Deck should now be empty");
    }
}
