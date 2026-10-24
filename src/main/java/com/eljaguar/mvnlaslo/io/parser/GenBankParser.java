package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for GenBank format files.
 * Single Responsibility: GenBank file parsing.
 */
public final class GenBankParser implements FastaParser {

    private static final Pattern LOCUS_PATTERN = Pattern.compile("^LOCUS\\s+(\\S+)");
    private static final Pattern DEFINITION_PATTERN = Pattern.compile("^DEFINITION\\s+(.*)");
    private static final Pattern ACCESSION_PATTERN = Pattern.compile("^ACCESSION\\s+(\\S+)");
    private static final Pattern ORGANISM_PATTERN = Pattern.compile("^ORGANISM\\s+(.*)");
    private static final Pattern GENE_PATTERN = Pattern.compile("^\\s+/gene=\"(.*)\"");
    private static final Pattern ORIGIN_PATTERN = Pattern.compile("^ORIGIN");

    @Override
    public String getFormatName() {
        return "GenBank";
    }

    @Override
    public List<String> getSupportedExtensions() {
        return List.of("gb", "gbk", "genbank");
    }

    @Override
    public List<SequenceInfo> parse(Path filePath) throws IOException {
        List<SequenceInfo> records = new ArrayList<>();
        List<String> lines = com.eljaguar.mvnlaslo.util.FileUtils.readLines(filePath);

        String currentLocus = "";
        String currentDefinition = "";
        String currentAccession = "";
        String currentOrganism = "";
        String currentGene = "";
        StringBuilder sequenceBuilder = new StringBuilder();
        boolean inOrigin = false;

        for (String line : lines) {
            Matcher locusMatcher = LOCUS_PATTERN.matcher(line);
            if (locusMatcher.find()) {
                // Save previous record if exists
                if (!sequenceBuilder.isEmpty()) {
                    records.add(buildSequenceInfo(currentLocus, currentDefinition,
                            currentAccession, currentOrganism, currentGene, sequenceBuilder.toString()));
                    sequenceBuilder.setLength(0);
                }
                currentLocus = locusMatcher.group(1);
                currentDefinition = "";
                currentAccession = "";
                currentOrganism = "";
                currentGene = "";
                inOrigin = false;
                continue;
            }

            Matcher defMatcher = DEFINITION_PATTERN.matcher(line);
            if (defMatcher.find()) {
                currentDefinition = defMatcher.group(1).trim();
                continue;
            }

            Matcher accMatcher = ACCESSION_PATTERN.matcher(line);
            if (accMatcher.find()) {
                currentAccession = accMatcher.group(1);
                continue;
            }

            Matcher orgMatcher = ORGANISM_PATTERN.matcher(line);
            if (orgMatcher.find()) {
                currentOrganism = orgMatcher.group(1).trim();
                continue;
            }

            Matcher geneMatcher = GENE_PATTERN.matcher(line);
            if (geneMatcher.find()) {
                currentGene = geneMatcher.group(1);
                continue;
            }

            if (ORIGIN_PATTERN.matcher(line).find()) {
                inOrigin = true;
                continue;
            }

            if (inOrigin) {
                String cleaned = line.replaceAll("\\d+", "").replaceAll("\\s+", "");
                sequenceBuilder.append(cleaned);
            }
        }

        // Don't forget the last record
        if (!sequenceBuilder.isEmpty()) {
            records.add(buildSequenceInfo(currentLocus, currentDefinition,
                    currentAccession, currentOrganism, currentGene, sequenceBuilder.toString()));
        }

        return records;
    }

    private SequenceInfo buildSequenceInfo(String locus, String definition,
                                           String accession, String organism,
                                           String gene, String sequence) {
        String header = ">" + locus;
        if (!definition.isEmpty()) {
            header += " " + definition;
        }

        return SequenceInfo.builder(sequence)
                .header(header)
                .species(organism)
                .geneName(gene)
                .transcriptId(accession)
                .type(SequenceInfo.SequenceType.GENBANK)
                .build();
    }
}
