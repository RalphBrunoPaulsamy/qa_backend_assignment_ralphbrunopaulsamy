package com.abnamro.gitlab.stepdefinitions;

import com.abnamro.gitlab.model.UpdateIssueRequest;
import com.abnamro.gitlab.pages.UpdateIssuePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateIssueSteps {

    private final UpdateIssuePage updateIssuePage = new UpdateIssuePage();

    @When("I update the issue title to {string}")
    public void iUpdateTheIssueTitleTo(String title) {
        IssueScenarioState.response = updateIssuePage.update(
                IssueScenarioState.issueIid,
                UpdateIssueRequest.builder().title(title).build()
        );
    }

    @Then("the updated issue response status is {int}")
    public void theUpdatedIssueResponseStatusIs(int expectedStatus) {
        int actualStatus = IssueScenarioState.response.statusCode();
        int actualIid = IssueScenarioState.response.jsonPath().getInt("iid");
        assertEquals(expectedStatus, actualStatus,
            "Expected updated issue response status " + expectedStatus + ", got: " + actualStatus);
        assertEquals(IssueScenarioState.issueIid, actualIid,
            "Expected updated issue IID " + IssueScenarioState.issueIid + ", got: " + actualIid);
    }

    @And("the issue title is {string}")
    public void theIssueTitleIs(String expectedTitle) {
        String actualTitle = IssueScenarioState.response.jsonPath().getString("title");
        assertEquals(expectedTitle, actualTitle,
            "Expected updated issue title '" + expectedTitle + "', got: '" + actualTitle + "'");
    }
}
