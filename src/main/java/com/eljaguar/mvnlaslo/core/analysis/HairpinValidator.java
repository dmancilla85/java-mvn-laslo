package com.eljaguar.mvnlaslo.core.analysis;

import com.eljaguar.mvnlaslo.core.model.BiologicPatterns;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validates whether a sequence segment forms a valid hairpin/loop structure.
 * Single Responsibility: hairpin structure validation only.
 */
public final class HairpinValidator {

    private final int minStemLength;
    private final int maxLoopLength;

    public HairpinValidator(int minStemLength, int maxLoopLength, double minComplementarity) {
        if (minStemLength <= 0) {
            throw new IllegalArgumentException("minStemLength must be positive");
        }
        if (maxLoopLength < 3) {
            throw new IllegalArgumentException("maxLoopLength must be at least 3");
        }
        this.minStemLength = minStemLength;
        this.maxLoopLength = maxLoopLength;
    }

    public HairpinValidator() {
        this(5, 20, 0.8);
    }

    /**
     * Validates whether the given stem and loop regions form a valid hairpin.
     *
     * @param sequence   the full sequence
     * @param stemStart  start of the stem region
     * @param stemEnd    end of the stem region (exclusive)
     * @param loopStart  start of the loop region
     * @param loopEnd    end of the loop region (exclusive)
     * @return true if this is a valid hairpin structure
     */
    public boolean isValidHairpin(String sequence, int stemStart, int stemEnd, int loopStart, int loopEnd) {
        if (!validatePositions(sequence, stemStart, stemEnd, loopStart, loopEnd)) {
            return false;
        }

        int stemLen = loopStart - stemStart;
        int loopLen = loopEnd - loopStart;

        if (stemLen < minStemLength) {
            return false;
        }
        if (loopLen > maxLoopLength || loopLen < 3) {
            return false;
        }

        String stemSeq = sequence.substring(stemStart, loopStart);
        String loopSeq = sequence.substring(loopStart, loopEnd);

        return BiologicPatterns.isValidSequence(stemSeq) && BiologicPatterns.isValidSequence(loopSeq);
    }

    /**
     * Finds all valid hairpin positions in a sequence.
     *
     * @param sequence the sequence to search
     * @return list of valid hairpin positions as int arrays [stemStart, stemEnd, loopStart, loopEnd]
     */
    public List<int[]> findValidHairpins(String sequence) {
        List<int[]> hairpins = new ArrayList<>();
        if (sequence == null || sequence.length() < minStemLength * 2 + 3) {
            return hairpins;
        }

        for (int i = 0; i <= sequence.length() - minStemLength * 2 - 3; i++) {
            for (int loopLen = 3; loopLen <= maxLoopLength; loopLen++) {
                int stem1End = i + minStemLength;
                int loopStart = stem1End;
                int loopEnd = loopStart + loopLen;
                int stem2Start = loopEnd;

                if (stem2Start + minStemLength > sequence.length()) {
                    break;
                }

                if (isValidHairpin(sequence, i, stem1End, loopStart, loopEnd)) {
                    hairpins.add(new int[]{i, stem1End, loopStart, loopEnd});
                }
            }
        }
        return hairpins;
    }

    private boolean validatePositions(String sequence, int stemStart, int stemEnd, int loopStart, int loopEnd) {
        if (sequence == null || sequence.isEmpty()) {
            return false;
        }
        if (stemStart < 0 || stemEnd <= stemStart || loopStart < stemEnd || loopEnd <= loopStart) {
            return false;
        }
        if (loopEnd > sequence.length()) {
            return false;
        }
        return true;
    }
}
