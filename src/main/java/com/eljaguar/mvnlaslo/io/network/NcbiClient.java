package com.eljaguar.mvnlaslo.io.network;

import com.eljaguar.mvnlaslo.config.AppConfiguration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Client for accessing NCBI databases (GenBank, etc.).
 * Single Responsibility: NCBI HTTP communication.
 */
public final class NcbiClient {

    private static final String NCBI_BASE_URL = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
    private static final String EFETCH_URL = NCBI_BASE_URL + "efetch.fcgi";
    private static final String ESEARCH_URL = NCBI_BASE_URL + "esearch.fcgi";

    private final HttpClient httpClient;
    private final AppConfiguration config;

    public NcbiClient(AppConfiguration config) {
        this.config = config;
        this.httpClient = createHttpClient();
    }

    public NcbiClient() {
        this(AppConfiguration.defaults());
    }

    /**
     * Fetches a GenBank record by accession number.
     *
     * @param accession the accession number
     * @return the GenBank record content
     * @throws IOException if the request fails
     */
    public String fetchGenBankRecord(String accession) throws IOException {
        try {
            String encodedAccession = URLEncoder.encode(accession, StandardCharsets.UTF_8);
            String url = EFETCH_URL +
                    "?db=nucleotide" +
                    "&id=" + encodedAccession +
                    "&rettype=genbank" +
                    "&retmode=text";

            if (config.hasNcbiApiKey()) {
                url += "&api_key=" + URLEncoder.encode(config.getNcbiApiKey(), StandardCharsets.UTF_8);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("User-Agent", "LoopMatcher/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("NCBI returned HTTP " + response.statusCode() + " for accession: " + accession);
            }

            return response.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted while fetching accession: " + accession, e);
        }
    }

    /**
     * Searches NCBI for sequences matching a query.
     *
     * @param query    the search query
     * @param database the database to search (e.g., "nucleotide", "protein")
     * @return the search results
     * @throws IOException if the request fails
     */
    public String search(String query, String database) throws IOException {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String encodedDb = URLEncoder.encode(database, StandardCharsets.UTF_8);
            String url = ESEARCH_URL +
                    "?db=" + encodedDb +
                    "&term=" + encodedQuery +
                    "&retmode=json";

            if (config.hasNcbiApiKey()) {
                url += "&api_key=" + URLEncoder.encode(config.getNcbiApiKey(), StandardCharsets.UTF_8);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(config.getReadTimeoutMs()))
                    .header("User-Agent", "LoopMatcher/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("NCBI search returned HTTP " + response.statusCode());
            }

            return response.body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Search request interrupted", e);
        }
    }

    private HttpClient createHttpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeoutMs()));

        if (config.hasProxy()) {
            builder.proxy(ProxySelector.of(new InetSocketAddress(config.getProxyHost(), config.getProxyPort())));
        }

        return builder.build();
    }
}
