package cardgame;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Player implements Runnable {

    private final int playerId;
    private final List<Card> hand;
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
    public void addCard(Card card) {
        hand.add(card);
    }

    // Check if player has 4 of the same value
    private boolean hasWinningHand() {
        if (hand.size() != 4) {
            return false; // safety check
        }

        int firstValue = hand.get(0).getValue(); // get the integer value of the first card

        for (int i = 1; i < 4; i++) {
            if (hand.get(i).getValue() != firstValue) { // compare card values
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
            Card drawnCard = null;

            // try to get card without holding both locks for long
            synchronized (leftDeck) {
                drawnCard = leftDeck.drawCard();
            }

            if (drawnCard == null) {
                // no card available, wait a bit and retry
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {
                }
                continue;
            }

            // determine lock order for atomic draw+discard
            Deck firstLock = leftDeck.getDeckId() < rightDeck.getDeckId() ? leftDeck : rightDeck;
            Deck secondLock = leftDeck.getDeckId() < rightDeck.getDeckId() ? rightDeck : leftDeck;

            synchronized (firstLock) {
                synchronized (secondLock) {
                    hand.add(drawnCard);
                    Card discardCard = chooseCardToDiscard();
                    rightDeck.addCard(discardCard);

                    logAction("player " + playerId + " draws a " + drawnCard + " from deck " + leftDeck.getDeckId());
                    logAction("player " + playerId + " discards a " + discardCard + " to deck " + rightDeck.getDeckId());
                    logAction("player " + playerId + " current hand " + getHandSnapshot());
                }
            }

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
    private Card chooseCardToDiscard() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredValue) {
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
