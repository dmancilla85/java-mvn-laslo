package com.eljaguar.mvnlaslo.core.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplementarityCheckerTest {

    @Test
    void testPerfectComplementarity() {
        ComplementarityChecker checker = new ComplementarityChecker();
        
        // ATCG complementary to TAGC: each position is a Watson-Crick pair
        assertEquals(1.0, checker.calculateRatio("ATCG", "TAGC"), 0.001);
    }

    @Test
    void testNoComplementarity() {
        ComplementarityChecker checker = new ComplementarityChecker();
        
        // AAAA vs AAAA: A-A is not complementary
        assertEquals(0.0, checker.calculateRatio("AAAA", "AAAA"), 0.001);
    }

    @Test
    void testPartialComplementarity() {
        ComplementarityChecker checker = new ComplementarityChecker();
        
        // ATCG vs TACG: complement("TACG")="ATGC", matches at positions 0,1 → 2/4 = 0.5
        double ratio = checker.calculateRatio("ATCG", "TACG");
        assertEquals(0.5, ratio, 0.001);
    }

    @Test
    void testIsComplementary() {
        ComplementarityChecker checker = new ComplementarityChecker(0.8);
        
        assertTrue(checker.isComplementary("ATCG", "TAGC"));
        assertFalse(checker.isComplementary("AAAA", "AAAA"));
    }

    @Test
    void testInvalidInput() {
        ComplementarityChecker checker = new ComplementarityChecker();
        
        assertEquals(0.0, checker.calculateRatio(null, "ATCG"));
        assertEquals(0.0, checker.calculateRatio("ATCG", null));
        assertEquals(0.0, checker.calculateRatio("AT", "ATCG"));
        assertEquals(0.0, checker.calculateRatio("", ""));
    }

    @Test
    void testCalculateRegionRatio() {
        ComplementarityChecker checker = new ComplementarityChecker();
        
        // ATCG vs TAGC in region = perfect complementarity
        assertEquals(1.0, checker.calculateRegionRatio("ATCGATCG", "TAGCTAGC", 0, 4, 0, 4), 0.001);
        
        // Invalid positions
        assertEquals(0.0, checker.calculateRegionRatio("ATCG", "ATCG", -1, 2, 0, 2));
        assertEquals(0.0, checker.calculateRegionRatio("ATCG", "ATCG", 0, 5, 0, 2));
    }
}
