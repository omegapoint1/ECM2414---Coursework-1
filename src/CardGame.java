import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/*
 * CardGame class
 * Entry point of the program. Handles user input, pack validation,
 * distribution of cards and decks, starting player threads,
 * and coordinating game termination.
 */
public class CardGame {

    public static void main(String[] args) {
        CardGame game = new CardGame();

        // TODO: read number of players
        int numPlayers = game.readNumPlayers();

        // TODO: read pack file path
        String path = game.readFilePath();

        // TODO: get valid pack
        List<Integer> pack = game.getValidPack(numPlayers);

        // TODO: distribute cards to players and decks

        // TODO: start player threads

        // TODO: monitor for winner and end game
    }
    
    private int readNumPlayers() {
        Scanner scanner = new Scanner(System.in);
        int n = 0;
        while (true) {
            System.out.print("Enter number of players: ");
            String input = scanner.nextLine();
            try {
                n = Integer.parseInt(input);
                if (n > 0) {
                    break;
                }
                System.out.println("Number of players must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a positive integer.");
            }
        }
        return n;
    }

    private String readFilePath() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter pack file path: ");
        return scanner.nextLine().trim();
    }

    private List<Integer> getValidPack(int numPlayers) {
        String path = readFilePath(); // prompt user once
        while (true) {
            try {
                return PackReader.readPack(path, numPlayers);
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Invalid pack: " + e.getMessage());
                path = readFilePath(); // re-prompt for a new file path
            }
        }
    }

}
