@getAPITest
Feature: Get a GitLab issue
  I want to retrieve an existing issue by IID
  So that its details can be verified

  Scenario: Get the configured issue
    Given an existing GitLab issue is configured
    When I get the issue
    Then the issue response status is 200