@createAPITest
Feature: Create a GitLab issue
  I want to create an issue through the GitLab API

  Scenario: Create an issue with a title and description
    When I create a new issue titled "Cucumber Create Test"
    Then the create issue response status is 201
    And the created issue title starts with "Cucumber Create Test"