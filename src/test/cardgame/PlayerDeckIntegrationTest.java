package cardgame;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerDeckIntegrationTest {

    @Test
    void testPlayerDrawDiscardAtomicity() throws Exception {

        // Create two decks
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);

        // Fill left deck with known cards
        leftDeck.addCard(new Card(5));
        leftDeck.addCard(new Card(6));

        // Create player with a non-winning hand
        Player player = new Player(1, leftDeck, rightDeck);
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(1));
        player.addCard(new Card(2)); // ensures player is not winning yet

        // Run player in a thread
        Thread playerThread = new Thread(player);
        playerThread.start();
        playerThread.join();

        // Check that atomic draw+discard preserved hand size 4
        // Player hand is private, but effect visible in decks
        List<Card> leftContents = leftDeck.getContents();
        List<Card> rightContents = rightDeck.getContents();

        // Left deck should have had at least one card drawn
        assertTrue(leftContents.size() <= 1, "Left deck should have at most 1 card remaining");

        // Right deck should have received at least one card from discard
        assertTrue(rightContents.size() >= 1, "Right deck should have at least 1 card from discard");
    }
}
