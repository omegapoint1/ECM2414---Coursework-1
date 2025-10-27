package cardgame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import cardgame.PackReader;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.io.*;
import java.util.List;

class PackReaderTest {

	private static Path createTempPackFile(String content) throws IOException {
		Path temp = Files.createTempFile("pack", ".txt");
		Files.writeString(temp, content);
		return temp;
	}

	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}

	@Test
	void testReadPackValid() throws IOException {
		// File contains 0,1,2,...,7 repeated for 2 players (16 cards)
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) sb.append(i % 8).append("\n");
		Path packFile = createTempPackFile(sb.toString());

		List<Card> cards = PackReader.readPack(packFile.toString(), 2);
		assertEquals(16, cards.size());
		for (int i = 0; i < 16; i++) {
			assertEquals(i % 8, cards.get(i).getValue());
		}
	}

	@Test
	void testReadPackInvalidValue() throws IOException {
		// File contains non-integer
		Path packFile = createTempPackFile("0\n1\ntwo\n3\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 1)
		);
	}

	@Test
	void testReadPackNegativeValue() throws IOException {
		Path packFile = createTempPackFile("0\n-1\n2\n3\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 1)
		);
	}

	@Test
	void testReadPackInvalidSize() throws IOException {
		// File contains wrong number of cards
		Path packFile = createTempPackFile("0\n1\n2\n3\n4\n");
		assertThrows(IllegalArgumentException.class, () ->
			PackReader.readPack(packFile.toString(), 2)
		);
	}
}
