package com.eljaguar.mvnlaslo.io.writer;

import com.eljaguar.mvnlaslo.core.model.StemLoopModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Provides CSV headers for different output formats.
 * Single Responsibility: CSV header generation.
 */
public final class CsvHeaderProvider {

    private CsvHeaderProvider() {
        // Utility class
    }

    /**
     * Returns the standard CSV headers for stem-loop output.
     */
    public static List<String> getStandardHeaders() {
        List<String> headers = new ArrayList<>();
        headers.add("ID");
        headers.add("Sequence");
        headers.add("Stem Start");
        headers.add("Stem End");
        headers.add("Loop Start");
        headers.add("Loop End");
        headers.add("Loop Length");
        headers.add("Stem Length");
        headers.add("Complementarity");
        headers.add("Species");
        headers.add("Gene Name");
        headers.add("Transcript ID");
        headers.add("Match Type");
        return Collections.unmodifiableList(headers);
    }

    /**
     * Returns CSV headers for detailed output.
     */
    public static List<String> getDetailedHeaders() {
        List<String> headers = new ArrayList<>(getStandardHeaders());
        headers.add("Full Sequence");
        headers.add("Stem Sequence");
        headers.add("Loop Sequence");
        headers.add("Reversed");
        headers.add("Complement");
        return headers;
    }

    /**
     * Converts a StemLoopModel to a CSV row.
     */
    public static List<String> toCsvRow(StemLoopModel model) {
        Objects.requireNonNull(model, "model cannot be null");
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(model.getId()));
        row.add(String.valueOf(model.getSequence()));
        row.add(String.valueOf(model.getStemStart()));
        row.add(String.valueOf(model.getStemEnd()));
        row.add(String.valueOf(model.getLoopStart()));
        row.add(String.valueOf(model.getLoopEnd()));
        row.add(String.valueOf(model.getLoopLength()));
        row.add(String.valueOf(model.getStemLength()));
        row.add(String.format("%.4f", model.getComplementarityRatio()));
        row.add(String.valueOf(model.getSpecies()));
        row.add(String.valueOf(model.getGeneName()));
        row.add(String.valueOf(model.getTranscriptId()));
        row.add(model.getMatchType().name());
        return row;
    }

    /**
     * Converts a StemLoopModel to a detailed CSV row.
     */
    public static List<String> toDetailedCsvRow(StemLoopModel model) {
        List<String> row = new ArrayList<>(toCsvRow(model));
        row.add(String.valueOf(model.getFullSequence()));
        row.add(String.valueOf(model.getStemSequence()));
        row.add(String.valueOf(model.getLoopSequence()));
        row.add(String.valueOf(model.getReversed()));
        row.add(String.valueOf(model.getComplement()));
        return row;
    }

    /**
     * Joins a list of values with commas, escaping values that contain commas.
     */
    public static String joinCsvLine(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            String value = values.get(i);
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                sb.append('"').append(value.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(value);
            }
        }
        return sb.toString();
    }
}
