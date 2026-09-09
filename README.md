# GitLab API Test Automation

BDD API test automation for the GitLab Issues API using **Java 17, Maven, Cucumber, JUnit 5, REST Assured, and PicoContainer**.

## Overview

This project validates the complete GitLab Issue CRUD lifecycle through a self-contained Cucumber scenario:

1. Create an issue.
2. Retrieve and validate the issue.
3. Update the issue.
4. Retrieve and validate the update.
5. Delete the issue.
6. Verify the deleted issue returns `404 Not Found`.

Each test run creates a uniquely named temporary issue and stores its project-scoped `iid` in `IssueScenarioContext`. No pre-existing issue IID is required.

If the scenario fails before deletion, `IssueCleanupHooks` attempts to safely remove the issue created during that scenario.

## API Workflow

```text
Create Issue
    ↓
Store returned IID
    ↓
Get & Validate Issue
    ↓
Update Issue
    ↓
Get & Validate Update
    ↓
Delete Issue
    ↓
GET Deleted Issue → 404
    ↓
Cleanup Hook (if required)
```

## Project Structure

```text
src/test/java/com/abnamro/gitlab/
├── client/
│   └── GitLabIssueClient.java       # REST Assured API client
├── config/
│   └── TestConfig.java              # Configuration and authentication
├── context/
│   └── IssueScenarioContext.java    # Scenario-scoped test state
├── hooks/
│   └── IssueCleanupHooks.java       # Cleanup after incomplete workflows
├── model/
│   └── ...                          # Request/response models
├── runner/
│   └── CucumberTest.java            # Cucumber JUnit Platform runner
└── stepdefinitions/
    └── IssueCrudSteps.java          # CRUD step definitions

src/test/resources/features/
└── issue_crud_workflow.feature      # CRUD lifecycle scenario
```

## Prerequisites

* Java 17+
* Maven 3.6+
* GitLab Personal Access Token with `api` scope
* Access to a GitLab project with permission to create, update, and delete issues
* A dedicated GitLab test project is recommended

## Configuration

The tests require:

```env
GITLAB_TOKEN=your-personal-access-token
GITLAB_PROJECT_ID=your-project-id
```

Configuration is resolved in the following order:

1. Environment variables
2. Local `.env.local` file

Create local configuration from the provided template:

```bash
cp .env.example .env.local
```

Update `.env.local`:

```env
GITLAB_TOKEN=your-personal-access-token
GITLAB_PROJECT_ID=your-gitlab-test-project-id
```

`.env.local` is ignored by Git and must never be committed.

For CI/CD, provide these values through secure environment variables or secret variables instead.

## Running the Tests

Compile without calling GitLab:

```bash
mvn -q clean test-compile
```

Run the full test suite:

```bash
source ./.env.local && mvn -q clean test
```

Run without Maven quiet mode for troubleshooting:

```bash
source ./.env.local && mvn clean test
```

Run only the CRUD workflow:

```bash
source ./.env.local && \
mvn -q \
  -Dtest=com.abnamro.gitlab.runner.CucumberTest \
  -Dcucumber.filter.tags='@crudWorkflow' \
  test
```

A successful execution should end with:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Test Safety

The automation creates and deletes GitLab issues. Use a dedicated test project rather than a production or business-critical project.

Safeguards include:

* Unique UUID-based issue titles for every run.
* Issue IID stored only in scenario-scoped memory.
* Only the issue created by the current scenario is deleted.
* An `@After` hook performs cleanup if the normal workflow does not complete deletion.
* Cleanup accepts `200`, `204`, and `404` as valid outcomes.
* Credentials are loaded from local or secure environment configuration.

## Design Principles

* HTTP request construction is isolated in `GitLabIssueClient`.
* Configuration and authentication are handled by `TestConfig`.
* Scenario state is maintained in `IssueScenarioContext`.
* Cleanup logic is isolated in `IssueCleanupHooks`.
* Request and response payloads are represented by model classes where applicable.
* Business behaviour remains readable in Cucumber feature files.
* Tests avoid static or globally shared mutable issue state.

## Reviewer Setup

Clone the repository and create local configuration:

```bash
git clone <repository-url>
cd qa_backend_assignment_ralphbrunopaulsamy
cp .env.example .env.local
```

Add GitLab test-project credentials to `.env.local`:

```env
GITLAB_TOKEN=your-gitlab-personal-access-token
GITLAB_PROJECT_ID=your-gitlab-test-project-id
```

Then run:

```bash
source ./.env.local && mvn -q clean test
```

Alternatively, use environment variables:

```bash
export GITLAB_TOKEN="your-gitlab-personal-access-token"
export GITLAB_PROJECT_ID="your-gitlab-test-project-id"
mvn -q clean test
```

## Reports

Maven Surefire reports are generated under:

```text
target/surefire-reports/
```

If Cucumber reporting plugins are enabled, additional reports are generated under `target/`.

## Troubleshooting

### `401 Unauthorized`

Verify that `GITLAB_TOKEN` is valid, has `api` scope, and is correctly loaded.

### `403 Forbidden`

Verify that the token has permission to create, update, and delete issues in the configured project.

### `404 Project Not Found`

Verify `GITLAB_PROJECT_ID` and confirm that the token has access to the project.

### Failure after issue creation

Review the Maven/Cucumber reports. If an IID was successfully returned during creation, `IssueCleanupHooks` should attempt to remove the temporary issue.

## Repository Hygiene

Do not commit credentials, local configuration, reports, logs, or build output.

Recommended `.gitignore` entries:

```gitignore
.env.local
target/
logs/
*.log
.DS_Store
.idea/
.vscode/
```

Verify that local credentials are ignored:

```bash
git check-ignore -v .env.local
```
