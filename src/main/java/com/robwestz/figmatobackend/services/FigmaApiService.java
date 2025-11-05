package com.robwestz.figmatobackend.services;

import com.google.gson.Gson;
import com.intellij.openapi.components.Service;
import com.robwestz.figmatobackend.models.FigmaFile;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public final class FigmaApiService {
    private static final String FIGMA_API_BASE_URL = "https://api.figma.com/v1";
    private final OkHttpClient httpClient;
    private final Gson gson;
    private String accessToken;

    public FigmaApiService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    /**
     * Fetches a Figma file by its key
     * @param fileKey The Figma file key (from the URL)
     * @return The FigmaFile object
     * @throws IOException if the API call fails
     */
    public FigmaFile getFile(String fileKey) throws IOException {
        if (!hasAccessToken()) {
            throw new IllegalStateException("Figma access token not set");
        }

        String url = FIGMA_API_BASE_URL + "/files/" + fileKey;
        Request request = new Request.Builder()
                .url(url)
                .header("X-Figma-Token", accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch Figma file: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, FigmaFile.class);
        }
    }

    /**
     * Exports a specific node from a Figma file as an image
     * @param fileKey The Figma file key
     * @param nodeId The node ID to export
     * @param format The image format (png, jpg, svg, pdf)
     * @return The URL to the exported image
     * @throws IOException if the API call fails
     */
    public String exportNode(String fileKey, String nodeId, String format) throws IOException {
        if (!hasAccessToken()) {
            throw new IllegalStateException("Figma access token not set");
        }

        String url = FIGMA_API_BASE_URL + "/images/" + fileKey + "?ids=" + nodeId + "&format=" + format;
        Request request = new Request.Builder()
                .url(url)
                .header("X-Figma-Token", accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to export node: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            // Parse the response to get the image URL
            return responseBody; // Simplified - in production, parse JSON properly
        }
    }

    /**
     * Validates that the access token is valid by making a test API call
     * @return true if the token is valid, false otherwise
     */
    public boolean validateAccessToken() {
        if (!hasAccessToken()) {
            return false;
        }

        try {
            // Make a simple API call to check if token is valid
            String url = FIGMA_API_BASE_URL + "/me";
            Request request = new Request.Builder()
                    .url(url)
                    .header("X-Figma-Token", accessToken)
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException e) {
            return false;
        }
    }
}
