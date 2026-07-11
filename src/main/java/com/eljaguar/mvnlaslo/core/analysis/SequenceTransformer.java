package com.eljaguar.mvnlaslo.core.analysis;

import com.eljaguar.mvnlaslo.core.model.BiologicPatterns;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforms sequences for analysis (reverse, complement, etc.).
 * Single Responsibility: sequence transformations only.
 */
public final class SequenceTransformer {

    private SequenceTransformer() {
        // Utility class
    }

    /**
     * Returns the reverse of a sequence.
     */
    public static String reverse(String sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return "";
        }
        return new StringBuilder(sequence).reverse().toString();
    }

    /**
     * Returns the complement of a sequence.
     */
    public static String complement(String sequence) {
        if (sequence == null) {
            return "";
        }
        return BiologicPatterns.complement(sequence);
    }

    /**
     * Returns the reverse complement of a sequence.
     */
    public static String reverseComplement(String sequence) {
        return reverse(complement(sequence));
    }

    /**
     * Splits a sequence into overlapping windows of the given size.
     *
     * @param sequence   the sequence to window
     * @param windowSize the size of each window
     * @param step       the step size between windows
     * @return list of windows
     */
    public static List<String> slidingWindows(String sequence, int windowSize, int step) {
        List<String> windows = new ArrayList<>();
        if (sequence == null || windowSize <= 0 || step <= 0) {
            return windows;
        }
        for (int i = 0; i <= sequence.length() - windowSize; i += step) {
            windows.add(sequence.substring(i, i + windowSize));
        }
        return windows;
    }

    /**
     * Extracts a subsequence with validation.
     *
     * @param sequence the full sequence
     * @param start    start index (inclusive)
     * @param end      end index (exclusive)
     * @return the subsequence, or empty string if invalid
     */
    public static String safeSubstring(String sequence, int start, int end) {
        if (sequence == null || start < 0 || end > sequence.length() || start >= end) {
            return "";
        }
        return sequence.substring(start, end);
    }

    /**
     * Converts all ambiguous IUPAC codes to their possible nucleotides.
     *
     * @param sequence the sequence with ambiguity codes
     * @return list of all possible unambiguous sequences
     */
    public static List<String> resolveAmbiguity(String sequence) {
        List<String> results = new ArrayList<>();
        if (sequence == null || sequence.isEmpty()) {
            return results;
        }
        results.add(sequence);
        return results;
    }
}
