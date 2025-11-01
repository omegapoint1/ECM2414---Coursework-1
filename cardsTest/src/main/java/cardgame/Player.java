package cardgame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the card game.
 * Each player runs on its own thread, draws and discards cards,
 * and writes actions to its output file.
 */
public class Player implements Runnable {

    private final int playerId;
    private final List<Card> hand = new ArrayList<>();
    private final Deck leftDeck;
    private final Deck rightDeck;
    private final int preferredValue;
    private final String outputFile;

    /**
     * Constructs a Player with ID and references to left and right decks.
     */
    public Player(int playerId, Deck leftDeck, Deck rightDeck) {
        this.playerId = playerId;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.preferredValue = playerId;
        this.outputFile = "player" + playerId + "_output.txt";
    }

    /**
     * Adds a card to the player's hand.
     */
    public void addCard(Card c) {
        hand.add(c);
    }

    /**
     * Checks if the player has a winning hand (all four cards have the same value).
     */
    private boolean hasWinningHand() {

        if (hand.size() != 4) {
            return false;
        }

        int firstValue = hand.get(0).getValue();

        for (int i = 1; i < 4; i++) {
            if (hand.get(i).getValue() != firstValue) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a snapshot of the current hand as a space-separated string.
     */
    public String handSnapshot() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i).getValue());

            if (i < hand.size() - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * Chooses a card to discard based on the preferred value.
     * Discards a non-preferred card if available, otherwise discards the first card.
     */
    private Card chooseCardToDiscard() {

        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredValue) {
                return hand.remove(i);
            }
        }

        return hand.remove(0);
    }

    /**
     * Main player loop.
     * Draws from the left deck and discards to the right deck until a winner is declared.
     */
    @Override
    public void run() {

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile, true), true)) {

            // Write initial hand to output
            writer.println("player " + playerId + " initial hand " + handSnapshot());

            // Immediate win check
            if (hasWinningHand() && CardGame.tryDeclareWin(playerId)) {
                System.out.println("player " + playerId + " wins");
                writer.println("player " + playerId + " wins");
                writer.println("player " + playerId + " exits");
                writer.println("player " + playerId + " final hand: " + handSnapshot());
                return;
            }

            // Main game loop
            while (!CardGame.isGameWon()) {

                // Wait for a card to be available or game end
                try {
                    if (!leftDeck.awaitCardOrGameEnd()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    continue;
                }

                // Determine lock order to prevent deadlocks
                Deck firstLock = leftDeck.getDeckId() < rightDeck.getDeckId() ? leftDeck : rightDeck;
                Deck secondLock = (firstLock == leftDeck) ? rightDeck : leftDeck;

                firstLock.lock();
                secondLock.lock();

                Card drawn = null;
                Card discarded = null;

                try {
                    if (CardGame.isGameWon()) {
                        break;
                    }

                    // Draw a card from the left deck
                    drawn = leftDeck.pollFirst();

                    if (drawn == null) {
                        continue;
                    }

                    hand.add(drawn);

                    discarded = chooseCardToDiscard();

                    // Discard to the right deck
                    rightDeck.addCard(discarded);

                } finally {
                    secondLock.unlock();
                    firstLock.unlock();
                }

                // Write actions to output file
                writer.println("player " + playerId + " draws a " + drawn.getValue() + " from deck " + leftDeck.getDeckId());
                writer.println("player " + playerId + " discards a " + discarded.getValue() + " to deck " + rightDeck.getDeckId());
                writer.println("player " + playerId + " current hand is " + handSnapshot());

                // Check for winning hand after draw/discard
                if (hasWinningHand() && CardGame.tryDeclareWin(playerId)) {
                    System.out.println("player " + playerId + " wins");
                    writer.println("player " + playerId + " wins");
                    break;
                }
            }

            // Notify player if another player has won
            int winner = CardGame.getWinnerId();

            if (winner != -1 && winner != playerId) {
                writer.println("player " + winner + " has informed player " + playerId + " that player " + winner + " has won");
            }

            // Write exit and final hand
            writer.println("player " + playerId + " exits");

            if (winner == playerId) {
                writer.println("player " + playerId + " final hand: " + handSnapshot());
            } else {
                writer.println("player " + playerId + " hand: " + handSnapshot());
            }

        } catch (IOException e) {
            System.err.println("Error writing to " + outputFile + ": " + e.getMessage());
        }
    }
}
