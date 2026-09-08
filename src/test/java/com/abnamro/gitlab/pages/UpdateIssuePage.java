package com.abnamro.gitlab.pages;

import com.abnamro.gitlab.client.GitLabIssueClient;
import com.abnamro.gitlab.model.UpdateIssueRequest;
import io.restassured.response.Response;

/** API page for updating existing issues in the configured GitLab project. */
public class UpdateIssuePage {

    private final GitLabIssueClient issueClient;

    /**
     * Initializes the page object with a GitLab issue client bound to the configured project.
     */
    public UpdateIssuePage() {
        issueClient = new GitLabIssueClient();
    }

    /**
     * Updates the issue identified by the given IID with the supplied fields.
     *
     * @param issueIid the GitLab issue IID to update
     * @param request the update payload containing the fields to modify
     * @return the raw HTTP response returned by the update-issue API
     */
    public Response update(Integer issueIid, UpdateIssueRequest request) {
        return issueClient.updateIssue(issueIid, request);
    }
}
