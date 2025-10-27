package cardgame;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the multi-threaded card game simulation.
 * Handles reading inputs, creating decks and players, distributing cards,
 * starting player threads, and writing final outputs.
 */
public class CardGame {

    private static volatile boolean gameWon = false;   // Flag indicating whether a player has won
    private static volatile int winnerId = -1;         // ID of winning player
    private static Deck[] allDecks;                    // Array of all decks for signalling threads

    /** Returns true if the game has already been won. */
    public static boolean isGameWon() {
        return gameWon;
    }

    /** Returns the ID of the winning player, or -1 if none yet. */
    public static int getWinnerId() {
        return winnerId;
    }

    /**
     * Attempt to declare the calling player as the winner.
     * If successful, updates game state and signals all waiting deck threads.
     *
     * @param playerId ID of the player trying to declare victory
     * @return true if the win was successfully declared, false otherwise
     */
    public static synchronized boolean tryDeclareWin(int playerId) {
        if (!gameWon) {
            gameWon = true;
            winnerId = playerId;

            // Wake up all threads waiting on decks
            if (allDecks != null) {
                for (Deck d : allDecks) {
                    d.signalAllWaiting();
                }
            }
            return true;
        }
        return false;
    }

    /** Main method: sets up and runs the card game simulation. */
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            // Read number of players from console input
            int numPlayers = readNumPlayers(scanner);

            // Read pack file path from console input
            String path = readFilePath(scanner);

            // Read and validate the pack contents
            List<Card> pack = getValidPack(path, numPlayers, scanner);

            // Create decks and register them globally for signalling threads
            Deck[] decks = createDecks(numPlayers);
            allDecks = decks;

            // Create player objects and assign left/right decks
            Player[] players = createPlayers(numPlayers, decks);

            // Distribute initial hands to players and remaining cards to decks
            distributeCards(pack, players, decks);

            // Start player threads and wait for them to complete
            startPlayerThreads(players);

            // Write final deck contents to output files
            writeDeckOutputs(decks);

            // Print game completion message
            System.out.println("Game finished.");
        }
    }

    /** Reads and validates the number of players from console input. */
    private static int readNumPlayers(Scanner scanner) {
        while (true) {
            System.out.print("Please enter the number of players:");
            String input = scanner.nextLine();
            try {
                int n = Integer.parseInt(input.trim());
                if (n > 0) return n;
                System.out.println("Number of players must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a positive integer.");
            }
        }
    }

    /** Reads a non-empty file path from console input. */
    private static String readFilePath(Scanner scanner) {
        while (true) {
            System.out.print("Please enter location of pack to load:");
            String p = scanner.nextLine().trim();
            if (!p.isEmpty()) return p;
            System.out.println("Path cannot be empty.");
        }
    }

    /** Reads and validates the pack; retries until a valid pack is provided. */
    private static List<Card> getValidPack(String path, int numPlayers, Scanner consoleScanner) {
        while (true) {
            try {
                return PackReader.readPack(path, numPlayers);
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Invalid pack: " + e.getMessage());
                System.out.print("Enter pack file path: ");
                path = consoleScanner.nextLine().trim();
            }
        }
    }

    /** Creates an array of decks with sequential IDs from 1 to numPlayers. */
    private static Deck[] createDecks(int numPlayers) {
        Deck[] decks = new Deck[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            decks[i] = new Deck(i + 1);
        }
        return decks;
    }

    /** Creates player objects, assigning each their left and right decks. */
    private static Player[] createPlayers(int numPlayers, Deck[] decks) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            Deck left = decks[i];
            Deck right = decks[(i + 1) % numPlayers];
            players[i] = new Player(i + 1, left, right);
        }
        return players;
    }

    /**
     * Distributes cards to players and decks.
     * - First 4 * numPlayers cards are dealt round-robin to players (initial hands).
     * - Remaining cards are distributed round-robin to the decks.
     */
    private static void distributeCards(List<Card> pack, Player[] players, Deck[] decks) {
        int numPlayers = players.length;
        int index = 0;

        for (int round = 0; round < 4; round++) {
            for (Player p : players) {
                p.addCard(pack.get(index++));
            }
        }

        for (int i = index; i < pack.size(); i++) {
            decks[i % numPlayers].addCard(pack.get(i));
        }
    }

    /** Starts all player threads and waits for them to finish execution. */
    private static void startPlayerThreads(Player[] players) {
        Thread[] threads = new Thread[players.length];

        for (int i = 0; i < players.length; i++) {
            threads[i] = new Thread(players[i]);
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    /** Writes the final contents of all decks to their respective output files. */
    private static void writeDeckOutputs(Deck[] decks) {
        for (Deck d : decks) {
            try (PrintWriter pw = new PrintWriter("deck" + d.getDeckId() + "_output.txt")) {
                pw.print("deck" + d.getDeckId() + " contents:");
                for (Card c : d.getContents()) {
                    pw.print(" " + c.getValue());
                }
                pw.println();
            } catch (IOException e) {
                System.err.println("Error writing deck output for deck " + d.getDeckId() + ": " + e.getMessage());
            }
        }
    }
}
