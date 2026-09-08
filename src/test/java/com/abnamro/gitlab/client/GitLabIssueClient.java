package com.abnamro.gitlab.client;

import com.abnamro.gitlab.config.TestConfig;
import com.abnamro.gitlab.model.CreateIssueRequest;
import com.abnamro.gitlab.model.UpdateIssueRequest;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST client for the GitLab project Issues API.
 *
 * Each method builds an authenticated request through {@link TestConfig},
 * sends it to GitLab, and returns the raw REST Assured response so tests can
 * assert status codes and response fields.
 */
public class GitLabIssueClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitLabIssueClient.class);

    private String projectId;

    public GitLabIssueClient() {
        this.projectId = TestConfig.getProjectId();
    }

    public GitLabIssueClient(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Sends POST /projects/:id/issues with the issue fields as a JSON body.
     * GitLab returns the newly created issue, normally with HTTP 201.
     *
     * @param request issue title and optional creation fields
     * @return GitLab's create-issue response
     */
    public Response createIssue(CreateIssueRequest request) {
        LOGGER.info("Creating issue with title: {}", request.getTitle());
        
        return TestConfig.getAuthenticatedSpec()
                .body(request)
                .when()
                .post("/projects/{projectId}/issues", projectId)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    /**
     * Sends GET /projects/:id/issues/:issue_iid.
     * The IID is scoped to this project and is different from GitLab's global
     * issue ID. GitLab normally returns HTTP 200 for an existing issue.
     *
     * @param issueIid project-scoped internal issue ID
     * @return GitLab's issue response
     */
    public Response getIssue(Integer issueIid) {
        LOGGER.info("Getting issue with IID: {}", issueIid);
        
        return TestConfig.getAuthenticatedSpec()
                .when()
                .get("/projects/{projectId}/issues/{issueIid}", projectId, issueIid)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    /**
     * Sends PUT /projects/:id/issues/:issue_iid with changed fields as JSON.
     * Fields omitted from the request remain unchanged. GitLab normally
     * returns the updated issue with HTTP 200.
     *
     * @param issueIid project-scoped internal issue ID
     * @param request fields to update, such as title or state_event
     * @return GitLab's update-issue response
     */
    public Response updateIssue(Integer issueIid, UpdateIssueRequest request) {
        LOGGER.info("Updating issue IID: {} with state: {}", issueIid, request.getState_event());
        
        return TestConfig.getAuthenticatedSpec()
                .body(request)
                .when()
                .put("/projects/{projectId}/issues/{issueIid}", projectId, issueIid)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    /**
     * Sends DELETE /projects/:id/issues/:issue_iid.
     * A successful deletion normally returns HTTP 204; GitLab may also return
     * HTTP 200 depending on the API response.
     *
     * @param issueIid project-scoped internal issue ID
     * @return GitLab's delete response
     */
    public Response deleteIssue(Integer issueIid) {
        LOGGER.info("Deleting issue with IID: {}", issueIid);
        
        return TestConfig.getAuthenticatedSpec()
                .when()
                .delete("/projects/{projectId}/issues/{issueIid}", projectId, issueIid)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    /**
     * Sends GET /projects/:id/issues without filters.
     * GitLab returns a JSON array of issues, normally with HTTP 200.
     *
     * @return GitLab's issue list response
     */
    public Response listIssues() {
        LOGGER.info("Listing all issues in project: {}", projectId);
        
        return TestConfig.getAuthenticatedSpec()
                .when()
                .get("/projects/{projectId}/issues", projectId)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    /**
     * Sends GET /projects/:id/issues?state={state}.
     * The state filter is typically "opened" or "closed".
     *
     * @param state issue state used to filter the result
     * @return GitLab's filtered issue list response
     */
    public Response listIssuesByState(String state) {
        LOGGER.info("Listing issues with state: {}", state);
        
        return TestConfig.getAuthenticatedSpec()
                .queryParam("state", state)
                .when()
                .get("/projects/{projectId}/issues", projectId)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
