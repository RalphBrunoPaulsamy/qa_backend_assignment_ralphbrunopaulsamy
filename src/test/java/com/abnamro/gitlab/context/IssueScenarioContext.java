package com.abnamro.gitlab.context;

import io.restassured.response.Response;

/**
 * Scenario-scoped storage for the CRUD workflow.
 */
public class IssueScenarioContext {

    private Integer issueIid;
    private String createdTitle;
    private String updatedTitle;
    private Response lastResponse;
    private boolean issueDeleted;

    public Integer getIssueIid() {
        return issueIid;
    }

    public void setIssueIid(Integer issueIid) {
        this.issueIid = issueIid;
    }

    public String getCreatedTitle() {
        return createdTitle;
    }

    public void setCreatedTitle(String createdTitle) {
        this.createdTitle = createdTitle;
    }

    public String getUpdatedTitle() {
        return updatedTitle;
    }

    public void setUpdatedTitle(String updatedTitle) {
        this.updatedTitle = updatedTitle;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    public boolean isIssueDeleted() {
        return issueDeleted;
    }

    public void markIssueDeleted() {
        this.issueDeleted = true;
    }
}
