package com.eljaguar.mvnlaslo.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralizes all configuration for the application.
 * Follows Configuration Object pattern — single source of truth for all configurable values.
 * Immutable after construction.
 */
public final class AppConfiguration {

    private static final Logger LOGGER = Logger.getLogger(AppConfiguration.class.getName());
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final String EXT_DIR = "./ext/";
    private static final String RNAFOLD_EXE = "RNAfold.exe";
    private static final String USHUFFLE_EXE = "ushuffle.exe";

    // Thread pool defaults
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    private static final int MIN_THREAD_POOL_SIZE = 1;
    private static final int MAX_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    // Temperature defaults (Celsius)
    private static final double DEFAULT_LOW_TEMPERATURE = 37.0;
    private static final double DEFAULT_HIGH_TEMPERATURE = 70.0;

    // Matching defaults
    private static final int DEFAULT_MIN_STEM_LENGTH = 5;
    private static final int DEFAULT_MAX_LOOP_LENGTH = 20;
    private static final double DEFAULT_MIN_COMPLEMENTARITY = 0.8;

    // Network defaults
    private static final int DEFAULT_CONNECT_TIMEOUT_MS = 10_000;
    private static final int DEFAULT_READ_TIMEOUT_MS = 30_000;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY_MS = 1_000;

    private final Path externalToolsDir;
    private final String rnaFoldPath;
    private final String uShufflePath;
    private final int threadPoolSize;
    private final double lowTemperature;
    private final double highTemperature;
    private final int minStemLength;
    private final int maxLoopLength;
    private final double minComplementarity;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final int maxRetries;
    private final long retryDelayMs;
    private final String proxyHost;
    private final int proxyPort;
    private final String ncbiApiKey;

    private AppConfiguration(Builder builder) {
        this.externalToolsDir = builder.externalToolsDir;
        this.rnaFoldPath = builder.rnaFoldPath;
        this.uShufflePath = builder.uShufflePath;
        this.threadPoolSize = builder.threadPoolSize;
        this.lowTemperature = builder.lowTemperature;
        this.highTemperature = builder.highTemperature;
        this.minStemLength = builder.minStemLength;
        this.maxLoopLength = builder.maxLoopLength;
        this.minComplementarity = builder.minComplementarity;
        this.connectTimeoutMs = builder.connectTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.maxRetries = builder.maxRetries;
        this.retryDelayMs = builder.retryDelayMs;
        this.proxyHost = builder.proxyHost;
        this.proxyPort = builder.proxyPort;
        this.ncbiApiKey = builder.ncbiApiKey;
    }

    /**
     * Returns a new Builder instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a configuration with default values.
     */
    public static AppConfiguration defaults() {
        return new Builder().build();
    }

    /**
     * Loads configuration from a properties file, falling back to defaults for missing values.
     */
    public static AppConfiguration fromProperties(String resourcePath) {
        Builder builder = new Builder();
        try (InputStream is = AppConfiguration.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                builder.loadFromProperties(props);
            }
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Could not load config from " + resourcePath + ", using defaults", e);
        }
        return builder.build();
    }

    public Path getExternalToolsDir() {
        return externalToolsDir;
    }

    public String getRnaFoldPath() {
        return rnaFoldPath;
    }

    public String getUShufflePath() {
        return uShufflePath;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public double getLowTemperature() {
        return lowTemperature;
    }

    public double getHighTemperature() {
        return highTemperature;
    }

    public int getMinStemLength() {
        return minStemLength;
    }

    public int getMaxLoopLength() {
        return maxLoopLength;
    }

    public double getMinComplementarity() {
        return minComplementarity;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getRetryDelayMs() {
        return retryDelayMs;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getNcbiApiKey() {
        return ncbiApiKey;
    }

    public boolean hasProxy() {
        return proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0;
    }

    public boolean hasNcbiApiKey() {
        return ncbiApiKey != null && !ncbiApiKey.isEmpty();
    }

    @Override
    public String toString() {
        return "AppConfiguration{" +
                "externalToolsDir=" + externalToolsDir +
                ", threadPoolSize=" + threadPoolSize +
                ", lowTemperature=" + lowTemperature +
                ", highTemperature=" + highTemperature +
                ", minStemLength=" + minStemLength +
                ", maxLoopLength=" + maxLoopLength +
                ", minComplementarity=" + minComplementarity +
                '}';
    }

    /**
     * Builder for AppConfiguration.
     */
    public static final class Builder {
        private Path externalToolsDir = Paths.get(EXT_DIR);
        private String rnaFoldPath = EXT_DIR + RNAFOLD_EXE;
        private String uShufflePath = EXT_DIR + USHUFFLE_EXE;
        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private double lowTemperature = DEFAULT_LOW_TEMPERATURE;
        private double highTemperature = DEFAULT_HIGH_TEMPERATURE;
        private int minStemLength = DEFAULT_MIN_STEM_LENGTH;
        private int maxLoopLength = DEFAULT_MAX_LOOP_LENGTH;
        private double minComplementarity = DEFAULT_MIN_COMPLEMENTARITY;
        private int connectTimeoutMs = DEFAULT_CONNECT_TIMEOUT_MS;
        private int readTimeoutMs = DEFAULT_READ_TIMEOUT_MS;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private long retryDelayMs = DEFAULT_RETRY_DELAY_MS;
        private String proxyHost = "";
        private int proxyPort = 0;
        private String ncbiApiKey = "";

        public Builder externalToolsDir(Path dir) {
            this.externalToolsDir = dir;
            return this;
        }

        public Builder rnaFoldPath(String path) {
            this.rnaFoldPath = path;
            return this;
        }

        public Builder uShufflePath(String path) {
            this.uShufflePath = path;
            return this;
        }

        public Builder threadPoolSize(int size) {
            if (size < MIN_THREAD_POOL_SIZE || size > MAX_THREAD_POOL_SIZE) {
                throw new IllegalArgumentException(
                        "Thread pool size must be between " + MIN_THREAD_POOL_SIZE + " and " + MAX_THREAD_POOL_SIZE);
            }
            this.threadPoolSize = size;
            return this;
        }

        public Builder lowTemperature(double temp) {
            this.lowTemperature = temp;
            return this;
        }

        public Builder highTemperature(double temp) {
            this.highTemperature = temp;
            return this;
        }

        public Builder minStemLength(int length) {
            this.minStemLength = length;
            return this;
        }

        public Builder maxLoopLength(int length) {
            this.maxLoopLength = length;
            return this;
        }

        public Builder minComplementarity(double ratio) {
            if (ratio < 0.0 || ratio > 1.0) {
                throw new IllegalArgumentException("Complementarity ratio must be between 0.0 and 1.0");
            }
            this.minComplementarity = ratio;
            return this;
        }

        public Builder connectTimeoutMs(int ms) {
            this.connectTimeoutMs = ms;
            return this;
        }

        public Builder readTimeoutMs(int ms) {
            this.readTimeoutMs = ms;
            return this;
        }

        public Builder maxRetries(int retries) {
            this.maxRetries = retries;
            return this;
        }

        public Builder retryDelayMs(long ms) {
            this.retryDelayMs = ms;
            return this;
        }

        public Builder proxy(String host, int port) {
            this.proxyHost = host;
            this.proxyPort = port;
            return this;
        }

        public Builder ncbiApiKey(String key) {
            this.ncbiApiKey = key;
            return this;
        }

        void loadFromProperties(Properties props) {
            if (props.containsKey("app.thread-pool-size")) {
                threadPoolSize = Integer.parseInt(props.getProperty("app.thread-pool-size"));
            }
            if (props.containsKey("app.low-temperature")) {
                lowTemperature = Double.parseDouble(props.getProperty("app.low-temperature"));
            }
            if (props.containsKey("app.high-temperature")) {
                highTemperature = Double.parseDouble(props.getProperty("app.high-temperature"));
            }
            if (props.containsKey("app.min-stem-length")) {
                minStemLength = Integer.parseInt(props.getProperty("app.min-stem-length"));
            }
            if (props.containsKey("app.max-loop-length")) {
                maxLoopLength = Integer.parseInt(props.getProperty("app.max-loop-length"));
            }
            if (props.containsKey("app.min-complementarity")) {
                minComplementarity = Double.parseDouble(props.getProperty("app.min-complementarity"));
            }
            if (props.containsKey("app.proxy-host")) {
                proxyHost = props.getProperty("app.proxy-host");
            }
            if (props.containsKey("app.proxy-port")) {
                proxyPort = Integer.parseInt(props.getProperty("app.proxy-port"));
            }
            if (props.containsKey("app.ncbi-api-key")) {
                ncbiApiKey = props.getProperty("app.ncbi-api-key");
            }
            if (props.containsKey("app.connect-timeout-ms")) {
                connectTimeoutMs = Integer.parseInt(props.getProperty("app.connect-timeout-ms"));
            }
            if (props.containsKey("app.read-timeout-ms")) {
                readTimeoutMs = Integer.parseInt(props.getProperty("app.read-timeout-ms"));
            }
            if (props.containsKey("tools.rnafold-path")) {
                rnaFoldPath = props.getProperty("tools.rnafold-path");
            }
            if (props.containsKey("tools.ushuffle-path")) {
                uShufflePath = props.getProperty("tools.ushuffle-path");
            }
        }

        public AppConfiguration build() {
            return new AppConfiguration(this);
        }
    }
}
