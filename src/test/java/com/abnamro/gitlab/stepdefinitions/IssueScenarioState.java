package com.abnamro.gitlab.stepdefinitions;

import io.restassured.response.Response;

/**
 * Shared scenario state for the split GitLab issue step definitions.
 * The state is intentionally kept in a single holder so individual endpoint
 * step classes can share the current issue IID and last API response.
 */
public final class IssueScenarioState {

    private IssueScenarioState() {
    }

    public static Integer issueIid;
    public static Response response;
}
