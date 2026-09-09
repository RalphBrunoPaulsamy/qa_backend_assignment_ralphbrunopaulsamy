@crudWorkflow
Feature: GitLab issue CRUD workflow

  Scenario: Create, get, update and delete an issue
    Given I create a unique GitLab issue
    Then the issue should be created successfully

    When I get the created issue
    Then the issue should be returned successfully
    And the issue title should match the created title

    When I update the created issue with a new title
    Then the issue should be updated successfully

    When I get the created issue again
    Then the issue title should match the updated title

    When I delete the created issue
    Then the issue should be deleted successfully

    When I get the deleted issue
    Then the issue should not be found
