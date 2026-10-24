package com.eljaguar.mvnlaslo.io.network;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

/**
 * Proxy configuration for network connections.
 * Immutable value object.
 */
public final class ProxyConfig {

    private final String host;
    private final int port;
    private final Proxy.Type type;

    public ProxyConfig(String host, int port, Proxy.Type type) {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host cannot be null or empty");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("port must be positive");
        }
        this.host = host;
        this.port = port;
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    public ProxyConfig(String host, int port) {
        this(host, port, Proxy.Type.HTTP);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Proxy.Type getType() {
        return type;
    }

    /**
     * Creates a java.net.Proxy from this configuration.
     */
    public Proxy toProxy() {
        return new Proxy(type, new InetSocketAddress(host, port));
    }

    public boolean isValid() {
        return host != null && !host.isEmpty() && port > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyConfig that = (ProxyConfig) o;
        return port == that.port && host.equals(that.host) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, type);
    }

    @Override
    public String toString() {
        return type + "://" + host + ":" + port;
    }
}
