package com.abnamro.gitlab.pages;

import com.abnamro.gitlab.client.GitLabIssueClient;
import com.abnamro.gitlab.model.CreateIssueRequest;
import io.restassured.response.Response;

/** API page for creating issues in the configured GitLab project. */
public class CreateIssuePage {

    private final GitLabIssueClient issueClient;

    /**
     * Initializes the page object with a GitLab issue client bound to the configured project.
     */
    public CreateIssuePage() {
        issueClient = new GitLabIssueClient();
    }

    /**
     * Creates a new issue using the supplied request payload.
     *
     * @param request issue details to send to GitLab
     * @return the raw HTTP response returned by the create-issue API
     */
    public Response create(CreateIssueRequest request) {
        return issueClient.createIssue(request);
    }
}
