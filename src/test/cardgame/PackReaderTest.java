package cardgame;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PackReader.
 * Verifies correct reading of valid packs and proper rejection of invalid packs.
 */
class PackReaderTest {

    @Test
    void testValidPack() throws Exception {
        // Create temporary file with 8 zeros for 1 player
        File tempFile = File.createTempFile("pack", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            for (int i = 0; i < 8; i++) writer.write("0\n");
        }

        // Read pack and verify size and first card
        List<Card> pack = PackReader.readPack(tempFile.getAbsolutePath(), 1);
        assertEquals(8, pack.size(), "Pack should contain exactly 8 cards");
        assertEquals(0, pack.get(0).getValue(), "First card value should be 0");

        tempFile.delete();
    }

    @Test
    void testInvalidNumberOfCards() throws Exception {
        // Create temporary file with only 1 card
        File tempFile = File.createTempFile("pack", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("0\n");
        }

        // Reading should fail due to wrong number of cards
        assertThrows(IllegalArgumentException.class,
                () -> PackReader.readPack(tempFile.getAbsolutePath(), 1),
                "Pack with wrong number of cards should throw exception");

        tempFile.delete();
    }

    @Test
    void testNegativeCard() throws Exception {
        // Create temporary file with a negative card
        File tempFile = File.createTempFile("pack", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("-1\n0\n0\n0\n0\n0\n0\n0\n");
        }

        // Reading should fail due to negative value
        assertThrows(IllegalArgumentException.class,
                () -> PackReader.readPack(tempFile.getAbsolutePath(), 1));

    }

     @Test
    void testReadPackValidNormalCase() throws IOException {
        // Arrange: create a temporary file with 16 valid integers (0-7 repeated twice)
        File tempFile = Files.createTempFile("pack", ".txt").toFile();
        tempFile.deleteOnExit(); // ensure cleanup

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            for (int i = 0; i < 2; i++) {        // 2 players → 16 cards (8 per player)
                for (int j = 0; j < 8; j++) {
                    writer.println(j);
                }
            }
        }

        // Reading the pack
        List<Card> cards = PackReader.readPack(tempFile.getAbsolutePath(), 2);

        // list should contain exactly 16 cards with correct values
        assertEquals(16, cards.size(), "Pack should contain 16 cards");

        for (int i = 0; i < 16; i++) {
            int expectedValue = i % 8; // 0-7 repeated
            assertEquals(expectedValue, cards.get(i).getValue(),
                    "Card at index " + i + " should have value " + expectedValue);
        }
    }

     @Test
    void testReadPackThrowsForNegativeValue() throws IOException {
        // creating a temporary file with a negative number
        File tempFile = Files.createTempFile("negativePack", ".txt").toFile();
        tempFile.deleteOnExit();

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println(-5); // invalid negative card
            // Fill remaining cards to meet 8*numPlayers requirement
            for (int i = 0; i < 7; i++) {
                writer.println(i);
            }
        }

        // should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> PackReader.readPack(tempFile.getAbsolutePath(), 1));
    }

    @Test
    void testReadPackThrowsForWrongNumberOfCards() throws IOException {
        // Creating a temporary file with fewer cards than required
        File tempFile = Files.createTempFile("wrongSizePack", ".txt").toFile();
        tempFile.deleteOnExit();

        int numPlayers = 2;       // expecting 16 cards (2*8)
        int actualCards = 10;     // fewer than required

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            for (int i = 0; i < actualCards; i++) {
                writer.println(i);  // write integers 0..9
            }
        }

        // should throw IllegalArgumentException due to invalid pack size
        assertThrows(IllegalArgumentException.class, () -> PackReader.readPack(tempFile.getAbsolutePath(), numPlayers)
        );
    }

     @Test
    void testReadPackThrowsFileNotFound() {
        // Using a file path that doesn't exist
        String nonExistentPath = "this_file_does_not_exist.txt";
        int numPlayers = 2;

        // should throw FileNotFoundException
        assertThrows(FileNotFoundException.class, () -> PackReader.readPack(nonExistentPath, numPlayers));
    }





}
