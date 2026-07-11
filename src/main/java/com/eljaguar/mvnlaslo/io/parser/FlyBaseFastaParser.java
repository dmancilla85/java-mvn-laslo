package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for FlyBase FASTA format files.
 * Single Responsibility: FlyBase FASTA parsing.
 */
public final class FlyBaseFastaParser implements FastaParser {

    @Override
    public String getFormatName() {
        return "FlyBase FASTA";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of("flybase", "fb");
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
                    sequences.add(buildFlyBaseSequence(currentHeader, currentSequence.toString()));
                    currentSequence.setLength(0);
                }
                currentHeader = line.substring(1).trim();
            } else {
                currentSequence.append(line.trim());
            }
        }

        // Don't forget the last sequence
        if (currentSequence.length() > 0) {
            sequences.add(buildFlyBaseSequence(currentHeader, currentSequence.toString()));
        }

        return sequences;
    }

    private SequenceInfo buildFlyBaseSequence(String header, String sequence) {
        return SequenceInfo.builder(sequence)
                .header(">" + header)
                .type(SequenceInfo.SequenceType.FASTA)
                .build();
    }
}
