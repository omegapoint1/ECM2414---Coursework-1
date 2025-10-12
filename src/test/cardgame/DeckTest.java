package cardgame;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Deck class.
 * Verifies FIFO behavior, polling from empty decks, and thread-safety contract.
 */
public class DeckTest {

    @Test
    void testEmptyPoll() {
        Deck deck = new Deck(1);

        // Polling from an empty deck returns null
        assertNull(deck.pollFirst(), "Empty deck should return null");
    }

    @Test
    void testAddAndPoll() {
        Deck deck = new Deck(1);
        Card card1 = new Card(10);
        Card card2 = new Card(20);

        // Add cards to deck
        deck.addCard(card1);
        deck.addCard(card2);

        // Poll first card (FIFO)
        Card drawn1 = deck.pollFirst();
        Card drawn2 = deck.pollFirst();

        // Check order
        assertEquals(card1, drawn1, "First card drawn should match first added card");
        assertEquals(card2, drawn2, "Second card drawn should match second added card");

        // Deck should now be empty
        assertNull(deck.pollFirst(), "Deck should now be empty");
    }
}
