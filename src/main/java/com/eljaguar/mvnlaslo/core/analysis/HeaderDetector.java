package com.eljaguar.mvnlaslo.core.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects and parses biological sequence headers.
 * Single Responsibility: header parsing and metadata extraction.
 */
public final class HeaderDetector {

    private static final Pattern FASTA_HEADER_PATTERN = Pattern.compile("^>(\\S+)(?:\\s+(.*))?$");
    private static final Pattern GENBANK_HEADER_PATTERN = Pattern.compile("^LOCUS\\s+(\\S+)");
    private static final Pattern ENSEMBL_PATTERN = Pattern.compile("^>(ENST\\d+\\.\\d+)");
    private static final Pattern GENE_NAME_PATTERN = Pattern.compile("gene[=:]\\s*(\\S+)");
    private static final Pattern SPECIES_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");

    private HeaderDetector() {
        // Utility class
    }

    /**
     * Detects the format of a header line.
     *
     * @param header the header line
     * @return the detected format
     */
    public static HeaderFormat detectFormat(String header) {
        if (header == null || header.isEmpty()) {
            return HeaderFormat.UNKNOWN;
        }
        if (header.startsWith(">")) {
            if (ENSEMBL_PATTERN.matcher(header).find()) {
                return HeaderFormat.ENSEMBL_FASTA;
            }
            return HeaderFormat.FASTA;
        }
        if (header.startsWith("LOCUS")) {
            return HeaderFormat.GENBANK;
        }
        if (header.contains("vienna") || header.contains("VIENNA") || header.contains("dotbracket") || header.contains("DOTBRACKET")) {
            return HeaderFormat.VIENNA;
        }
        return HeaderFormat.UNKNOWN;
    }

    /**
     * Extracts the identifier from a header.
     *
     * @param header the header line
     * @return the identifier, or empty string if not found
     */
    public static String extractId(String header) {
        if (header == null || header.isEmpty()) {
            return "";
        }

        Matcher fastaMatcher = FASTA_HEADER_PATTERN.matcher(header);
        if (fastaMatcher.matches()) {
            return fastaMatcher.group(1);
        }

        Matcher genbankMatcher = GENBANK_HEADER_PATTERN.matcher(header);
        if (genbankMatcher.find()) {
            return genbankMatcher.group(1);
        }

        Matcher ensemblMatcher = ENSEMBL_PATTERN.matcher(header);
        if (ensemblMatcher.find()) {
            return ensemblMatcher.group(1);
        }

        return "";
    }

    /**
     * Extracts the gene name from a header.
     *
     * @param header the header line
     * @return the gene name, or empty string if not found
     */
    public static String extractGeneName(String header) {
        if (header == null || header.isEmpty()) {
            return "";
        }
        Matcher matcher = GENE_NAME_PATTERN.matcher(header);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Extracts the species from a header.
     *
     * @param header the header line
     * @return the species, or empty string if not found
     */
    public static String extractSpecies(String header) {
        if (header == null || header.isEmpty()) {
            return "";
        }
        Matcher matcher = SPECIES_PATTERN.matcher(header);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Extracts the transcript ID from an Ensembl header.
     *
     * @param header the header line
     * @return the transcript ID, or empty string if not found
     */
    public static String extractTranscriptId(String header) {
        if (header == null || header.isEmpty()) {
            return "";
        }
        Matcher matcher = ENSEMBL_PATTERN.matcher(header);
        return matcher.find() ? matcher.group(1) : "";
    }

    /**
     * Header format types.
     */
    public enum HeaderFormat {
        FASTA,
        GENBANK,
        ENSEMBL_FASTA,
        VIENNA,
        UNKNOWN
    }
}
