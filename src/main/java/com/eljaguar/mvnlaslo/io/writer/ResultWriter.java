package com.eljaguar.mvnlaslo.io.writer;

import com.eljaguar.mvnlaslo.core.model.MatchResult;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Strategy interface for writing MatchResult to different output formats.
 */
public interface ResultWriter {

    /**
     * Returns the output format name.
     */
    String getFormatName();

    /**
     * Writes the match result to the specified file.
     *
     * @param result   the match result to write
     * @param filePath the output file path
     * @throws IOException if the file cannot be written
     */
    void write(MatchResult result, Path filePath) throws IOException;

    /**
     * Returns the file extension for this output format.
     */
    String getFileExtension();
}
