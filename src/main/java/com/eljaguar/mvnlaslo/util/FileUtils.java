package com.eljaguar.mvnlaslo.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * File utility methods for reading, writing, and validating files.
 */
public final class FileUtils {

    private FileUtils() {
        // Utility class
    }

    /**
     * Reads all lines from a file.
     *
     * @param path the file path
     * @return list of lines
     * @throws IOException if the file cannot be read
     */
    public static List<String> readLines(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.toList();
        }
    }

    /**
     * Reads all lines from a file, suppressing the checked exception.
     *
     * @param path the file path
     * @return list of lines
     * @throws UncheckedIOException if the file cannot be read
     */
    public static List<String> readLinesUnchecked(Path path) {
        try {
            return readLines(path);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + path, e);
        }
    }

    /**
     * Reads the entire file content as a string.
     *
     * @param path the file path
     * @return file content
     * @throws IOException if the file cannot be read
     */
    public static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Writes a string to a file.
     *
     * @param path    the file path
     * @param content the content to write
     * @throws IOException if the file cannot be written
     */
    public static void writeString(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Writes lines to a file.
     *
     * @param path  the file path
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    public static void writeLines(Path path, List<String> lines) throws IOException {
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Validates that a file exists and is readable.
     *
     * @param path the file path
     * @throws IOException if the file does not exist or is not readable
     */
    public static void validateReadable(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a regular file: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + path);
        }
    }

    /**
     * Validates that a file's extension matches one of the allowed extensions.
     *
     * @param path             the file path
     * @param allowedExtension file extensions (without dot), e.g., "fasta", "fa"
     * @throws IOException if the extension does not match
     */
    public static void validateExtension(Path path, String... allowedExtension) throws IOException {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            throw new IOException("File has no extension: " + path);
        }
        String ext = fileName.substring(dotIndex + 1).toLowerCase();
        for (String allowed : allowedExtension) {
            if (ext.equals(allowed.toLowerCase())) {
                return;
            }
        }
        throw new IOException("File extension '" + ext + "' not in allowed list: " + List.of(allowedExtension));
    }

    /**
     * Ensures a directory exists, creating it if necessary.
     *
     * @param dir the directory path
     * @throws IOException if the directory cannot be created
     */
    public static void ensureDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    /**
     * Returns a list of files in a directory matching the given extension.
     *
     * @param dir       the directory to search
     * @param extension the file extension (without dot)
     * @return list of matching file paths
     * @throws IOException if the directory cannot be read
     */
    public static List<Path> findFilesByExtension(Path dir, String extension) throws IOException {
        List<Path> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            files.filter(p -> {
                String name = p.getFileName().toString();
                return name.endsWith("." + extension);
            }).forEach(result::add);
        }
        return result;
    }

    /**
     * Creates a temporary file with the given prefix and suffix.
     *
     * @param prefix file name prefix
     * @param suffix file name suffix (extension)
     * @return path to the temporary file
     * @throws IOException if the temporary file cannot be created
     */
    public static Path createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }
}
