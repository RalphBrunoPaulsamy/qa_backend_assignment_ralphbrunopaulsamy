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
public class TestConfig {

    private static final String BASE_URL = "https://gitlab.com/api/v4";
    private static final Map<String, String> LOCAL_ENV = loadLocalEnv();
    private static final String GITLAB_TOKEN = getConfigValue("GITLAB_TOKEN");
    private static final String PROJECT_ID = getConfigValue("GITLAB_PROJECT_ID");

    static {
        if (GITLAB_TOKEN == null || GITLAB_TOKEN.isEmpty()) {
            throw new IllegalStateException(
                "GITLAB_TOKEN environment variable is not set. " +
                "Please set it before running tests."
            );
        }
        if (PROJECT_ID == null || PROJECT_ID.isEmpty()) {
            throw new IllegalStateException(
                "GITLAB_PROJECT_ID environment variable is not set. " +
                "Please set it before running tests."
            );
        }
    }

    /**
     * Resolves a configuration value from the process environment before falling back to the local .env file.
     *
     * @param key the configuration key to resolve
     * @return the configured value, or null if it is not present
     */
    private static String getConfigValue(String key) {
        // CI and shell-provided values take precedence over local files.
        String environmentValue = System.getenv(key);
        return environmentValue != null && !environmentValue.isEmpty()
                ? environmentValue
                : LOCAL_ENV.get(key);
    }

    /**
     * Loads the local environment variables from the ignored .env.local file when available.
     *
     * @return a map of environment values read from the local file
     */
    private static Map<String, String> loadLocalEnv() {
        // Keep local credentials out of source code and version control.
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

                int separator = trimmedLine.indexOf('=');
                if (separator > 0) {
                    String key = trimmedLine.substring(0, separator).trim();
                    String value = trimmedLine.substring(separator + 1).trim();
                    values.put(key, value);
                }
            }
            return values;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read .env.local", exception);
        }
    }

    /**
     * Returns the GitLab API base URL used for all requests.
     *
     * @return the GitLab v4 API base URL
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Returns the configured GitLab personal access token.
     *
     * @return the GitLab private token used for authentication
     */
    public static String getGitLabToken() {
        return GITLAB_TOKEN;
    }

    /**
     * Returns the configured GitLab project identifier.
     *
     * @return the project ID used in API paths
     */
    public static String getProjectId() {
        return PROJECT_ID;
    }

    /**
     * Returns the project-scoped IID of an issue supplied for delete testing.
     *
     * @return the stored issue IID from the local environment configuration
     */
    public static Integer getIssueIid() {
        String issueIid = getConfigValue("GITLAB_ISSUE_IID");
        if (issueIid == null || issueIid.isEmpty()) {
            throw new IllegalStateException(
                    "GITLAB_ISSUE_IID is not set. Provide the IID of an existing issue to delete."
            );
        }

        try {
            return Integer.valueOf(issueIid);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("GITLAB_ISSUE_IID must be a positive integer.", exception);
        }
    }

    /**
     * Stores the latest created issue IID in the ignored local env file so
     * separate GET, PUT, and DELETE Maven runs use the same issue.
     *
     * @param issueIid the project-scoped issue IID to persist for follow-up test runs
     */
    public static void saveIssueIid(Integer issueIid) {
        Path envPath = Path.of(".env.local");
        try {
            java.util.List<String> lines = Files.exists(envPath)
                    ? Files.readAllLines(envPath)
                    : new java.util.ArrayList<>();
            String issueLine = "GITLAB_ISSUE_IID=" + issueIid;
            boolean replaced = false;

            for (int index = 0; index < lines.size(); index++) {
                if (lines.get(index).startsWith("GITLAB_ISSUE_IID=")) {
                    lines.set(index, issueLine);
                    replaced = true;
                    break;
                }
            }

            if (!replaced) {
                lines.add(issueLine);
            }
            Files.write(envPath, lines);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save GITLAB_ISSUE_IID in .env.local", exception);
        }
    }

    /**
     * Creates the shared request specification used by every API call.
     * It sets the GitLab v4 base URL, private-token authentication, and JSON
     * content type before the client adds its endpoint-specific method/path.
     *
     * @return a configured RestAssured request specification for authenticated GitLab calls
     */
    public static RequestSpecification getAuthenticatedSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("PRIVATE-TOKEN", GITLAB_TOKEN)
                .header("Content-Type", "application/json");
    }
}
