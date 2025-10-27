package cardgame;

import cardgame.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {

    private static final int NUM_PLAYERS = 2;
	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}
    @AfterEach
    void cleanUpFiles() {
        for (int i = 1; i <= NUM_PLAYERS; i++) {
            File f = new File("player" + i + "_output.txt");
            if (f.exists()) f.delete();
        }
    }

    @Test
    void testSimultaneousPlayerActions() throws InterruptedException {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);

        Player player1 = new Player(1, leftDeck, rightDeck);
        Player player2 = new Player(2, leftDeck, rightDeck);

        // Initialize hands
        player1.addCard(new Card(1));
        player1.addCard(new Card(2));
        player1.addCard(new Card(3));
        player1.addCard(new Card(4));

        player2.addCard(new Card(5));
        player2.addCard(new Card(6));
        player2.addCard(new Card(7));
        player2.addCard(new Card(8));

        leftDeck.addCard(new Card(9));
        leftDeck.addCard(new Card(10));

        Thread t1 = new Thread(player1);
        Thread t2 = new Thread(player2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Check hand sizes via handSnapshot
        assertEquals(4, player1.handSnapshot().split(" ").length, "Player 1 should have 4 cards");
        assertEquals(4, player2.handSnapshot().split(" ").length, "Player 2 should have 4 cards");

        // Check deck contents
        List<Integer> combinedCards = leftDeck.getContents().stream().map(Card::getValue).collect(Collectors.toList());
        combinedCards.addAll(rightDeck.getContents().stream().map(Card::getValue).collect(Collectors.toList()));
        assertEquals(2 + 0, combinedCards.size(), "Decks should contain the expected remaining cards");
    }

    @Test
    void testDeadlockPrevention() throws InterruptedException {
        Deck d1 = new Deck(1);
        Deck d2 = new Deck(2);
        Deck d3 = new Deck(3);

        Player p1 = new Player(1, d1, d2);
        Player p2 = new Player(2, d2, d3);

        for (int i = 1; i <= 4; i++) p1.addCard(new Card(i));
        for (int i = 5; i <= 8; i++) p2.addCard(new Card(i));

        d1.addCard(new Card(9));
        d2.addCard(new Card(10));
        d3.addCard(new Card(11));

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        t1.start();
        t2.start();

        t1.join(2000);
        t2.join(2000);

        assertFalse(t1.isAlive(), "Player 1 thread should have completed");
        assertFalse(t2.isAlive(), "Player 2 thread should have completed");
    }

    @Test
    void testGameTerminationUnderConcurrency() throws InterruptedException {
        Deck deck1 = new Deck(1);
        Deck deck2 = new Deck(2);

        Player player1 = new Player(1, deck1, deck2);
        Player player2 = new Player(2, deck2, deck1);

        // Winning hands
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));
        player1.addCard(new Card(1));

        player2.addCard(new Card(2));
        player2.addCard(new Card(2));
        player2.addCard(new Card(2));
        player2.addCard(new Card(2));

        Thread t1 = new Thread(player1);
        Thread t2 = new Thread(player2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertTrue(CardGame.isGameWon(), "Game should have a winner");
        int winner = CardGame.getWinnerId();
        assertTrue(winner == 1 || winner == 2, "Winner should be player 1 or 2");
    }
}
	