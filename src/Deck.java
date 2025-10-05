
import java.util.*;

public class Deck {

    private final int deckId;
    private final Queue<Integer> cards;

    public Deck(int deckId) {
        this.deckId = deckId;
        this.cards = new LinkedList<>();
    }
    public synchronized void addCard(int card) {
        // add card to the bottom of the queue
        cards.add(card);
    }

    public synchronized int drawCard() {
        // remove and return card from top of queue
        return cards.poll();
    }

    public synchronized List<Integer> getContents() {
        return new ArrayList<>(cards);
    }

    public int getDeckId() {
        return deckId;
    }

}
