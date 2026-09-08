package com.abnamro.gitlab.pages;

import com.abnamro.gitlab.client.GitLabIssueClient;
import io.restassured.response.Response;

/** API page for deleting issues from the configured GitLab project. */
public class DeleteIssuePage {

    private final GitLabIssueClient issueClient;

    /**
     * Initializes the page object with a GitLab issue client bound to the configured project.
     */
    public DeleteIssuePage() {
        issueClient = new GitLabIssueClient();
    }

    /**
     * Deletes the issue identified by the given project-scoped issue IID.
     *
     * @param issueIid the GitLab issue IID to delete
     * @return the raw HTTP response returned by the delete-issue API
     */
    public Response delete(Integer issueIid) {
        return issueClient.deleteIssue(issueIid);
    }
}
