package com.abnamro.gitlab.stepdefinitions;

import com.abnamro.gitlab.client.GitLabIssueClient;
import com.abnamro.gitlab.context.IssueScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class IssueCrudSteps {

    private final IssueScenarioContext context;
    private final GitLabIssueClient client;

    public IssueCrudSteps(IssueScenarioContext context, GitLabIssueClient client) {
        this.context = context;
        this.client = client;
    }

    @Given("I create a unique GitLab issue")
    public void createUniqueIssue() {
        String title = "AUTOMATION_CRUD_" + UUID.randomUUID();

        Response response = client.createIssue(title);
        response.then().statusCode(201);

        Integer iid = response.jsonPath().getInt("iid");

        context.setIssueIid(iid);
        context.setCreatedTitle(title);
        context.setLastResponse(response);
    }

    @Then("the issue should be created successfully")
    public void issueShouldBeCreatedSuccessfully() {
        Response response = context.getLastResponse();

        assertThat(response, is(notNullValue()));
        response.then().statusCode(201);
        assertThat(response.jsonPath().getInt("iid"), is(notNullValue()));
        assertThat(response.jsonPath().getString("title"), is(context.getCreatedTitle()));
        assertThat(response.jsonPath().getString("state"), is("opened"));
    }

    @When("I get the created issue")
    public void getCreatedIssue() {
        context.setLastResponse(client.getIssue(context.getIssueIid()));
    }

    @Then("the issue should be returned successfully")
    public void issueShouldBeReturnedSuccessfully() {
        context.getLastResponse().then().statusCode(200);
    }

    @Then("the issue title should match the created title")
    public void issueTitleShouldMatchCreatedTitle() {
        String actualTitle = context.getLastResponse().jsonPath().getString("title");
        assertThat(actualTitle, is(context.getCreatedTitle()));
    }

    @When("I update the created issue with a new title")
    public void updateCreatedIssue() {
        String updatedTitle = context.getCreatedTitle() + " - updated";

        context.setUpdatedTitle(updatedTitle);
        context.setLastResponse(client.updateIssue(context.getIssueIid(), updatedTitle));
    }

    @Then("the issue should be updated successfully")
    public void issueShouldBeUpdatedSuccessfully() {
        Response response = context.getLastResponse();

        response.then().statusCode(200);
        assertThat(response.jsonPath().getString("title"), is(context.getUpdatedTitle()));
    }

    @When("I get the created issue again")
    public void getCreatedIssueAgain() {
        context.setLastResponse(client.getIssue(context.getIssueIid()));
    }

    @Then("the issue title should match the updated title")
    public void issueTitleShouldMatchUpdatedTitle() {
        String actualTitle = context.getLastResponse().jsonPath().getString("title");
        assertThat(actualTitle, is(context.getUpdatedTitle()));
    }

    @When("I delete the created issue")
    public void deleteCreatedIssue() {
        context.setLastResponse(client.deleteIssue(context.getIssueIid()));
    }

    @Then("the issue should be deleted successfully")
    public void issueShouldBeDeletedSuccessfully() {
        assertThat(context.getLastResponse().statusCode(), anyOf(is(200), is(204)));
        context.markIssueDeleted();
    }

    @When("I get the deleted issue")
    public void getDeletedIssue() {
        context.setLastResponse(client.getIssue(context.getIssueIid()));
    }

    @Then("the issue should not be found")
    public void issueShouldNotBeFound() {
        context.getLastResponse().then().statusCode(404);
    }
}
