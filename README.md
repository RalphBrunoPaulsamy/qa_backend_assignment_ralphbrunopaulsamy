# GitLab API Test Automation

BDD API automation tests for the GitLab Issues API using Java 17, Maven,
Cucumber, JUnit 5, REST Assured, and PicoContainer.

## Overview

This project validates the complete GitLab Issue CRUD lifecycle in one
self-contained end-to-end Cucumber scenario:

- Create a GitLab issue.
- Retrieve the created issue.
- Update the issue.
- Retrieve the issue again and validate the update.
- Delete the issue.
- Verify that the deleted issue returns `404 Not Found`.

The automation does not rely on a pre-existing GitLab issue IID. Each run
creates a unique temporary issue and keeps its returned project-scoped IID in
memory for the lifetime of the scenario.

## API Workflow

```text
Start scenario
  ↓
Generate unique UUID-based issue title
  ↓
POST create issue
  ↓
Extract response.iid into scenario context
  ↓
GET /issues/{iid}
  ↓
Validate created title and state
  ↓
PUT /issues/{iid}
  ↓
Validate update response
  ↓
GET /issues/{iid}
  ↓
Validate updated title
  ↓
DELETE /issues/{iid}
  ↓
GET /issues/{iid}
  ↓
Expect 404 Not Found
  ↓
After hook performs safe cleanup only if a previous step failed
  ↓
Publish test report
```

The scenario creates a unique GitLab issue, extracts the returned `iid`, and
stores it in `IssueScenarioContext`.

The IID is passed between Create, Get, Update, and Delete API calls during the
same Cucumber scenario. It is not stored in `.env.local`, committed to Git, or
reused across test runs.

The DELETE operation is intentionally executed near the end of the workflow.
If a scenario fails before deletion, `IssueCleanupHooks` attempts to remove
only the issue created by that scenario.

A cleanup response of `404 Not Found` is treated as valid because the issue
may already have been deleted by the workflow.

## Project Structure

```text
src/test/java/com/abnamro/gitlab/
├── client/
│   └── GitLabIssueClient.java          # REST Assured GitLab API client
├── config/
│   └── TestConfig.java                 # Environment, authentication, .env loading
├── context/
│   └── IssueScenarioContext.java       # Scenario-scoped IID/title/response state
├── hooks/
│   └── IssueCleanupHooks.java          # Cleanup for incomplete CRUD workflows
├── model/
│   └── ...                             # Request and response models
├── runner/
│   └── CucumberTest.java               # Cucumber JUnit Platform suite runner
└── stepdefinitions/
    └── IssueCrudSteps.java             # CRUD step definitions

src/test/resources/features/
└── issue_crud_workflow.feature         # Complete CRUD lifecycle scenario
```

## Configuration

The test requires two runtime configuration values:

```env
GITLAB_TOKEN=your-personal-access-token
GITLAB_PROJECT_ID=your-project-id
```

`TestConfig` resolves configuration in this order:

1. Shell environment variables.
2. Local `.env.local` file.

The `.env.local` file is ignored by Git and must never be committed.

The test does not require `GITLAB_ISSUE_IID`.

### Local configuration

Create `.env.local` from the template:

```bash
cp .env.example .env.local
```

Then update `.env.local`:

```env
GITLAB_TOKEN=your-personal-access-token
GITLAB_PROJECT_ID=your-gitlab-test-project-id
```

### Environment-variable configuration

Alternatively, provide values directly in the shell:

```bash
export GITLAB_TOKEN="your-personal-access-token"
export GITLAB_PROJECT_ID="your-gitlab-test-project-id"

mvn -q clean test
```

This approach is recommended for CI/CD pipelines, where values should be
stored as masked or secret variables.

## Prerequisites

- Java 17 or later.
- Maven 3.6 or later.
- GitLab Personal Access Token with `api` scope.
- Access to the configured GitLab project.
- Permission to create, update, and delete issues in that project.
- A dedicated GitLab test project is strongly recommended.

## Compile

Compile the project without calling GitLab:

```bash
mvn -q clean test-compile
```

## Run the Full Test Suite

Load local configuration and run the complete active test suite:

```bash
source ./.env.local && \
mvn -q clean test
```

The current active suite contains the full CRUD workflow.

Run without Maven quiet mode when troubleshooting:

```bash
source ./.env.local && \
mvn clean test
```

## Run the CRUD Workflow by Tag

Run only the CRUD scenario explicitly:

```bash
source ./.env.local && \
mvn -q \
  -Dtest=com.abnamro.gitlab.runner.CucumberTest \
  -Dcucumber.filter.tags='@crudWorkflow' \
  test
```

A successful execution should end with output similar to:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Test Safety

The workflow creates and deletes GitLab issues.

Use a dedicated GitLab test project. Do not run this suite against a production
project or a project containing business-critical issues unless you have
explicit approval.

The test applies these safeguards:

- Every created issue has a unique UUID-based title, for example:
  `AUTOMATION_CRUD_<uuid>`.
- The created issue IID is stored only in scenario-scoped memory.
- Only the issue created by the current scenario can be deleted.
- The normal workflow deletes the issue before scenario completion.
- An `@After` hook attempts cleanup only if the workflow has not already
  deleted the created issue.
- The cleanup hook accepts HTTP `200`, `204`, and `404` as valid outcomes.
- GitLab token and project ID are loaded from local environment configuration
  or secure environment variables.
- Tokens, `.env.local`, reports, logs, and build output must not be committed.

## Coding Standards

- Keep HTTP request construction in `GitLabIssueClient`.
- Keep configuration and authentication in `TestConfig`.
- Keep scenario-specific mutable state in `IssueScenarioContext`.
- Keep cleanup logic in `IssueCleanupHooks`.
- Keep request and response payloads in model classes where applicable.
- Keep business-readable behaviour in `.feature` files.
- Keep REST Assured assertions in step definitions or shared assertion helpers.
- Use unique data for every Create operation.
- Never use static or globally shared mutable state for issue IDs.
- Do not commit tokens, `.env.local`, `target/`, logs, reports, or test data
  containing sensitive information.

## Reviewer Setup

This repository does not include `.env.local` because it contains local
credentials and is intentionally excluded from Git.

### Option 1: Create a local configuration file

```bash
git clone <repository-url>
cd qa_backend_assignment_ralphbrunopaulsamy

cp .env.example .env.local
```

Edit `.env.local` and add your own GitLab test-project configuration:

```env
GITLAB_TOKEN=your-gitlab-personal-access-token
GITLAB_PROJECT_ID=your-gitlab-test-project-id
```

Then run:

```bash
source ./.env.local && mvn -q clean test
```

### Option 2: Use shell environment variables

```bash
export GITLAB_TOKEN="your-gitlab-personal-access-token"
export GITLAB_PROJECT_ID="your-gitlab-test-project-id"

mvn -q clean test
```

The token requires `api` scope and permission to create, update, and delete
issues in the configured project.

Use a dedicated GitLab test project because the automation creates and deletes
a temporary issue.

## Reports

Maven Surefire reports are generated under:

```text
target/
target/surefire-reports/
```

If Cucumber HTML or JSON plugins are configured in `CucumberTest`, additional
reports are generated under `target/`, for example:

```text
target/cucumber-report.html
target/cucumber-report.json
```

Review reports after each run, especially if a failure occurs during update,
delete, or cleanup.

## Troubleshooting

### Maven cannot find `pom.xml`

Run Maven from the directory that contains `pom.xml`:

```bash
pwd
ls -la pom.xml
```

Then run the test command again.

### `401 Unauthorized`

Confirm that `GITLAB_TOKEN` is valid, has `api` scope, and is loaded:

```bash
source ./.env.local
echo "$GITLAB_PROJECT_ID"
echo "${GITLAB_TOKEN:0:6}******"
```

Do not print the full token.

### `403 Forbidden`

The configured token may not have permission to create, update, or delete
issues in the target GitLab project.

### `404 Project Not Found`

Check `GITLAB_PROJECT_ID` and confirm that the token has access to the
configured project.

### Test fails after creating an issue

Check Maven and Cucumber reports. `IssueCleanupHooks` should attempt to remove
the issue created during a failed scenario, provided the IID was returned by
the Create API call.

## Repository Hygiene

The following files should be ignored by Git:

```gitignore
.env.local
target/
logs/
*.log
.DS_Store
.idea/
.vscode/
```

Verify that `.env.local` is ignored locally:

```bash
git check-ignore -v .env.local
```

This command is for the repository owner’s local security verification.
Reviewers do not need your `.env.local`; they create their own from
`.env.example` or use shell/CI environment variables.