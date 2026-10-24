package com.eljaguar.mvnlaslo.io.parser;

import com.eljaguar.mvnlaslo.core.model.SequenceInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Strategy interface for parsing different file formats into SequenceInfo objects.
 * Each parser implementation handles a specific biological file format.
 */
public interface FastaParser {

    /**
     * Returns the file format name.
     */
    String getFormatName();

    /**
     * Returns the file extensions this parser supports.
     */
    List<String> getSupportedExtensions();

    /**
     * Parses a file into a list of SequenceInfo objects.
     *
     * @param filePath the file to parse
     * @return list of parsed sequences
     * @throws IOException if the file cannot be read or parsed
     */
    List<SequenceInfo> parse(Path filePath) throws IOException;

    /**
     * Returns true if this parser can handle the given file extension.
     */
    default boolean canParse(String extension) {
        return getSupportedExtensions().stream()
                .anyMatch(ext -> ext.equalsIgnoreCase(extension));
    }
}
