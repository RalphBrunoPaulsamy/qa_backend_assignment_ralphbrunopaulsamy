package com.abnamro.gitlab.stepdefinitions;

import com.abnamro.gitlab.client.GitLabIssueClient;
import com.abnamro.gitlab.model.CreateIssueRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdgeCaseSteps {

    private final GitLabIssueClient issueClient = new GitLabIssueClient();
    private Response response;
    private Response[] responses;

    @When("I create an issue with a 200-character title")
    public void iCreateAnIssueWithA200CharacterTitle() {
        String title = "A".repeat(200);
        response = issueClient.createIssue(CreateIssueRequest.builder().title(title).build());
    }

    @Then("the edge case issue response status is {int}")
    public void theIssueResponseStatusIs(int expectedStatus) {
        int actualStatus = response.statusCode();
        assertEquals(expectedStatus, actualStatus,
                "Expected issue response status " + expectedStatus + ", got: " + actualStatus);
    }

    @And("the issue response title has {int} characters")
    public void theIssueResponseTitleHasCharacters(int expectedLength) {
        String title = response.jsonPath().getString("title");
        assertEquals(expectedLength, title.length(),
                "Expected issue title length " + expectedLength + ", got: " + title.length());
    }

    @When("I create an issue with special characters in the title")
    public void iCreateAnIssueWithSpecialCharactersInTheTitle() {
        response = issueClient.createIssue(CreateIssueRequest.builder()
                .title("Issue @#$%^&*() with [brackets] and {braces}")
                .description("Testing special chars: <html>test</html>")
                .build());
    }

    @And("the issue response title contains {string}")
    public void theIssueResponseTitleContains(String expectedText) {
        String title = response.jsonPath().getString("title");
        assertTrue(title.contains(expectedText),
                "Expected issue title to contain '" + expectedText + "', got: '" + title + "'");
    }

    @And("the issue response state is {string}")
    public void theIssueResponseStateIs(String expectedState) {
        String actualState = response.jsonPath().getString("state");
        assertEquals(expectedState, actualState,
                "Expected issue state '" + expectedState + "', got: '" + actualState + "'");
    }

    @When("I create an issue with Unicode characters in the title")
    public void iCreateAnIssueWithUnicodeCharactersInTheTitle() {
        response = issueClient.createIssue(CreateIssueRequest.builder()
                .title("Issue with émojis 🚀 and ñ characters")
                .build());
    }

    @When("I create an issue with a long description")
    public void iCreateAnIssueWithALongDescription() {
        response = issueClient.createIssue(CreateIssueRequest.builder()
                .title("Long Description Test")
                .description("Description ".repeat(100))
                .build());
    }

    @And("the issue response contains a description")
    public void theIssueResponseContainsADescription() {
        String description = response.jsonPath().getString("description");
        assertNotNull(description, "Expected the issue response to contain a description");
    }

    @When("I create an issue with whitespace title {string}")
    public void iCreateAnIssueWithWhitespaceTitle(String title) {
        response = issueClient.createIssue(CreateIssueRequest.builder().title(title).build());
    }

    @When("I create an issue without a description")
    public void iCreateAnIssueWithoutADescription() {
        response = issueClient.createIssue(CreateIssueRequest.builder()
                .title("Issue with No Description")
                .description(null)
                .build());
    }

    @And("the issue response title is {string}")
    public void theIssueResponseTitleIs(String expectedTitle) {
        String actualTitle = response.jsonPath().getString("title");
        assertEquals(expectedTitle, actualTitle,
                "Expected issue title '" + expectedTitle + "', got: '" + actualTitle + "'");
    }

    @When("I create an issue for field verification")
    public void iCreateAnIssueForFieldVerification() {
        response = issueClient.createIssue(CreateIssueRequest.builder()
                .title("Field Verification Test")
                .build());
    }

    @And("the issue response contains all required fields")
    public void theIssueResponseContainsAllRequiredFields() {
        response.then()
                .body("id", notNullValue())
                .body("iid", notNullValue())
                .body("title", notNullValue())
                .body("state", notNullValue())
                .body("created_at", notNullValue())
                .body("updated_at", notNullValue())
                .body("web_url", notNullValue());
    }

    @When("I create three issues for state verification")
    public void iCreateThreeIssuesForStateVerification() {
        CreateIssueRequest request = CreateIssueRequest.builder()
                .title("State Verification Test")
                .build();
        responses = new Response[3];
        for (int index = 0; index < responses.length; index++) {
            responses[index] = issueClient.createIssue(request);
        }
    }

    @Then("every edge case issue response status is {int}")
    public void everyIssueResponseStatusIs(int expectedStatus) {
        for (Response currentResponse : responses) {
            int actualStatus = currentResponse.statusCode();
            assertEquals(expectedStatus, actualStatus,
                    "Expected every issue response status to be " + expectedStatus + ", got: " + actualStatus);
        }
    }

    @And("every issue response state is {string}")
    public void everyIssueResponseStateIs(String expectedState) {
        for (Response currentResponse : responses) {
            String actualState = currentResponse.jsonPath().getString("state");
            assertEquals(expectedState, actualState,
                    "Expected every issue state to be '" + expectedState + "', got: '" + actualState + "'");
        }
    }
}
