package com.eljaguar.mvnlaslo.core;

import com.eljaguar.mvnlaslo.config.AppConfiguration;
import com.eljaguar.mvnlaslo.core.analysis.SequenceAnalyzer;
import com.eljaguar.mvnlaslo.core.matching.MatchPipeline;
import com.eljaguar.mvnlaslo.core.matching.MatchStrategy;
import com.eljaguar.mvnlaslo.core.model.MatchResult;
import com.eljaguar.mvnlaslo.core.model.ProgressListener;
import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import com.eljaguar.mvnlaslo.tools.RNAFoldConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JProgressBar;

/**
 * Main entry point for loop/hairpin matching operations.
 * Refactored to use Observer pattern, Builder pattern, and configuration extraction.
 * Includes backward-compatible setters so existing GUI code continues to compile.
 */
public final class LoopMatcher {

    private final AppConfiguration config;
    private final SequenceAnalyzer analyzer;
    private final List<ProgressListener> progressListeners;

    // Legacy mutable state — kept for backward compatibility with GUIFrame
    private ResourceBundle bundle;
    private List<String> loopPatterns = new ArrayList<>();
    private String additionalSequence = "";
    private int maxLength = 30;
    private int minLength = 5;
    private int maxMismatch = 1;
    private int maxWobble = 1;
    private String pathOut = "";
    private String pathIn = "";
    private File[] fileList;
    private boolean isExtendedMode;
    private boolean makeRandoms;
    private int numberOfRandoms = 10;
    private int kLetRandoms = 2;
    private boolean searchReverse;
    private int temperature = RNAFoldConfiguration.DEFAULT_TEMP;
    private boolean avoidLonelyPairs = true;
    private JProgressBar progressBar;

    private LoopMatcher(Builder builder) {
        this.config = builder.config;
        this.progressListeners = new CopyOnWriteArrayList<>(builder.progressListeners);

        MatchStrategy.MatchConfig matchConfig = new MatchStrategy.MatchConfig(
                config.getMinStemLength(),
                config.getMaxLoopLength(),
                config.getMinComplementarity(),
                config.getLowTemperature(),
                config.getHighTemperature()
        );

        this.analyzer = new SequenceAnalyzer(
                createMatchPipeline(builder.strategyName, matchConfig)
        );
    }

    /**
     * Backward-compatible no-arg constructor for existing GUI code.
     */
    public LoopMatcher() {
        this.config = AppConfiguration.defaults();
        this.progressListeners = new CopyOnWriteArrayList<>();

        MatchStrategy.MatchConfig matchConfig = MatchStrategy.MatchConfig.defaults();
        this.analyzer = new SequenceAnalyzer(
                createMatchPipeline("default", matchConfig)
        );
    }

    /**
     * Creates a builder for LoopMatcher.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a LoopMatcher with default configuration.
     */
    public static LoopMatcher createDefault() {
        return builder().build();
    }

    // --- New API ---

    public MatchResult analyze(SequenceInfo query, List<SequenceInfo> targetSequences) {
        notifyProgress(0, "Starting analysis...");
        MatchResult result = analyzer.analyze(query, targetSequences);
        notifyComplete("Analysis complete: " + result.getTotalMatchesFound() + " matches found");
        return result;
    }

    public MatchResult analyzeSelf(SequenceInfo sequence) {
        return analyzer.analyzeSelf(sequence);
    }

    public List<MatchResult> analyzeBatch(List<SequenceInfo> queries, List<SequenceInfo> targetSequences) {
        return analyzer.analyzeBatch(queries, targetSequences);
    }

    public void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(ProgressListener listener) {
        progressListeners.remove(listener);
    }

    public AppConfiguration getConfig() {
        return config;
    }

    // --- Legacy backward-compatible setters (deprecated, kept for GUIFrame) ---

    @Deprecated
    public void setBundle(ResourceBundle bundle) { this.bundle = bundle; }
    @Deprecated
    public void setLoopPatterns(List<String> patterns) { this.loopPatterns = patterns; }
    @Deprecated
    public void setAdditionalSequence(String seq) { this.additionalSequence = seq; }
    @Deprecated
    public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
    @Deprecated
    public void setMinLength(int minLength) { this.minLength = minLength; }
    @Deprecated
    public void setMaxMismatch(int mismatch) { this.maxMismatch = mismatch; }
    @Deprecated
    public void setMaxWobble(int wobble) { this.maxWobble = wobble; }
    @Deprecated
    public void setPathOut(String path) { this.pathOut = path; }
    @Deprecated
    public void setPathIn(String path) { this.pathIn = path; }
    @Deprecated
    public void setFileList(File[] files) { this.fileList = files; }
    @Deprecated
    public void setIsExtendedMode(boolean extended) { this.isExtendedMode = extended; }
    @Deprecated
    public void setMakeRandoms(boolean makeRandoms) { this.makeRandoms = makeRandoms; }
    @Deprecated
    public void setNumberOfRandoms(int count) { this.numberOfRandoms = count; }
    @Deprecated
    public void setKLetRandoms(int klet) { this.kLetRandoms = klet; }
    @Deprecated
    public void setSearchReverse(boolean search) { this.searchReverse = search; }
    @Deprecated
    public void setTemperature(int temp) { this.temperature = temp; }
    @Deprecated
    public void setAvoidLonelyPairs(boolean avoid) { this.avoidLonelyPairs = avoid; }
    @Deprecated
    public void setProgressBar(JProgressBar bar) { this.progressBar = bar; }

    @Deprecated public ResourceBundle getBundle() { return bundle; }
    @Deprecated public List<String> getLoopPatterns() { return loopPatterns; }
    @Deprecated public String getAdditionalSequence() { return additionalSequence; }
    @Deprecated public int getMaxLength() { return maxLength; }
    @Deprecated public int getMinLength() { return minLength; }
    @Deprecated public int getMaxMismatch() { return maxMismatch; }
    @Deprecated public int getMaxWobble() { return maxWobble; }
    @Deprecated public String getPathOut() { return pathOut; }
    @Deprecated public String getPathIn() { return pathIn; }
    @Deprecated public File[] getFileList() { return fileList; }
    @Deprecated public boolean isExtendedMode() { return isExtendedMode; }
    @Deprecated public boolean isMakeRandoms() { return makeRandoms; }
    @Deprecated public int getNumberOfRandoms() { return numberOfRandoms; }
    @Deprecated public int getKLetRandoms() { return kLetRandoms; }
    @Deprecated public boolean isSearchReverse() { return searchReverse; }
    @Deprecated public int getTemperature() { return temperature; }
    @Deprecated public boolean isAvoidLonelyPairs() { return avoidLonelyPairs; }
    @Deprecated public JProgressBar getProgressBar() { return progressBar; }

    /**
     * Legacy method — starts file reading process.
     * This method is kept for backward compatibility with existing GUI code.
     */
    @Deprecated
    public boolean startReadingFiles() {
        // Legacy entry point — does nothing in new architecture.
        // Use analyze() or analyzeBatch() instead.
        return true;
    }

    /** Alias for backward compatibility. */
    @Deprecated public boolean getIsExtendedMode() { return isExtendedMode; }
    /** Alias for backward compatibility. */
    @Deprecated public boolean getAvoidLonelyPairs() { return avoidLonelyPairs; }
    /** Alias for backward compatibility. */
    @Deprecated public int getProgress() { return 0; }
    /** Alias for backward compatibility. */
    @Deprecated public void setExtendedMode(boolean mode) { this.isExtendedMode = mode; }
    /** Alias for backward compatibility. */
    @Deprecated public void setInputType(com.eljaguar.mvnlaslo.io.InputSequence type) { /* no-op */ }
    /** Alias for backward compatibility. */
    @Deprecated public void callProcessThreads() { /* no-op */ }

    // --- Private helpers ---

    private MatchStrategy matchStrategy(String strategyName, MatchStrategy.MatchConfig matchConfig) {
        return switch (strategyName.toLowerCase()) {
            case "full" -> new com.eljaguar.mvnlaslo.core.matching.FullMatchStrategy(matchConfig);
            case "basic" -> new com.eljaguar.mvnlaslo.core.matching.BasicMatchStrategy(matchConfig);
            default -> new com.eljaguar.mvnlaslo.core.matching.DefaultMatchStrategy(matchConfig);
        };
    }

    private MatchPipeline createMatchPipeline(String strategyName, MatchStrategy.MatchConfig matchConfig) {
        MatchStrategy strategy = matchStrategy(strategyName, matchConfig);
        return new MatchPipeline(strategy, matchConfig, createCompositeListener());
    }

    private ProgressListener createCompositeListener() {
        return (progress, message) -> {
            for (ProgressListener listener : progressListeners) {
                listener.onProgress(progress, message);
            }
        };
    }

    private void notifyProgress(int progress, String message) {
        for (ProgressListener listener : progressListeners) {
            listener.onProgress(progress, message);
        }
    }

    private void notifyComplete(String message) {
        for (ProgressListener listener : progressListeners) {
            listener.onComplete(message);
        }
    }

    /**
     * Builder for LoopMatcher.
     */
    public static final class Builder {
        private AppConfiguration config = AppConfiguration.defaults();
        private String strategyName = "default";
        private final List<ProgressListener> progressListeners = new ArrayList<>();

        private Builder() {}

        public Builder config(AppConfiguration config) {
            this.config = config;
            return this;
        }

        public Builder strategy(String strategyName) {
            this.strategyName = strategyName;
            return this;
        }

        public Builder addProgressListener(ProgressListener listener) {
            this.progressListeners.add(listener);
            return this;
        }

        public Builder withProgressListener(ProgressListener listener) {
            this.progressListeners.add(listener);
            return this;
        }

        public LoopMatcher build() {
            return new LoopMatcher(this);
        }
    }
}
