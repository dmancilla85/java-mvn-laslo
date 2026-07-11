package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for standard FASTA format files.
 * Single Responsibility: FASTA file parsing.
 */
public final class FastaFileParser implements FastaParser {

    @Override
    public String getFormatName() {
        return "FASTA";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of("fasta", "fa", "fna", "faa", "fast");
    }

    @Override
    public List<SequenceInfo> parse(Path filePath) throws IOException {
        List<SequenceInfo> sequences = new ArrayList<>();
        List<String> lines = com.eljaguar.mvnlaslo.util.FileUtils.readLines(filePath);

        String currentHeader = "";
        StringBuilder currentSequence = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith(">")) {
                // Save previous sequence if exists
                if (currentSequence.length() > 0) {
                    sequences.add(SequenceInfo.fromFasta(currentHeader, currentSequence.toString()));
                    currentSequence.setLength(0);
                }
                currentHeader = line.substring(1).trim();
            } else {
                currentSequence.append(line.trim());
            }
        }

        // Don't forget the last sequence
        if (currentSequence.length() > 0) {
            sequences.add(SequenceInfo.fromFasta(currentHeader, currentSequence.toString()));
        }

        return sequences;
    }
}
