
import java.util.*;

public class Deck {

    private final int deckId;
    private final Queue<Card> cards;

    public Deck(int deckId) {
        this.deckId = deckId;
        this.cards = new LinkedList<>();
    }
    
    public synchronized Card drawCard() {
        return cards.poll(); // returns null if empty
    }

    public synchronized void addCard(Card card) {
        cards.add(card);
        // no notify/wait used anymore
    }

    public synchronized List<Card> getContents() {
        return new ArrayList<>(cards);
    }


    public int getDeckId() {
        return deckId;
    }
}
