package com.eljaguar.mvnlaslo.core.model;

import java.util.Objects;

/**
 * Immutable value object representing a parsed biological sequence.
 * Contains the sequence data and associated metadata (header, species, etc.).
 */
public final class SequenceInfo {

    private final String header;
    private final String sequence;
    private final String species;
    private final String geneName;
    private final String transcriptId;
    private final SequenceType type;

    private SequenceInfo(Builder builder) {
        this.header = builder.header;
        this.sequence = builder.sequence.toUpperCase();
        this.species = builder.species;
        this.geneName = builder.geneName;
        this.transcriptId = builder.transcriptId;
        this.type = builder.type;
    }

    public String getHeader() {
        return header;
    }

    public String getSequence() {
        return sequence;
    }

    public String getSpecies() {
        return species;
    }

    public String getGeneName() {
        return geneName;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public SequenceType getType() {
        return type;
    }

    public int length() {
        return sequence.length();
    }

    public String subsequence(int start, int end) {
        return sequence.substring(start, end);
    }

    public char charAt(int index) {
        return sequence.charAt(index);
    }

    /**
     * Creates a builder for SequenceInfo.
     */
    public static Builder builder(String sequence) {
        return new Builder(sequence);
    }

    /**
     * Creates a SequenceInfo from a FASTA header and sequence.
     */
    public static SequenceInfo fromFasta(String header, String sequence) {
        return builder(sequence)
                .header(header)
                .type(SequenceType.FASTA)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceInfo that = (SequenceInfo) o;
        return Objects.equals(header, that.header) && Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, sequence);
    }

    @Override
    public String toString() {
        return "SequenceInfo{" +
                "header='" + header + '\'' +
                ", length=" + sequence.length() +
                ", type=" + type +
                '}';
    }

    /**
     * Types of biological sequences.
     */
    public enum SequenceType {
        FASTA,
        GENBANK,
        VIENNA,
        ENSEMBL,
        UNKNOWN
    }

    /**
     * Builder for SequenceInfo.
     */
    public static final class Builder {
        private String header = "";
        private final String sequence;
        private String species = "";
        private String geneName = "";
        private String transcriptId = "";
        private SequenceType type = SequenceType.UNKNOWN;

        private Builder(String sequence) {
            this.sequence = Objects.requireNonNull(sequence, "Sequence cannot be null");
        }

        public Builder header(String header) {
            this.header = header;
            return this;
        }

        public Builder species(String species) {
            this.species = species;
            return this;
        }

        public Builder geneName(String geneName) {
            this.geneName = geneName;
            return this;
        }

        public Builder transcriptId(String transcriptId) {
            this.transcriptId = transcriptId;
            return this;
        }

        public Builder type(SequenceType type) {
            this.type = type;
            return this;
        }

        public SequenceInfo build() {
            return new SequenceInfo(this);
        }
    }
}
