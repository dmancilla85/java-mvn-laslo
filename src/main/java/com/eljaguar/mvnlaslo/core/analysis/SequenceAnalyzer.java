package com.eljaguar.mvnlaslo.core.analysis;

import com.eljaguar.mvnlaslo.core.matching.MatchPipeline;
import com.eljaguar.mvnlaslo.core.matching.MatchStrategy;
import com.eljaguar.mvnlaslo.core.model.MatchResult;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.util.List;
import java.util.Objects;

/**
 * Main entry point for sequence analysis operations.
 * Replaces the monolithic SequenceAnalizer class.
 * Uses Facade pattern to provide a simple API to the analysis subsystem.
 */
public final class SequenceAnalyzer {

    private final MatchPipeline pipeline;

    public SequenceAnalyzer(MatchPipeline pipeline) {
        this.pipeline = Objects.requireNonNull(pipeline, "pipeline cannot be null");
    }

    public SequenceAnalyzer(MatchStrategy.MatchConfig config) {
        this(MatchPipeline.createDefault(config));
    }

    public SequenceAnalyzer() {
        this(MatchStrategy.MatchConfig.defaults());
    }

    /**
     * Analyzes a query sequence against target sequences.
     *
     * @param query           the query sequence
     * @param targetSequences the target sequences to match against
     * @return the match result
     */
    public MatchResult analyze(SequenceInfo query, List<SequenceInfo> targetSequences) {
        return pipeline.execute(query, targetSequences);
    }

    /**
     * Analyzes multiple queries against target sequences.
     *
     * @param queries         the query sequences
     * @param targetSequences the target sequences to match against
     * @return list of match results
     */
    public List<MatchResult> analyzeBatch(List<SequenceInfo> queries, List<SequenceInfo> targetSequences) {
        return pipeline.executeBatch(queries, targetSequences);
    }

    /**
     * Analyzes a single sequence for internal hairpin structures.
     *
     * @param sequence the sequence to analyze
     * @return the match result
     */
    public MatchResult analyzeSelf(SequenceInfo sequence) {
        return pipeline.execute(sequence, List.of(sequence));
    }

    public MatchPipeline getPipeline() {
        return pipeline;
    }
}
