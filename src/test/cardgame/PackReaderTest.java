package cardgame;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
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
                () -> PackReader.readPack(tempFile.getAbsolutePath(), 1),
