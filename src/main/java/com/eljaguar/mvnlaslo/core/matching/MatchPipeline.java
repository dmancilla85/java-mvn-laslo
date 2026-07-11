package com.eljaguar.mvnlaslo.core.matching;

import com.eljaguar.mvnlaslo.core.model.MatchResult;
import com.eljaguar.mvnlaslo.core.model.ProgressListener;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the matching process using a configurable strategy.
 * Implements the Facade pattern — provides a simple interface to the complex matching subsystem.
 */
public final class MatchPipeline {

    private final MatchStrategy strategy;
    private final MatchStrategy.MatchConfig config;
    private final ProgressListener progressListener;

    public MatchPipeline(MatchStrategy strategy, MatchStrategy.MatchConfig config, ProgressListener progressListener) {
        this.strategy = strategy;
        this.config = config;
        this.progressListener = progressListener;
    }

    public MatchPipeline(MatchStrategy strategy, MatchStrategy.MatchConfig config) {
        this(strategy, config, ProgressListener.none());
    }

    public MatchPipeline(MatchStrategy strategy) {
        this(strategy, MatchStrategy.MatchConfig.defaults());
    }

    /**
     * Executes the matching pipeline.
     *
     * @param query           the query sequence
     * @param targetSequences the target sequences to match against
     * @return the match result
     */
    public MatchResult execute(SequenceInfo query, List<SequenceInfo> targetSequences) {
        progressListener.onProgress(0, "Starting matching with strategy: " + strategy.getName());

        MatchResult result = strategy.match(query, targetSequences, config);

        if (result.isSuccess()) {
            progressListener.onComplete("Matching complete: " + result.getTotalMatchesFound() + " matches found");
        } else {
            progressListener.onError(result.getErrorMessage());
        }

        return result;
    }

    /**
     * Executes the matching pipeline with multiple queries.
     *
     * @param queries         the query sequences
     * @param targetSequences the target sequences to match against
     * @return list of match results, one per query
     */
    public List<MatchResult> executeBatch(List<SequenceInfo> queries, List<SequenceInfo> targetSequences) {
        List<MatchResult> results = new ArrayList<>();
        int total = queries.size();

        for (int i = 0; i < total; i++) {
            int progress = (int) ((i * 100.0) / total);
            progressListener.onProgress(progress, "Processing query " + (i + 1) + " of " + total);

            MatchResult result = execute(queries.get(i), targetSequences);
            results.add(result);
        }

        progressListener.onComplete("Batch processing complete: " + total + " queries processed");
        return results;
    }

    public MatchStrategy getStrategy() {
        return strategy;
    }

    public MatchStrategy.MatchConfig getConfig() {
        return config;
    }

    /**
     * Factory method to create a pipeline with the default strategy.
     */
    public static MatchPipeline createDefault(MatchStrategy.MatchConfig config) {
        return new MatchPipeline(new DefaultMatchStrategy(config), config);
    }

    /**
     * Factory method to create a pipeline with the full match strategy.
     */
    public static MatchPipeline createFull(MatchStrategy.MatchConfig config) {
        return new MatchPipeline(new FullMatchStrategy(config), config);
    }

    /**
     * Factory method to create a pipeline with the basic match strategy.
     */
    public static MatchPipeline createBasic(MatchStrategy.MatchConfig config) {
        return new MatchPipeline(new BasicMatchStrategy(config), config);
    }
}
