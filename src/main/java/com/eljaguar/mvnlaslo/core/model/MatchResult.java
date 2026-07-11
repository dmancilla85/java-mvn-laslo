package com.eljaguar.mvnlaslo.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable value object representing the result of a matching operation.
 * Contains a list of matched StemLoopModel results and metadata about the operation.
 */
public final class MatchResult {

    private final List<StemLoopModel> matches;
    private final int totalSequencesScanned;
    private final int totalMatchesFound;
    private final long executionTimeMs;
    private final String strategyName;
    private final boolean success;
    private final String errorMessage;

    private MatchResult(Builder builder) {
        this.matches = Collections.unmodifiableList(new ArrayList<>(builder.matches));
        this.totalSequencesScanned = builder.totalSequencesScanned;
        this.totalMatchesFound = builder.totalMatchesFound;
        this.executionTimeMs = builder.executionTimeMs;
        this.strategyName = builder.strategyName;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    public List<StemLoopModel> getMatches() {
        return matches;
    }

    public int getTotalSequencesScanned() {
        return totalSequencesScanned;
    }

    public int getTotalMatchesFound() {
        return totalMatchesFound;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasMatches() {
        return !matches.isEmpty();
    }

    /**
     * Creates a builder for MatchResult.
     */
    public static Builder builder(String strategyName) {
        return new Builder(strategyName);
    }

    /**
     * Creates a successful empty result.
     */
    public static MatchResult empty(String strategyName) {
        return builder(strategyName).build();
    }

    /**
     * Creates a failed result.
     */
    public static MatchResult failure(String strategyName, String errorMessage) {
        return builder(strategyName)
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "strategy='" + strategyName + '\'' +
                ", matches=" + totalMatchesFound +
                ", scanned=" + totalSequencesScanned +
                ", time=" + executionTimeMs + "ms" +
                ", success=" + success +
                '}';
    }

    /**
     * Builder for MatchResult.
     */
    public static final class Builder {
        private final String strategyName;
        private final List<StemLoopModel> matches = new ArrayList<>();
        private int totalSequencesScanned;
        private int totalMatchesFound;
        private long executionTimeMs;
        private boolean success = true;
        private String errorMessage = "";

        private Builder(String strategyName) {
            this.strategyName = Objects.requireNonNull(strategyName, "Strategy name cannot be null");
        }

        public Builder addMatch(StemLoopModel match) {
            this.matches.add(match);
            return this;
        }

        public Builder addAllMatches(List<StemLoopModel> matches) {
            this.matches.addAll(matches);
            return this;
        }

        public Builder totalSequencesScanned(int count) {
            this.totalSequencesScanned = count;
            return this;
        }

        public Builder totalMatchesFound(int count) {
            this.totalMatchesFound = count;
            return this;
        }

        public Builder executionTimeMs(long ms) {
            this.executionTimeMs = ms;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorMessage(String message) {
            this.errorMessage = message;
            return this;
        }

        public MatchResult build() {
            return new MatchResult(this);
        }
    }
}
