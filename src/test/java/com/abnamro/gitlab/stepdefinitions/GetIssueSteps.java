package com.abnamro.gitlab.stepdefinitions;
import com.abnamro.gitlab.config.TestConfig;
import com.abnamro.gitlab.pages.GetIssuePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class GetIssueSteps {
    private final GetIssuePage getIssuePage = new GetIssuePage();
    @Given("an existing GitLab issue is configured")
    public void anExistingGitLabIssueIsConfigured() {
        IssueScenarioState.issueIid = TestConfig.getIssueIid();
    }
    @When("I get the issue")
    public void iGetTheIssue() {
        IssueScenarioState.response = getIssuePage.get(IssueScenarioState.issueIid);
    }
    @Then("the issue response status is {int}")
    public void theIssueResponseStatusIs(int expectedStatus) {
        int actualStatus = IssueScenarioState.response.statusCode();
        int actualIid = IssueScenarioState.response.jsonPath().getInt("iid");
        assertEquals(expectedStatus, actualStatus,
            "Expected issue response status " + expectedStatus + ", got: " + actualStatus);
        assertEquals(IssueScenarioState.issueIid, actualIid,
            "Expected issue IID " + IssueScenarioState.issueIid + ", got: " + actualIid);
    }
    @Then("the issue title starts with {string}")
    public void theIssueTitleStartsWith(String expectedTitle) {
        String actualTitle = IssueScenarioState.response.jsonPath().getString("title");
        assertTrue(actualTitle.startsWith(expectedTitle),
            "Expected issue title to start with '" + expectedTitle + "', got: '" + actualTitle + "'");
    }
}