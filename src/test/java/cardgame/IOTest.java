package cardgame;

import cardgame.CardGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IOTest {

	private static final int NUM_PLAYERS = 2;
	private Path packFile;
	private Path[] playerFiles;
	private Path[] deckFiles;

	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}


	@AfterEach
	void cleanUpFiles() throws Exception {
		if (packFile != null && Files.exists(packFile))
			Files.delete(packFile);

		if (playerFiles != null) {
			for (Path f : playerFiles)
				if (f != null && Files.exists(f))
					Files.delete(f);
		}

		if (deckFiles != null) {
			for (Path f : deckFiles)
				if (f != null && Files.exists(f))
					Files.delete(f);
		}
	}

	@Test
	void testPlayerLoggingAndDeckOutput() throws Exception {
		// Create a valid pack file (16 cards for 2 players)
		packFile = Files.createTempFile("pack", ".txt");
		Files.write(packFile, List.of(
				"0", "1", "2", "3",
				"0", "1", "2", "3",
				"1", "2", "3", "0",
				"1", "2", "3", "0"
		));

		// Simulate user input
		String input = NUM_PLAYERS + "\n" + packFile.toAbsolutePath() + "\n";
		InputStream originalIn = System.in;
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Expected output files
		playerFiles = new Path[NUM_PLAYERS];
		deckFiles = new Path[NUM_PLAYERS];
		for (int i = 0; i < NUM_PLAYERS; i++) {
			playerFiles[i] = Path.of("player" + (i + 1) + "_output.txt");
			deckFiles[i] = Path.of("deck" + (i + 1) + "_output.txt");
		}

		// Run the game
		CardGame.main(new String[]{});

		// Restore input
		System.setIn(originalIn);

		// Check player logs
		for (int i = 0; i < NUM_PLAYERS; i++) {
			int playerNum = i + 1;
			assertTrue(Files.exists(playerFiles[i]), "Missing player" + playerNum + "_output.txt");

			List<String> lines = Files.readAllLines(playerFiles[i]);
			assertTrue(lines.stream().anyMatch(l -> l.contains("player " + playerNum + " initial hand")),
					"Player " + playerNum + " log missing initial hand");
			assertTrue(lines.stream().anyMatch(l -> l.contains("draws") || l.contains("discards")),
					"Player " + playerNum + " log missing actions");
			assertTrue(lines.stream().anyMatch(l -> l.contains("final hand")
					|| l.contains("wins")
					|| l.contains("has informed")),
					"Player " + playerNum + " log missing end-of-game info");
		}

		// Check deck logs
		for (int i = 0; i < NUM_PLAYERS; i++) {
			int deckNum = i + 1;
			assertTrue(Files.exists(deckFiles[i]), "Missing deck" + deckNum + "_output.txt");

			List<String> lines = Files.readAllLines(deckFiles[i]);
			assertTrue(!lines.isEmpty() && lines.get(0).startsWith("deck" + deckNum + " contents:"),
					"Deck " + deckNum + " log missing or malformed header");
		}
	}
}
