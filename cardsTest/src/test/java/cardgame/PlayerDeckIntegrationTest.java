package cardgame;

import cardgame.CardGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerDeckIntegrationTest {

    // Clean up leftover output files after each test
    private static final int NUM_PLAYERS = 2;
	@AfterEach
    void cleanUpFiles() throws IOException {
        for (int i = 1; i <= NUM_PLAYERS; i++) {
            Files.deleteIfExists(Path.of("player" + i + "_output.txt"));
            Files.deleteIfExists(Path.of("deck" + i + "_output.txt"));
        }
    }

    @Test
    void testDrawAndDiscard() throws Exception {
        // Create pack file
        Path packFile = Files.createTempFile("pack", ".txt");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        Files.write(packFile, lines);

        String input = NUM_PLAYERS + "\n" + packFile.toAbsolutePath() + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CardGame.main(new String[0]);

        assertTrue(CardGame.isGameWon(), "Game should have a winner");
        int winner = CardGame.getWinnerId();
        assertTrue(winner == 1 || winner == 2, "Winner must be player 1 or 2");
    }

    @Test
    void testTwoPlayerGameFlow() throws Exception {
        // Create pack file explicitly
        Path packFile = Files.createTempFile("pack", ".txt");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        lines.add("0"); lines.add("1"); lines.add("2"); lines.add("3");
        Files.write(packFile, lines);

        String input = NUM_PLAYERS + "\n" + packFile.toAbsolutePath() + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CardGame.main(new String[0]);

        assertTrue(CardGame.isGameWon(), "Game should have a winner");
        int winner = CardGame.getWinnerId();
        assertTrue(winner == 1 || winner == 2, "Winner must be player 1 or 2");
    }

    @Test
    void testWinnerRace() throws Exception {
        // Create pack file explicitly
        Path packFile = Files.createTempFile("pack", ".txt");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("1"); lines.add("1"); lines.add("1"); lines.add("1");
        lines.add("2"); lines.add("2"); lines.add("2"); lines.add("2");
        lines.add("1"); lines.add("1"); lines.add("1"); lines.add("1");
        lines.add("2"); lines.add("2"); lines.add("2"); lines.add("2");
        Files.write(packFile, lines);

        String input = NUM_PLAYERS + "\n" + packFile.toAbsolutePath() + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        CardGame.main(new String[0]);

        assertTrue(CardGame.isGameWon(), "Game should have a winner");
        int winner = CardGame.getWinnerId();
        assertTrue(winner == 1 || winner == 2, "Winner must be player 1 or 2");
    }
}
