package com.eljaguar.mvnlaslo.core.analysis;

import com.eljaguar.mvnlaslo.core.model.BiologicPatterns;

/**
 * Checks complementarity between nucleotide sequences.
 * Single Responsibility: complementarity analysis only.
 */
public final class ComplementarityChecker {

    private final double minComplementarity;

    public ComplementarityChecker(double minComplementarity) {
        this.minComplementarity = minComplementarity;
    }

    public ComplementarityChecker() {
        this(0.8);
    }

    /**
     * Calculates the complementarity ratio between two sequences.
     *
     * @param seq1 first sequence
     * @param seq2 second sequence (will be complemented for comparison)
     * @return ratio between 0.0 (no matches) and 1.0 (perfect complementarity)
     */
    public double calculateRatio(String seq1, String seq2) {
        if (seq1 == null || seq2 == null || seq1.length() != seq2.length() || seq1.isEmpty()) {
            return 0.0;
        }
        return BiologicPatterns.complementarityRatio(seq1, seq2);
    }

    /**
     * Checks if two sequences meet the minimum complementarity threshold.
     *
     * @param seq1 first sequence
     * @param seq2 second sequence
     * @return true if complementarity ratio >= minComplementarity
     */
    public boolean isComplementary(String seq1, String seq2) {
        return calculateRatio(seq1, seq2) >= minComplementarity;
    }

    /**
     * Finds the best complementary region in a target sequence for a given query.
     *
     * @param query        the query sequence
     * @param target       the target sequence
     * @param queryStart   start position in query
     * @param queryEnd     end position in query (exclusive)
     * @param targetStart  start position in target
     * @param targetEnd    end position in target (exclusive)
     * @return the complementarity ratio for the region, or 0.0 if invalid
     */
    public double calculateRegionRatio(String query, String target,
                                       int queryStart, int queryEnd,
                                       int targetStart, int targetEnd) {
        if (query == null || target == null) {
            return 0.0;
        }
        if (queryStart < 0 || queryEnd > query.length() ||
            targetStart < 0 || targetEnd > target.length()) {
            return 0.0;
        }

        String queryRegion = query.substring(queryStart, queryEnd);
        String targetRegion = target.substring(targetStart, targetEnd);

        if (queryRegion.length() != targetRegion.length()) {
            return 0.0;
        }

        return calculateRatio(queryRegion, targetRegion);
    }

    public double getMinComplementarity() {
        return minComplementarity;
    }
}
