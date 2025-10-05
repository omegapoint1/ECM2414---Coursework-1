
import java.io.*;
import java.util.*;

public class PackReader {

    /**
     * Reads a pack file and returns the card values as a list of integers.
     * Validates that each value is non-negative and that the total count
     * matches 8 × numPlayers.
     *
     * @param path Path to the pack file
     * @param numPlayers Number of players in the game
     * @return List of card values
     * @throws IOException If the file cannot be read
     * @throws IllegalArgumentException If the file contains invalid values or
     * wrong number of cards
     */
    public static List<Card> readPack(String path, int numPlayers) throws IOException {
        List<Card> pack = new ArrayList<>(); // store card values

        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNextLine()) {
                // remove whitespace
                String line = scanner.nextLine().trim(); 

                // skip blank lines
                if (line.isEmpty()) {continue;}

                // parse integer
                int value = Integer.parseInt(line);
                Card tempCard = new Card(value);
                // reject negatives
                if (value < 0) 
                {
                    throw new IllegalArgumentException("Negative value: " + value);
                }
                // add valid card to list
                pack.add(tempCard);
            }

        } catch (FileNotFoundException e) {
            // file could not be found
            throw new FileNotFoundException("File not found: " + path);
        } catch (NumberFormatException e) {
            // line was not a valid integer
            throw new IllegalArgumentException("Pack file contains a non-integer value.");
        }

        // ensure total number of cards matches 8 × numPlayers
        if (pack.size() != numPlayers * 8) {
            throw new IllegalArgumentException(
                    "Pack must contain exactly " + (numPlayers * 8) + " integers."
            );
        }

        return pack; // return validated pack
    }

}
