import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;


/*
 * CardGame class
 * Entry point of the program. Handles user input, pack validation,
 * distribution of cards and decks, starting player threads,
 * and coordinating game termination.
 */
public class CardGame {

    // shared flag for all threads to detect if the game has ended
    private static volatile boolean gameWon = false;
    public static boolean isGameWon() {return gameWon;}
    public static void setGameWon() {gameWon = true;}

    public static synchronized boolean tryDeclareWin() {
        if (!gameWon) {
            gameWon = true;
            return true; // caller successfully declared win
        }
        return false; // another player already won
    }

    public static void main(String[] args) {
        CardGame game = new CardGame();

        // TODO: read number of players
        int numPlayers = game.readNumPlayers();

        // TODO: read pack file path
        String path = game.readFilePath();

        // TODO: get valid pack
        List<Card> pack = game.getValidPack(path, numPlayers);

        // create decks and players
        Deck[] decks = createDecks(numPlayers);
        Player[] players = createPlayers(numPlayers, decks);

        // TODO: distribute cards to players and decks
        distributeCards(pack, players, decks);

        // start player threads
        startPlayerThreads(players);

        writeDeckOutputs(decks);
        System.out.println("Game finished.");

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

    private List<Card> getValidPack(String path, int numPlayers) {
        while (true) {
            try {
                return PackReader.readPack(path, numPlayers);
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Invalid pack: " + e.getMessage());
                path = readFilePath(); // only re-prompt if invalid
            }
        }
    }

    private static Deck[] createDecks(int numPlayers) {
        Deck[] decks = new Deck[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            decks[i] = new Deck(i + 1); // deck IDs start at 1
        }
        return decks;
    }

    private static Player[] createPlayers(int numPlayers, Deck[] decks) {
        Player[] players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            Deck leftDeck = decks[i];                     // draw from this deck
            Deck rightDeck = decks[(i + 1) % numPlayers]; // discard to this deck
            players[i] = new Player(i + 1, leftDeck, rightDeck);
        }
        return players;
    }

    private static void distributeCards(List<Card> pack, Player[] players, Deck[] decks) {
        int numPlayers = players.length;

        // 1. Distribute 4 cards to each player (round-robin)
        int packIndex = 0;
        for (int round = 0; round < 4; round++) {
            for (Player p : players) {
                p.addCard(pack.get(packIndex++));
            }
        }

        // 2. Distribute remaining cards to decks (round-robin)
        for (int i = packIndex; i < pack.size(); i++) {
            decks[i % numPlayers].addCard(pack.get(i));
        }
    }

    private static void startPlayerThreads(Player[] players) {
        Thread[] threads = new Thread[players.length];

        // start threads
        for (int i = 0; i < players.length; i++) {
            threads[i] = new Thread(players[i]);
            threads[i].start();
        }

        // wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    private static void writeDeckOutputs(Deck[] decks) {
        for (Deck d : decks) {
            List<Card> contents = d.getContents();
            try (PrintWriter pw = new PrintWriter("deck" + d.getDeckId() + "_output.txt")) {
                pw.print("deck" + d.getDeckId() + " contents: ");
                for (Card c : contents) {
                    pw.print(c + " ");
                }
                pw.println();
            } catch (IOException e) {
                System.err.println("Error writing deck output for deck " + d.getDeckId() + ": " + e.getMessage());
            }
        }
    }
}


