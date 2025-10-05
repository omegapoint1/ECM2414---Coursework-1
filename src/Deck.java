
import java.util.*;

public class Deck {

    private final int deckId;
    private final Queue<Card> cards;

    public Deck(int deckId) {
        this.deckId = deckId;
        this.cards = new LinkedList<>();
    }
    public synchronized Card drawCard() {
        while (cards.isEmpty()) {
            try {
                wait(); // wait until a card is added
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return cards.poll();
    }

    public synchronized void addCard(Card card) {
        cards.add(card);
        notifyAll(); // wake up threads waiting for a card
    }


    public synchronized List<Card> getContents() {
        return new ArrayList<>(cards);
    }

    public int getDeckId() {
        return deckId;
    }
}
