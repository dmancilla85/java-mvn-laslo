package com.eljaguar.mvnlaslo.core.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HairpinValidatorTest {

    @Test
    void testValidHairpin() {
        HairpinValidator validator = new HairpinValidator();
        
        // Simple hairpin: ATCGA (stem1, 5 chars) + AAAA (loop) + ATCGA (stem2, 5 chars)
        // Default minStemLength=5, so stem must be >= 5
        String sequence = "ATCGAAAAAATCGA";
        assertTrue(validator.isValidHairpin(sequence, 0, 5, 5, 9));
    }

    @Test
    void testInvalidHairpinTooShortStem() {
        HairpinValidator validator = new HairpinValidator(5, 20, 0.8);
        
        // Stem too short
        String sequence = "ATCAAAATCG";
        assertFalse(validator.isValidHairpin(sequence, 0, 3, 3, 7));
    }

    @Test
    void testInvalidHairpinLoopTooLong() {
        HairpinValidator validator = new HairpinValidator(5, 20, 0.8);
        
        // Loop too long
        String sequence = "ATCGAAAAAAAAAAAAAAAAAATCG";
        assertFalse(validator.isValidHairpin(sequence, 0, 4, 4, 24));
    }

    @Test
    void testInvalidPositions() {
        HairpinValidator validator = new HairpinValidator();
        
        String sequence = "ATCGATCG";
        assertFalse(validator.isValidHairpin(sequence, -1, 2, 2, 4));
        assertFalse(validator.isValidHairpin(sequence, 0, 0, 2, 4));
        assertFalse(validator.isValidHairpin(sequence, 0, 2, 1, 4));
        assertFalse(validator.isValidHairpin(sequence, 0, 2, 2, 2));
        assertFalse(validator.isValidHairpin(sequence, 0, 2, 2, 10));
    }

    @Test
    void testNullSequence() {
        HairpinValidator validator = new HairpinValidator();
        assertFalse(validator.isValidHairpin(null, 0, 2, 2, 4));
    }

    @Test
    void testFindValidHairpins() {
        HairpinValidator validator = new HairpinValidator(3, 10, 0.8);
        
        // Sequence with a hairpin: ATCG + AAA + ATCG
        String sequence = "NNNNATCGAAAATCGNNNN";
        var hairpins = validator.findValidHairpins(sequence);
        
        assertFalse(hairpins.isEmpty());
    }
}
