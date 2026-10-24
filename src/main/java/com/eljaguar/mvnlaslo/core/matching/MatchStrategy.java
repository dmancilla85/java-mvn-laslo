package com.eljaguar.mvnlaslo.core.matching;

import com.eljaguar.mvnlaslo.core.model.MatchResult;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;

/**
 * Strategy pattern interface for different hairpin/loop matching algorithms.
 * Each implementation represents a different matching strategy (full, basic, etc.).
 */
public interface MatchStrategy {

    /**
     * Returns the name of this matching strategy.
     */
    String getName();

    /**
     * Performs the match between a query sequence and target sequences.
     *
     * @param query           the input query sequence
     * @param targetSequences the target sequences to match against
     * @param config          configuration parameters
     * @return a MatchResult containing matched stems/loops
     */
    MatchResult match(SequenceInfo query, Iterable<SequenceInfo> targetSequences, MatchConfig config);

    /**
     * Configuration for a match operation.
     */
    record MatchConfig(
            int minStemLength,
            int maxLoopLength,
            double minComplementarity,
            double temperatureLow,
            double temperatureHigh
    ) {
        public static MatchConfig defaults() {
            return new MatchConfig(5, 20, 0.8, 37.0, 70.0);
        }
    }
}
