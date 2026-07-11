package com.eljaguar.mvnlaslo.core.model;

/**
 * Observer pattern interface for tracking operation progress.
 * Implementations update the UI or log progress of long-running operations.
 */
@FunctionalInterface
public interface ProgressListener {

    /**
     * Called when progress is updated.
     *
     * @param progress percentage complete (0–100)
     * @param message  descriptive status message
     */
    void onProgress(int progress, String message);

    /**
     * Called when the operation completes successfully.
     */
    default void onComplete(String message) {
        onProgress(100, message);
    }

    /**
     * Called when the operation fails.
     */
    default void onError(String errorMessage) {
        onProgress(0, "Error: " + errorMessage);
    }

    /**
     * No-op implementation for cases where progress tracking is not needed.
     */
    static ProgressListener none() {
        return (progress, message) -> { /* no-op */ };
    }
}
