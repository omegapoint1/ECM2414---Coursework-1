package cardgame;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerDeckIntegrationTest {

    @Test
    void testPlayerDrawDiscard() throws Exception {
        Deck d1 = new Deck(1);
        Deck d2 = new Deck(2);

        // fill left deck
        d1.addCard(new Card(1));
        d1.addCard(new Card(2));

        Player p = new Player(1, d1, d2);
        p.addCard(new Card(1));
        p.addCard(new Card(1));
        p.addCard(new Card(1));
        p.addCard(new Card(2));

        Thread t = new Thread(p);
        t.start();
        t.join();

        // After run, left deck should have one card (if player drew one), right deck at least one
        List<Card> leftContents = d1.getContents();
        List<Card> rightContents = d2.getContents();

        assertTrue(leftContents.size() <= 1);
        assertTrue(rightContents.size() >= 1);
    }
}
