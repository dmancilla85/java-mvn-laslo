package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for Ensembl FASTA format files.
 * Single Responsibility: Ensembl FASTA parsing.
 */
public final class EnsemblFastaParser implements FastaParser {

    @Override
    public String getFormatName() {
        return "Ensembl FASTA";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of("ensembl", "ens", "cdna");
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
                    sequences.add(buildEnsemblSequence(currentHeader, currentSequence.toString()));
                    currentSequence.setLength(0);
                }
                currentHeader = line.substring(1).trim();
            } else {
                currentSequence.append(line.trim());
            }
        }

        // Don't forget the last sequence
        if (currentSequence.length() > 0) {
            sequences.add(buildEnsemblSequence(currentHeader, currentSequence.toString()));
        }

        return sequences;
    }

    private SequenceInfo buildEnsemblSequence(String header, String sequence) {
        String transcriptId = extractTranscriptId(header);
        String geneName = extractGeneName(header);

        return SequenceInfo.builder(sequence)
                .header(">" + header)
                .transcriptId(transcriptId)
                .geneName(geneName)
                .type(SequenceInfo.SequenceType.ENSEMBL)
                .build();
    }

    private String extractTranscriptId(String header) {
        // Ensembl headers typically start with >ENST...
        if (header.startsWith("ENST")) {
            return header.split("\\s+")[0];
        }
        return "";
    }

    private String extractGeneName(String header) {
        // Try to extract gene name from Ensembl header
        String[] parts = header.split("\\s+");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("gene:".equals(parts[i]) || "gene=".equals(parts[i])) {
                return parts[i + 1];
            }
        }
        return "";
    }
}
