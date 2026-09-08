@edgeCaseTest
Feature: GitLab issue edge cases
  Validate boundary values and unusual inputs for issue creation

  Scenario: Create an issue with a very long title
    When I create an issue with a 200-character title
    Then the edge case issue response status is 201
    And the issue response title has 200 characters

  Scenario: Create an issue with special characters
    When I create an issue with special characters in the title
    Then the edge case issue response status is 201
    And the issue response title contains "@#$%"
    And the issue response state is "opened"

  Scenario: Create an issue with Unicode characters
    When I create an issue with Unicode characters in the title
    Then the edge case issue response status is 201

  Scenario: Create an issue with a long description
    When I create an issue with a long description
    Then the edge case issue response status is 201
    And the issue response contains a description

  Scenario Outline: Reject a whitespace-only issue title
    When I create an issue with whitespace title "<title>"
    Then the edge case issue response status is 400

    Examples:
      | title      |
      |            |
      | \n         |
      |   \n      |

  Scenario: Create an issue without a description
    When I create an issue without a description
    Then the edge case issue response status is 201
    And the issue response title is "Issue with No Description"

  Scenario: Verify required issue response fields
    When I create an issue for field verification
    Then the edge case issue response status is 201
    And the issue response contains all required fields

  Scenario: Verify new issues are opened
    When I create three issues for state verification
    Then every edge case issue response status is 201
    And every issue response state is "opened"
