package com.abnamro.gitlab.stepdefinitions;

import com.abnamro.gitlab.config.TestConfig;
import com.abnamro.gitlab.model.CreateIssueRequest;
import com.abnamro.gitlab.pages.CreateIssuePage;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateIssueSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateIssueSteps.class);

    private final CreateIssuePage createIssuePage = new CreateIssuePage();

    @Before
    public void setUp() {
        IssueScenarioState.response = null;
        IssueScenarioState.issueIid = null;
    }

    @When("I create a new issue titled {string}")
    public void iCreateANewIssueTitled(String title) {
        String uniqueTitle = title + " - " + UUID.randomUUID();
        IssueScenarioState.response = createIssuePage.create(CreateIssueRequest.builder()
                .title(uniqueTitle)
                .description("Created by the Cucumber API test")
                .build());
        IssueScenarioState.issueIid = IssueScenarioState.response.jsonPath().getInt("iid");

        if (IssueScenarioState.response.statusCode() == 201) {
            TestConfig.saveIssueIid(IssueScenarioState.issueIid);
        }

        LOGGER.info("Created GitLab issue IID {}. It remains available for follow-up API tests.", IssueScenarioState.issueIid);
    }

    @Then("the create issue response status is {int}")
    public void theCreateIssueResponseStatusIs(int expectedStatus) {
        int actualStatus = IssueScenarioState.response.statusCode();
        assertEquals(expectedStatus, actualStatus,
                "Expected create issue response status " + expectedStatus + ", got: " + actualStatus);
    }

    @And("the created issue title starts with {string}")
    public void theCreatedIssueTitleStartsWith(String expectedTitle) {
        String actualTitle = IssueScenarioState.response.jsonPath().getString("title");
        assertTrue(actualTitle.startsWith(expectedTitle),
                "Expected created issue title to start with '" + expectedTitle + "', got: '" + actualTitle + "'");
    }
}
