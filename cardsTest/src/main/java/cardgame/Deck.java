package cardgame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a single deck of cards.
 * Thread-safe for concurrent access by multiple players.
 */
public class Deck {

    // Identifier for this deck (1..n)
    private final int deckId;

    // Internal storage for cards, treated as a FIFO queue
    private final Deque<Card> cards = new ArrayDeque<>();

    // Lock for thread-safe access
    private final ReentrantLock lock = new ReentrantLock();

    // Condition to wait on when deck is empty
    private final Condition notEmpty = lock.newCondition();

    /**
     * Creates a deck with the given ID.
     *
     * @param deckId unique identifier of this deck
     */
    public Deck(int deckId) {
        this.deckId = deckId;
    }

    /**
     * Returns the deck's ID.
     *
     * @return deck ID
     */
    public int getDeckId() {
        return deckId;
    }

    /**
     * Waits until there is a card in the deck or the game has ended.
     *
     * @return true if the deck is non-empty, false if game ended
     * @throws InterruptedException if thread is interrupted while waiting
     */
    public boolean awaitCardOrGameEnd() throws InterruptedException {
        lock.lock();
        try {
            while (cards.isEmpty() && !CardGame.isGameWon()) {
                notEmpty.await();
            }
            return !cards.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds a card to the bottom of the deck and signals waiting threads.
     *
     * @param card Card to add
     */
    public void addCard(Card card) {
        lock.lock();
        try {
            cards.addLast(card);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes and returns the first card from the deck.
     * Caller does not need to hold the lock if using this method alone.
     *
     * @return the first Card, or null if deck is empty
     */
    public Card pollFirst() {
        lock.lock();
        try {
            return cards.pollFirst();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a snapshot of the current deck contents.
     *
     * @return list of cards currently in the deck
     */
    public List<Card> getContents() {
        lock.lock();
        try {
            return new ArrayList<>(cards);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Locks the deck for atomic operations (e.g., draw + discard).
     */
    public void lock() {
        lock.lock();
    }

    /**
     * Unlocks the deck after atomic operations.
     */
    public void unlock() {
        lock.unlock();
    }

    /**
     * Signals all threads waiting on this deck (e.g., game ended).
     */
    public void signalAllWaiting() {
        lock.lock();
        try {
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
