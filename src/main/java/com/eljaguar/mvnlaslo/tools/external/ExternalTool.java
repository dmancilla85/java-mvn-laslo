package com.eljaguar.mvnlaslo.tools.external;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Base class for wrapping external command-line tools.
 * Provides lifecycle management, timeout handling, and error reporting.
 */
public class ExternalTool {

    private final String name;
    private final Path executablePath;
    private final Duration timeout;

    /**
     * Creates an external tool wrapper.
     *
     * @param name           human-readable tool name
     * @param executablePath path to the executable
     * @param timeout        maximum execution time
     */
    protected ExternalTool(String name, Path executablePath, Duration timeout) {
        this.name = name;
        this.executablePath = executablePath;
        this.timeout = timeout;
        validateExecutable();
    }

    /**
     * Creates an external tool wrapper with a default 60-second timeout.
     */
    protected ExternalTool(String name, Path executablePath) {
        this(name, executablePath, Duration.ofSeconds(60));
    }

    /**
     * Executes the external tool with the given arguments.
     *
     * @param args command-line arguments
     * @return ProcessResult containing exit code, stdout, and stderr
     * @throws ToolExecutionException if the tool fails to execute or times out
     */
    public ProcessResult execute(String... args) throws ToolExecutionException {
        List<String> command = new ArrayList<>();
        command.add(executablePath.toAbsolutePath().toString());
        command.addAll(List.of(args));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(false);

        try {
            Process process = pb.start();

            // Read streams BEFORE waitFor() to prevent deadlock when OS buffers fill
            String stdout;
            String stderr;
            try (var stdoutStream = process.getInputStream();
                 var stderrStream = process.getErrorStream()) {
                stdout = new String(stdoutStream.readAllBytes()).trim();
                stderr = new String(stderrStream.readAllBytes()).trim();
            }

            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new ToolExecutionException(name + " timed out after " + timeout.toSeconds() + " seconds");
            }

            int exitCode = process.exitValue();

            if (exitCode != 0) {
                throw new ToolExecutionException(
                        name + " failed with exit code " + exitCode + ": " + stderr);
            }

            return new ProcessResult(exitCode, stdout, stderr);

        } catch (IOException e) {
            throw new ToolExecutionException("Failed to execute " + name + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ToolExecutionException(name + " execution interrupted", e);
        }
    }

    /**
     * Returns true if the executable exists and is readable.
     */
    public boolean isAvailable() {
        return Files.isExecutable(executablePath);
    }

    public String getName() {
        return name;
    }

    public Path getExecutablePath() {
        return executablePath;
    }

    private void validateExecutable() {
        if (!Files.exists(executablePath)) {
            // Don't throw — allow lazy validation via isAvailable()
        }
    }

    @Override
    public String toString() {
        return name + " [" + executablePath + "]";
    }

    /**
     * Result of an external tool execution.
     */
    public record ProcessResult(int exitCode, String stdout, String stderr) {

        public boolean isSuccess() {
            return exitCode == 0;
        }
    }

    /**
     * Exception thrown when an external tool fails to execute.
     */
    public static class ToolExecutionException extends Exception {

        public ToolExecutionException(String message) {
            super(message);
        }

        public ToolExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
