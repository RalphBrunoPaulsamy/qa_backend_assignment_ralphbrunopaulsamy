package com.abnamro.gitlab.stepdefinitions;

import com.abnamro.gitlab.pages.DeleteIssuePage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteIssueSteps {

    private final DeleteIssuePage deleteIssuePage = new DeleteIssuePage();

    @When("I delete the issue")
    public void iDeleteTheIssue() {
        IssueScenarioState.response = deleteIssuePage.delete(IssueScenarioState.issueIid);
    }

    @Then("the delete response status is successful")
    public void theDeleteResponseStatusIsSuccessful() {
        int statusCode = IssueScenarioState.response.statusCode();
        assertTrue(statusCode == 200 || statusCode == 204,
            "Delete should return 200 or 204 for issue IID "
                + IssueScenarioState.issueIid + ", got: " + statusCode);
    }
}
