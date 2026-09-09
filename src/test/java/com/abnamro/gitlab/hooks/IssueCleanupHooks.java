package com.abnamro.gitlab.hooks;

import com.abnamro.gitlab.client.GitLabIssueClient;
import com.abnamro.gitlab.context.IssueScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cleans up the issue created by the CRUD workflow scenario.
 */
public class IssueCleanupHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueCleanupHooks.class);

    private final IssueScenarioContext context;
    private final GitLabIssueClient client;

    public IssueCleanupHooks(IssueScenarioContext context, GitLabIssueClient client) {
        this.context = context;
        this.client = client;
    }

    @After("@crudWorkflow")
    public void cleanupCreatedIssue(Scenario scenario) {
        Integer issueIid = context.getIssueIid();

        if (issueIid == null || context.isIssueDeleted()) {
            return;
        }

        try {
            Response response = client.deleteIssue(issueIid);
            int statusCode = response.statusCode();

            if (statusCode == 200 || statusCode == 204 || statusCode == 404) {
                LOGGER.info("Cleanup completed for issue IID {} with status {}", issueIid, statusCode);
                return;
            }

            scenario.log("Cleanup did not return an expected status for issue IID " + issueIid
                    + ": " + statusCode + ". Body: " + response.asPrettyString());
        } catch (Exception cleanupError) {
            scenario.log("Cleanup failed for issue IID " + issueIid + ": " + cleanupError.getMessage());
            LOGGER.warn("Cleanup failed for issue IID {}", issueIid, cleanupError);
        }
    }
}
