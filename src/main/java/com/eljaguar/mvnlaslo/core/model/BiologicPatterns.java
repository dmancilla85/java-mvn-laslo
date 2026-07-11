package com.eljaguar.mvnlaslo.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Biological constants and pattern utilities.
 * Centralizes all biological knowledge — nucleotide complementarity, codon tables, etc.
 */
public final class BiologicPatterns {

    private BiologicPatterns() {
        // Utility class
    }

    /** Complement mapping for DNA/RNA nucleotides. */
    private static final Map<Character, Character> COMPLEMENT_MAP;

    static {
        Map<Character, Character> map = new HashMap<>();
        // DNA complements
        map.put('A', 'T');
        map.put('T', 'A');
        map.put('C', 'G');
        map.put('G', 'C');
        // RNA complement (U replaces T in RNA)
        map.put('U', 'A');
        // Lowercase
        map.put('a', 't');
        map.put('t', 'a');
        map.put('c', 'g');
        map.put('g', 'c');
        map.put('u', 'a');
        COMPLEMENT_MAP = Collections.unmodifiableMap(map);
    }

    /** Valid nucleotide characters. */
    private static final Set<Character> VALID_NUCLEOTIDES = Set.of(
            'A', 'T', 'C', 'G', 'U',
            'a', 't', 'c', 'g', 'u',
            'R', 'Y', 'S', 'W', 'K', 'M', 'B', 'D', 'H', 'V', 'N',
            'r', 'y', 's', 'w', 'k', 'm', 'b', 'd', 'h', 'v', 'n'
    );

    /** IUPAC ambiguity codes. */
    @SuppressWarnings("java:S6207")
    private static final Map<Character, String> IUPAC_AMBIGUITY = Map.ofEntries(
            Map.entry('R', "AG"),
            Map.entry('Y', "CT"),
            Map.entry('S', "GC"),
            Map.entry('W', "AT"),
            Map.entry('K', "GT"),
            Map.entry('M', "AC"),
            Map.entry('B', "CGT"),
            Map.entry('D', "AGT"),
            Map.entry('H', "ACT"),
            Map.entry('V', "ACG"),
            Map.entry('N', "ACGT")
    );

    /**
     * Returns the complement of a nucleotide character.
     * Returns '?' if the input is not a known nucleotide.
     */
    public static char complement(char nucleotide) {
        return COMPLEMENT_MAP.getOrDefault(nucleotide, '?');
    }

    /**
     * Returns the complement of a DNA/RNA sequence.
     */
    public static String complement(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(sequence.length());
        for (int i = 0; i < sequence.length(); i++) {
            sb.append(complement(sequence.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * Returns true if the character is a valid nucleotide (including IUPAC ambiguity codes).
     */
    public static boolean isValidNucleotide(char c) {
        return VALID_NUCLEOTIDES.contains(c);
    }

    /**
     * Returns true if the sequence contains only valid nucleotides.
     */
    public static boolean isValidSequence(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }
        for (int i = 0; i < sequence.length(); i++) {
            if (!isValidNucleotide(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the complementarity ratio between two sequences of equal length.
     * Returns a value between 0.0 (no matches) and 1.0 (perfect complementarity).
     */
    public static double complementarityRatio(String seq1, String seq2) {
        if (seq1 == null || seq2 == null || seq1.length() != seq2.length() || seq1.isEmpty()) {
            return 0.0;
        }
        String compSeq2 = complement(seq2);
        int matches = 0;
        for (int i = 0; i < seq1.length(); i++) {
            if (seq1.charAt(i) == compSeq2.charAt(i)) {
                matches++;
            }
        }
        return (double) matches / seq1.length();
    }

    /**
     * Converts a sequence to uppercase.
     */
    public static String toUpperCase(String sequence) {
        return sequence == null ? "" : sequence.toUpperCase();
    }

    /**
     * Converts a sequence to lowercase.
     */
    public static String toLowerCase(String sequence) {
        return sequence == null ? "" : sequence.toLowerCase();
    }

    /**
     * Replaces T with U (DNA to RNA conversion).
     */
    public static String dnaToRna(String sequence) {
        return sequence == null ? "" : sequence.replace('T', 'U').replace('t', 'u');
    }

    /**
     * Replaces U with T (RNA to DNA conversion).
     */
    public static String rnaToDna(String sequence) {
        return sequence == null ? "" : sequence.replace('U', 'T').replace('u', 't');
    }

    /**
     * Validates that a temperature is within a reasonable biological range.
     */
    public static boolean isValidTemperature(double tempCelsius) {
        return tempCelsius >= 0.0 && tempCelsius <= 100.0;
    }

    /**
     * Validates that a pH value is within a reasonable biological range.
     */
    public static boolean isValidPh(double ph) {
        return ph >= 0.0 && ph <= 14.0;
    }
}
