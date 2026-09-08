package com.abnamro.gitlab.pages;

import com.abnamro.gitlab.client.GitLabIssueClient;
import io.restassured.response.Response;

/** API page for retrieving a single issue from the configured GitLab project. */
public class GetIssuePage {

    private final GitLabIssueClient issueClient;

    /**
     * Initializes the page object with a GitLab issue client bound to the configured project.
     */
    public GetIssuePage() {
        issueClient = new GitLabIssueClient();
    }

    /**
     * Fetches the issue with the supplied project-scoped IID.
     *
     * @param issueIid the GitLab issue IID to retrieve
     * @return the raw HTTP response returned by the get-issue API
     */
    public Response get(Integer issueIid) {
        return issueClient.getIssue(issueIid);
    }
}
