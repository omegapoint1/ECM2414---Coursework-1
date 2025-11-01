package cardgame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.io.*;
import java.util.List;

class PackReaderTest {

	// Helper method to create a temporary pack file with given content
	private static Path createTempPackFile(String content) throws IOException {
		Path temp = Files.createTempFile("pack", ".txt");
		Files.writeString(temp, content);
		return temp;
	}

	// Clean up leftover output files after each test
	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}

	@Test
	void testReadPackValid() throws IOException {
		// Valid pack: 16 cards for 2 players, values repeat 0–7
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) sb.append(i % 8).append("\n");
		Path packFile = createTempPackFile(sb.toString());

		List<Card> cards = PackReader.readPack(packFile.toString(), 2);

		// Check total card count and individual values
		assertEquals(16, cards.size());
		for (int i = 0; i < 16; i++) {
			assertEquals(i % 8, cards.get(i).getValue());
		}
	}

	@Test
	void testReadPackInvalidValue() throws IOException {
		// Pack contains a non-integer value ("two")
		Path packFile = createTempPackFile("0\n1\ntwo\n3\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 1)
		);
	}

	@Test
	void testReadPackNegativeValue() throws IOException {
		// Pack contains a negative integer (-1)
		Path packFile = createTempPackFile("0\n-1\n2\n3\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 1)
		);
	}

	@Test
	void testReadPackInvalidSize() throws IOException {
		// Pack has too few cards for the given player count
		Path packFile = createTempPackFile("0\n1\n2\n3\n4\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 2)
		);
	}
}
