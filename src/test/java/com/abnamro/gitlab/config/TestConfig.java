package com.abnamro.gitlab.config;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Central configuration for REST Assured requests to GitLab.
 *
 * Credentials come from real environment variables first. For local Maven
 * runs, the ignored .env.local file is used as a fallback.
 */
public final class TestConfig {

    private static final String BASE_URL = "https://gitlab.com/api/v4";
    private static final Map<String, String> LOCAL_ENV = loadLocalEnv();
    private static final String GITLAB_TOKEN = getRequiredConfigValue("GITLAB_TOKEN");
    private static final String PROJECT_ID = getRequiredConfigValue("GITLAB_PROJECT_ID");

    private TestConfig() {
        // Utility class.
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getGitLabToken() {
        return GITLAB_TOKEN;
    }

    public static String getProjectId() {
        return PROJECT_ID;
    }

    public static RequestSpecification getAuthenticatedSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("PRIVATE-TOKEN", GITLAB_TOKEN)
                .contentType("application/json")
                .accept("application/json");
    }

    private static String getRequiredConfigValue(String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            value = LOCAL_ENV.get(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    key + " is not configured. Set it as an environment variable or in the local .env.local file."
            );
        }

        return value;
    }

    private static Map<String, String> loadLocalEnv() {
        Path envPath = Path.of(".env.local");

        if (!Files.exists(envPath)) {
            return Map.of();
        }

        Map<String, String> values = new HashMap<>();

        try {
            for (String line : Files.readAllLines(envPath)) {
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmedLine.indexOf('=');

                if (separatorIndex > 0) {
                    String key = trimmedLine.substring(0, separatorIndex).trim();
                    String value = trimmedLine.substring(separatorIndex + 1).trim();
                    values.put(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read .env.local", exception);
        }

        return Map.copyOf(values);
    }
}
