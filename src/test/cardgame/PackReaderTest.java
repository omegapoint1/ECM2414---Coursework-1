package cardgame;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PackReaderTest {

    @Test
    void testValidPack() throws Exception {
        File f = File.createTempFile("pack", ".txt");
        try (FileWriter fw = new FileWriter(f)) {
            for (int i = 0; i < 8; i++) fw.write("0\n");
        }
        List<Card> pack = PackReader.readPack(f.getAbsolutePath(), 1);
        assertEquals(8, pack.size());
        assertEquals(0, pack.get(0).getValue());
        f.delete();
    }

    @Test
    void testInvalidNumberOfCards() throws Exception {
        File f = File.createTempFile("pack", ".txt");
        try (FileWriter fw = new FileWriter(f)) { fw.write("0\n"); } 
        assertThrows(IllegalArgumentException.class, () -> PackReader.readPack(f.getAbsolutePath(), 1));
        f.delete();
    }

    @Test
    void testNegativeCard() throws Exception {
        File f = File.createTempFile("pack", ".txt");
        try (FileWriter fw = new FileWriter(f)) { fw.write("-1\n0\n0\n0\n0\n0\n0\n0\n"); } 
        assertThrows(IllegalArgumentException.class, () -> PackReader.readPack(f.getAbsolutePath(), 1));
        f.delete();
    }

    @Test
    void testNonInteger() throws Exception {
        File f = File.createTempFile("pack", ".txt");
        try (FileWriter fw = new FileWriter(f)) { fw.write("x\n0\n0\n0\n0\n0\n0\n0\n"); }
        assertThrows(IllegalArgumentException.class, () -> PackReader.readPack(f.getAbsolutePath(), 1));
        f.delete();
    }
}
