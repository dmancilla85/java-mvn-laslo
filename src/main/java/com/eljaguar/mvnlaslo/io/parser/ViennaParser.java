package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Vienna format (dot-bracket notation) files.
 * Single Responsibility: Vienna format parsing.
 */
public final class ViennaParser implements FastaParser {

    @Override
    public String getFormatName() {
        return "Vienna";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of("vienna", "dbn", "ct");
    }

    @Override
    public List<SequenceInfo> parse(Path filePath) throws IOException {
        List<SequenceInfo> sequences = new ArrayList<>();
        List<String> lines = com.eljaguar.mvnlaslo.util.FileUtils.readLines(filePath);

        String currentHeader = "";
        StringBuilder currentSequence = new StringBuilder();
        StringBuilder currentStructure = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith(">") || line.startsWith("#")) {
                // Save previous sequence if exists
                if (currentSequence.length() > 0) {
                    sequences.add(buildViennaSequence(currentHeader,
                            currentSequence.toString(), currentStructure.toString()));
                    currentSequence.setLength(0);
                    currentStructure.setLength(0);
                }
                currentHeader = line.substring(1).trim();
            } else if (line.contains(" ") && line.length() > 1) {
                // Format: sequence structure
                String[] parts = line.split("\\s+", 2);
                if (parts.length == 2) {
                    currentSequence.append(parts[0]);
                    currentStructure.append(parts[1]);
                }
            } else if (!line.isEmpty()) {
                // Could be sequence or structure line
                if (currentStructure.length() == 0 && currentSequence.length() > 0) {
                    currentStructure.append(line);
                } else {
                    currentSequence.append(line);
                }
            }
        }

        // Don't forget the last sequence
        if (currentSequence.length() > 0) {
            sequences.add(buildViennaSequence(currentHeader,
                    currentSequence.toString(), currentStructure.toString()));
        }

        return sequences;
    }

    private SequenceInfo buildViennaSequence(String header, String sequence, String structure) {
        String fullHeader = header;
        if (!structure.isEmpty()) {
            fullHeader += " [structure: " + structure + "]";
        }

        return SequenceInfo.builder(sequence)
                .header(">" + fullHeader)
                .type(SequenceInfo.SequenceType.VIENNA)
                .build();
    }
}
