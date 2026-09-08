@updateAPITest
Feature: Update a GitLab issue
  As an API test engineer
  I want to update an existing issue by IID
  So that its changed data is returned

  Scenario: Update the configured issue title
    Given an existing GitLab issue is configured
    When I update the issue title to "Updated Fixed IID Issue"
    Then the updated issue response status is 200
    And the issue title is "Updated Fixed IID Issue"