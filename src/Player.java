
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Player implements Runnable {

    private final int playerId;
    private final List<Integer> hand;
    private final Deck leftDeck;
    private final Deck rightDeck;
    private final int preferredValue;
    private final String outputFile;

    public Player(int playerId, Deck leftDeck, Deck rightDeck) {
        this.playerId = playerId;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.preferredValue = playerId; // player i prefers i
        this.hand = new ArrayList<>();
        this.outputFile = "player" + playerId + "_output.txt";
    }

    // Add a card to the player's hand
    public void addCard(int card) {
        hand.add(card);
    }

    // Check if player has 4 of same value
    private boolean hasWinningHand() {
        if (hand.size() != 4) {
            return false; // safety check
        }
        int first = hand.get(0);
        // check if all cards match the first card
        for (int i = 1; i < 4; i++) {
            if (hand.get(i) != first) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void run() {
        logAction("player " + playerId + " initial hand " + getHandSnapshot());

        // check initial win
        synchronized (CardGame.class) {
            if (!CardGame.isGameWon() && hasWinningHand()) {
                CardGame.setGameWon();
                System.out.println("Player " + playerId + " wins");
                logAction("player " + playerId + " wins");
                return;
            }
        }

        while (!CardGame.isGameWon()) {
            int drawnCard = leftDeck.drawCard();
            hand.add(drawnCard);

            int discardCard = chooseCardToDiscard();
            rightDeck.addCard(discardCard);

            logAction("player " + playerId + " draws a " + drawnCard + " from deck " + leftDeck.getDeckId());
            logAction("player " + playerId + " discards a " + discardCard + " to deck " + rightDeck.getDeckId());
            logAction("player " + playerId + " current hand " + getHandSnapshot());

            synchronized (CardGame.class) {
                if (!CardGame.isGameWon() && hasWinningHand()) {
                    CardGame.setGameWon();
                    System.out.println("Player " + playerId + " wins");
                    logAction("player " + playerId + " wins");
                    break;
                }
            }
        }

        logAction("player " + playerId + " exits");
        logAction("player " + playerId + " final hand: " + getHandSnapshot());
    }




    // helpers
    private String getHandSnapshot() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            sb.append(hand.get(i));
            if (i < hand.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    private int chooseCardToDiscard() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) != preferredValue) {
                return hand.remove(i);
            }
        }
        return hand.remove(0); // all cards preferred, discard first
    }

    private void logAction(String message) {
        try (FileWriter fw = new FileWriter(outputFile, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);  // write message to playerX_output.txt
        } catch (IOException e) {
            System.err.println("Error writing to " + outputFile + ": " + e.getMessage());
        }
    }
}
