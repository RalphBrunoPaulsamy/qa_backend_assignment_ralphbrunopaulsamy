@deleteAPITest
Feature: Delete a GitLab issue
  I want to delete an existing issue by IID
  So that it is removed from the project

  Scenario: Delete the configured issue
    Given an existing GitLab issue is configured
    When I delete the issue
    Then the delete response status is successful