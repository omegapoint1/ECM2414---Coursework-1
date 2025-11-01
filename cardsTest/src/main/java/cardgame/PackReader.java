package cardgame;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads a pack of cards from a text file.
 * Each line in the file must contain a single non-negative integer.
 * The pack must contain exactly 8 * numPlayers cards.
 */
public class PackReader {

    /**
     * Reads the pack from the given file path.
     *
     * @param path       Path to the pack file
     * @param numPlayers Number of players in the game
     * @return List of Card objects representing the pack
     * @throws IOException              If file not found or cannot be read
     * @throws IllegalArgumentException If file contains invalid values or wrong number of cards
     */
    public static List<Card> readPack(String path, int numPlayers) throws IOException {

        List<Card> pack = new ArrayList<>();

        File file = new File(path);

        // Check if the file exists
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + path);
        }

        // Read each line and parse into a Card
        try (Scanner scanner = new Scanner(file)) {

            int lineNumber = 0;

            while (scanner.hasNextLine()) {

                lineNumber++;
                String line = scanner.nextLine().trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                int value;

                // Parse integer and validate
                try {
                    value = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Non-integer at line " + lineNumber + ": '" + line + "'");
                }

                if (value < 0) {
                    throw new IllegalArgumentException("Negative value at line " + lineNumber + ": " + value);
                }

                pack.add(new Card(value));
            }
        }

        // Validate total number of cards
        int expectedSize = numPlayers * 8;

        if (pack.size() != expectedSize) {
            throw new IllegalArgumentException(
                "Pack must contain exactly " + expectedSize + " integers. Found: " + pack.size()
            );
        }

        return pack;
    }
}
