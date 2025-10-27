package cardgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;

class CardTest {
	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}
	@Test
	void testCardCreationNormal() {
		Card c = new Card(5);
		assertEquals(5, c.getValue());
	}

	@Test
	void testCardCreationEdge() {
		Card c = new Card(0);
		assertEquals(0, c.getValue());
	}

	@Test
	void testCardCreationInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new Card(-1));
	}

	@Test
	void testGetValue() {
		Card c = new Card(5);
		assertEquals(5, c.getValue());
	}

	@Test
	void testEquals() {
		Card a = new Card(5);
		Card b = new Card(5);
		assertTrue(a.equals(b));
	}

	@Test
	void testToString() {
		Card c = new Card(7);
		assertEquals("7", c.toString());
	}
}
