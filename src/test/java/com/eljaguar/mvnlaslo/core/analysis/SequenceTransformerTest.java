package com.eljaguar.mvnlaslo.core.analysis;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SequenceTransformerTest {

    @Test
    void testReverse() {
        assertEquals("DCBA", SequenceTransformer.reverse("ABCD"));
        assertEquals("", SequenceTransformer.reverse(""));
        assertEquals("", SequenceTransformer.reverse(null));
    }

    @Test
    void testComplement() {
        assertEquals("TACG", SequenceTransformer.complement("ATGC"));
        assertEquals("ACGT", SequenceTransformer.complement("TGCA"));
    }

    @Test
    void testReverseComplement() {
        // ATGC -> complement: TACG -> reverse: GCAT
        assertEquals("GCAT", SequenceTransformer.reverseComplement("ATGC"));
    }

    @Test
    void testSlidingWindows() {
        String sequence = "ABCDEFGHIJ";
        List<String> windows = SequenceTransformer.slidingWindows(sequence, 3, 2);
        
        assertEquals(4, windows.size());
        assertEquals("ABC", windows.get(0));
        assertEquals("CDE", windows.get(1));
        assertEquals("EFG", windows.get(2));
        assertEquals("GHI", windows.get(3));
    }

    @Test
    void testSlidingWindowsInvalid() {
        List<String> windows = SequenceTransformer.slidingWindows(null, 3, 2);
        assertTrue(windows.isEmpty());
        
        windows = SequenceTransformer.slidingWindows("ABC", 0, 2);
        assertTrue(windows.isEmpty());
        
        windows = SequenceTransformer.slidingWindows("ABC", 3, 0);
        assertTrue(windows.isEmpty());
    }

    @Test
    void testSafeSubstring() {
        String sequence = "ABCDEFGHIJ";
        
        assertEquals("ABCDE", SequenceTransformer.safeSubstring(sequence, 0, 5));
        assertEquals("FGH", SequenceTransformer.safeSubstring(sequence, 5, 8));
        assertEquals("", SequenceTransformer.safeSubstring(sequence, -1, 5));
        assertEquals("", SequenceTransformer.safeSubstring(sequence, 0, 15));
        assertEquals("", SequenceTransformer.safeSubstring(sequence, 5, 3));
        assertEquals("", SequenceTransformer.safeSubstring(null, 0, 5));
    }
}
