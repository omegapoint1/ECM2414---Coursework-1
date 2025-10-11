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

        // immediate win check
        if (hasWinningHand() && CardGame.tryDeclareWin(playerId)) {
            System.out.println("player " + playerId + " wins");
            logAction("player " + playerId + " wins");
        }

        while (!CardGame.isGameWon()) {
            Card drawnCard = null;
            Card discardCard;

            // determine lock order to avoid deadlocks
            Deck firstLock = leftDeck.getDeckId() < rightDeck.getDeckId() ? leftDeck : rightDeck;
            Deck secondLock = leftDeck.getDeckId() < rightDeck.getDeckId() ? rightDeck : leftDeck;

            synchronized (firstLock) {
                synchronized (secondLock) {
                    if (CardGame.isGameWon()) {
                        break;
                    }

                    drawnCard = leftDeck.drawCard();
                    if (drawnCard == null) {
                        Thread.yield(); // give other threads a chance to act
                        continue; // deck empty, release locks and retry
                    }
                    hand.add(drawnCard);
                    discardCard = chooseCardToDiscard();
                    rightDeck.addCard(discardCard);

                    logAction("player " + playerId + " draws a " + drawnCard + " from deck " + leftDeck.getDeckId());
                    logAction("player " + playerId + " discards a " + discardCard + " to deck " + rightDeck.getDeckId());
                    logAction("player " + playerId + " current hand is " + getHandSnapshot());
                }
            }

            // check win after atomic action
            if (hasWinningHand() && CardGame.tryDeclareWin(playerId)) {
                System.out.println("player " + playerId + " wins");
                logAction("player " + playerId + " wins");
                break;
            }

            Thread.yield(); // allow other threads to run
        }

        // write loser notification if needed
        int winner = CardGame.getWinnerId();
        if (winner != -1 && winner != playerId) {
            logAction("player " + winner + " has informed player " + playerId + " that player " + winner + " has won");
        }

        logAction("player " + playerId + " exits");
        if (winner == playerId) {
            logAction("player " + playerId + " final hand: " + getHandSnapshot());
        } else {
            logAction("player " + playerId + " hand: " + getHandSnapshot());
        }
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
