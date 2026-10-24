package com.eljaguar.mvnlaslo.core.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BiologicPatternsTest {

    @Test
    void testComplementDNA() {
        assertEquals('T', BiologicPatterns.complement('A'));
        assertEquals('A', BiologicPatterns.complement('T'));
        assertEquals('G', BiologicPatterns.complement('C'));
        assertEquals('C', BiologicPatterns.complement('G'));
    }

    @Test
    void testComplementRNA() {
        assertEquals('A', BiologicPatterns.complement('U'));
        assertEquals('T', BiologicPatterns.complement('A'));
    }

    @Test
    void testComplementString() {
        assertEquals("TACG", BiologicPatterns.complement("ATGC"));
        assertEquals("ACGT", BiologicPatterns.complement("TGCA"));
    }

    @Test
    void testComplementEmpty() {
        assertEquals("", BiologicPatterns.complement(""));
        assertEquals("", BiologicPatterns.complement(null));
    }

    @Test
    void testIsValidNucleotide() {
        assertTrue(BiologicPatterns.isValidNucleotide('A'));
        assertTrue(BiologicPatterns.isValidNucleotide('T'));
        assertTrue(BiologicPatterns.isValidNucleotide('C'));
        assertTrue(BiologicPatterns.isValidNucleotide('G'));
        assertTrue(BiologicPatterns.isValidNucleotide('U'));
        assertTrue(BiologicPatterns.isValidNucleotide('N'));
        assertFalse(BiologicPatterns.isValidNucleotide('X'));
        assertFalse(BiologicPatterns.isValidNucleotide('1'));
    }

    @Test
    void testIsValidSequence() {
        assertTrue(BiologicPatterns.isValidSequence("ATCG"));
        assertTrue(BiologicPatterns.isValidSequence("AUCG"));
        assertTrue(BiologicPatterns.isValidSequence("NNNN"));
        assertFalse(BiologicPatterns.isValidSequence("ATCGX"));
        assertFalse(BiologicPatterns.isValidSequence(""));
        assertFalse(BiologicPatterns.isValidSequence(null));
    }

    @Test
    void testComplementarityRatio() {
        // Perfect complementarity: ATCG vs TAGC (each position is a Watson-Crick pair)
        assertEquals(1.0, BiologicPatterns.complementarityRatio("ATCG", "TAGC"), 0.001);
        
        // No complementarity: AAAA vs AAAA (A-A is not a valid pair)
        assertEquals(0.0, BiologicPatterns.complementarityRatio("AAAA", "AAAA"), 0.001);
        
        // Partial complementarity: ATCG vs TACG (2 of 4 positions match)
        double ratio = BiologicPatterns.complementarityRatio("ATCG", "TACG");
        assertEquals(0.5, ratio, 0.001);
    }

    @Test
    void testComplementarityRatioInvalid() {
        assertEquals(0.0, BiologicPatterns.complementarityRatio(null, "ATCG"));
        assertEquals(0.0, BiologicPatterns.complementarityRatio("ATCG", null));
        assertEquals(0.0, BiologicPatterns.complementarityRatio("AT", "ATCG"));
        assertEquals(0.0, BiologicPatterns.complementarityRatio("", ""));
    }

    @Test
    void testDnaToRna() {
        assertEquals("AUCG", BiologicPatterns.dnaToRna("ATCG"));
        assertEquals("aucg", BiologicPatterns.dnaToRna("atcg"));
    }

    @Test
    void testRnaToDna() {
        assertEquals("ATCG", BiologicPatterns.rnaToDna("AUCG"));
        assertEquals("atcg", BiologicPatterns.rnaToDna("aucg"));
    }

    @Test
    void testToUpperCase() {
        assertEquals("ATCG", BiologicPatterns.toUpperCase("atcg"));
        assertEquals("ATCG", BiologicPatterns.toUpperCase("ATCG"));
    }

    @Test
    void testToLowerCase() {
        assertEquals("atcg", BiologicPatterns.toLowerCase("ATCG"));
        assertEquals("atcg", BiologicPatterns.toLowerCase("atcg"));
    }

    @Test
    void testIsValidTemperature() {
        assertTrue(BiologicPatterns.isValidTemperature(37.0));
        assertTrue(BiologicPatterns.isValidTemperature(0.0));
        assertTrue(BiologicPatterns.isValidTemperature(100.0));
        assertFalse(BiologicPatterns.isValidTemperature(-1.0));
        assertFalse(BiologicPatterns.isValidTemperature(101.0));
    }

    @Test
    void testIsValidPh() {
        assertTrue(BiologicPatterns.isValidPh(7.0));
        assertTrue(BiologicPatterns.isValidPh(0.0));
        assertTrue(BiologicPatterns.isValidPh(14.0));
        assertFalse(BiologicPatterns.isValidPh(-1.0));
        assertFalse(BiologicPatterns.isValidPh(15.0));
    }
}
