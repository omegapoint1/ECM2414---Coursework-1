package cardgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;

class CardTest {

	// Remove any leftover output files to keep environment clean between tests
	@AfterEach
	void cleanEnvironment() throws IOException {
		Files.deleteIfExists(Path.of("player1_output.txt"));
		Files.deleteIfExists(Path.of("player2_output.txt"));
		Files.deleteIfExists(Path.of("deck1_output.txt"));
		Files.deleteIfExists(Path.of("deck2_output.txt"));
	}

	@Test
	void testCardCreationNormal() {
		// Valid card creation with positive value
		Card c = new Card(5);
		assertEquals(5, c.getValue());
	}

	@Test
	void testCardCreationEdge() {
		// Edge case: zero is allowed
		Card c = new Card(0);
		assertEquals(0, c.getValue());
	}

	@Test
	void testCardCreationInvalid() {
		// Negative values should be rejected
		assertThrows(IllegalArgumentException.class, () -> new Card(-1));
	}

	@Test
	void testGetValue() {
		// getValue() should return the assigned card value
		Card c = new Card(5);
		assertEquals(5, c.getValue());
	}

	@Test
	void testEquals() {
		// Two cards with the same value should be equal
		Card a = new Card(5);
		Card b = new Card(5);
		assertTrue(a.equals(b));
	}

	@Test
	void testToString() {
		// toString() should return the string form of the card value
		Card c = new Card(7);
		assertEquals("7", c.toString());
	}
}
