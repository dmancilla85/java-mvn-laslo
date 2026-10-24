package com.eljaguar.mvnlaslo.core.model;

import java.util.Objects;

/**
 * Pure domain model for a stem-loop (hairpin) structure.
 * Replaces the mutable static-state StemLoop class.
 * This is an immutable value object — no side effects, no static state.
 */
public final class StemLoopModel {

    private final String id;
    private final String sequence;
    private final String reversed;
    private final String complement;
    private final int stemStart;
    private final int stemEnd;
    private final int loopStart;
    private final int loopEnd;
    private final int loopLength;
    private final int stemLength;
    private final double complementarityRatio;
    private final String species;
    private final String geneName;
    private final String transcriptId;
    private final String header;
    private final MatchType matchType;

    private StemLoopModel(Builder builder) {
        this.id = builder.id;
        this.sequence = builder.sequence;
        this.reversed = builder.reversed;
        this.complement = builder.complement;
        this.stemStart = builder.stemStart;
        this.stemEnd = builder.stemEnd;
        this.loopStart = builder.loopStart;
        this.loopEnd = builder.loopEnd;
        this.loopLength = builder.loopLength;
        this.stemLength = builder.stemLength;
        this.complementarityRatio = builder.complementarityRatio;
        this.species = builder.species;
        this.geneName = builder.geneName;
        this.transcriptId = builder.transcriptId;
        this.header = builder.header;
        this.matchType = builder.matchType;
    }

    // --- Getters ---

    public String getId() { return id; }
    public String getSequence() { return sequence; }
    public String getReversed() { return reversed; }
    public String getComplement() { return complement; }
    public int getStemStart() { return stemStart; }
    public int getStemEnd() { return stemEnd; }
    public int getLoopStart() { return loopStart; }
    public int getLoopEnd() { return loopEnd; }
    public int getLoopLength() { return loopLength; }
    public int getStemLength() { return stemLength; }
    public double getComplementarityRatio() { return complementarityRatio; }
    public String getSpecies() { return species; }
    public String getGeneName() { return geneName; }
    public String getTranscriptId() { return transcriptId; }
    public String getHeader() { return header; }
    public MatchType getMatchType() { return matchType; }

    public String getFullSequence() {
        return sequence.substring(stemStart, loopEnd);
    }

    public String getStemSequence() {
        return sequence.substring(stemStart, loopStart);
    }

    public String getLoopSequence() {
        return sequence.substring(loopStart, loopEnd);
    }

    /**
     * Types of matches found.
     */
    public enum MatchType {
        FULL_MATCH,
        PARTIAL_MATCH,
        BASIC_MATCH,
        HAIRPIN_ONLY
    }

    // --- Builder ---

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id = "";
        private String sequence = "";
        private String reversed = "";
        private String complement = "";
        private int stemStart;
        private int stemEnd;
        private int loopStart;
        private int loopEnd;
        private int loopLength;
        private int stemLength;
        private double complementarityRatio;
        private String species = "";
        private String geneName = "";
        private String transcriptId = "";
        private String header = "";
        private MatchType matchType = MatchType.FULL_MATCH;

        private Builder() {}

        public Builder id(String id) { this.id = id; return this; }
        public Builder sequence(String sequence) { this.sequence = sequence; return this; }
        public Builder reversed(String reversed) { this.reversed = reversed; return this; }
        public Builder complement(String complement) { this.complement = complement; return this; }
        public Builder stemStart(int stemStart) { this.stemStart = stemStart; return this; }
        public Builder stemEnd(int stemEnd) { this.stemEnd = stemEnd; return this; }
        public Builder loopStart(int loopStart) { this.loopStart = loopStart; return this; }
        public Builder loopEnd(int loopEnd) { this.loopEnd = loopEnd; return this; }
        public Builder loopLength(int loopLength) { this.loopLength = loopLength; return this; }
        public Builder stemLength(int stemLength) { this.stemLength = stemLength; return this; }
        public Builder complementarityRatio(double ratio) { this.complementarityRatio = ratio; return this; }
        public Builder species(String species) { this.species = species; return this; }
        public Builder geneName(String geneName) { this.geneName = geneName; return this; }
        public Builder transcriptId(String transcriptId) { this.transcriptId = transcriptId; return this; }
        public Builder header(String header) { this.header = header; return this; }
        public Builder matchType(MatchType matchType) { this.matchType = matchType; return this; }

        public StemLoopModel build() {
            return new StemLoopModel(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StemLoopModel that = (StemLoopModel) o;
        return stemStart == that.stemStart &&
                stemEnd == that.stemEnd &&
                loopStart == that.loopStart &&
                loopEnd == that.loopEnd &&
                Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence, stemStart, stemEnd, loopStart, loopEnd);
    }

    @Override
    public String toString() {
        return "StemLoopModel{" +
                "id='" + id + '\'' +
                ", stem=" + stemStart + "-" + stemEnd +
                ", loop=" + loopStart + "-" + loopEnd +
                ", complementarity=" + String.format("%.2f", complementarityRatio) +
                ", type=" + matchType +
                '}';
    }
}
