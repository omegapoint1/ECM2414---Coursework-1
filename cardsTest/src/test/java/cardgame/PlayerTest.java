package cardgame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import cardgame.Deck;
import cardgame.Player;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

class PlayerTest {

	private final Path[] filesToClean = {
		Path.of("player1_output.txt"),
		Path.of("player2_output.txt"),
        Path.of("player3_output.txt"),
		Path.of("deck1_output.txt"),
		Path.of("deck2_output.txt")
	};

	private ExecutorService exec; // shared executor reference for cleanup

	@AfterEach
	void cleanEnvironment() {
		// Delete any leftover output files
		for (Path p : filesToClean) {
			try {
				Files.deleteIfExists(p);
			} catch (IOException ignored) {
				// File may still be locked, ignore safely
			}
		}

		// Forcefully shut down executor if still running
		if (exec != null && !exec.isShutdown()) {
			exec.shutdownNow();
			try {
				if (!exec.awaitTermination(1, TimeUnit.SECONDS)) {
					System.err.println("Warning: executor did not terminate cleanly");
				}
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Test
	void testAddCardAndHandSnapshot() {
		Deck leftDeck = new Deck(1);
		Deck rightDeck = new Deck(2);
		Player player = new Player(1, leftDeck, rightDeck);

		player.addCard(new Card(1));
		player.addCard(new Card(2));
		player.addCard(new Card(1));
		player.addCard(new Card(1));
		player.addCard(new Card(3));

		assertEquals("1 2 1 1 3", player.handSnapshot());
	}

	@Test
	void testWinningHandRecognition() throws Exception {
		Deck leftDeck = new Deck(1);
		Deck rightDeck = new Deck(2);

		Player player = new Player(3, leftDeck, rightDeck);
		player.addCard(new Card(3));
		player.addCard(new Card(3));
		player.addCard(new Card(3));
		player.addCard(new Card(3));

		exec = Executors.newSingleThreadExecutor();
		Future<?> future = exec.submit(player);

		exec.shutdown();
		if (!exec.awaitTermination(5, TimeUnit.SECONDS)) {
			exec.shutdownNow();
			fail("Player thread did not finish in time");
		}

		assertEquals("3 3 3 3", player.handSnapshot());
	}
}
