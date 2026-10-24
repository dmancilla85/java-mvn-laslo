package com.eljaguar.mvnlaslo.io.network;

import com.eljaguar.mvnlaslo.config.AppConfiguration;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HTTP client for generic web requests.
 * Single Responsibility: HTTP communication.
 */
public final class WebClient {

    private final HttpClient httpClient;
    private final AppConfiguration config;

    public WebClient(AppConfiguration config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeoutMs()))
                .build();
    }

    public WebClient() {
        this(AppConfiguration.defaults());
    }

    /**
     * Sends a GET request and returns the response body.
     *
     * @param url the URL to request
     * @return the response body
     * @throws IOException if the request fails
     */
    public String get(String url) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("User-Agent", "LoopMatcher/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode() + " for URL: " + url);
            }

            return response.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted for URL: " + url, e);
        }
    }

    /**
     * Sends a POST request and returns the response body.
     *
     * @param url         the URL to request
     * @param requestBody the request body
     * @param contentType the content type
     * @return the response body
     * @throws IOException if the request fails
     */
    public String post(String url, String requestBody, String contentType) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("User-Agent", "LoopMatcher/1.0")
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("HTTP " + response.statusCode() + " for URL: " + url);
            }

            return response.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted for URL: " + url, e);
        }
    }
}
