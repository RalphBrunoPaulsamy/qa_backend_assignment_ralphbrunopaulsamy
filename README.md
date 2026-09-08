# GitLab API Test Automation

BDD-Automation tests for the GitLab Issues API.

## API Workflow

The four endpoint features cover this workflow:

```text
POST /projects/:id/issues              create_issue.feature
GET  /projects/:id/issues/:issue_iid   get_issue.feature
PUT  /projects/:id/issues/:issue_iid   update_issue.feature
DELETE /projects/:id/issues/:issue_iid delete_issue.feature
```

The create scenario generates a unique issue title, leaves the created issue in GitLab, and saves its returned IID to `.env.local`. GET, PUT, and DELETE then load that same IID in separate Maven processes. DELETE permanently removes that issue, so run it last.

## Project Structure

```text
src/test/java/com/abnamro/gitlab/
├── runner/
│   └── CucumberTest.java              # Runs all features; use tags to select one
├── stepdefinitions/
│   ├── CreateIssueSteps.java          # Create endpoint steps
│   ├── GetIssueSteps.java             # Get endpoint steps
│   ├── UpdateIssueSteps.java          # Update endpoint steps
│   ├── DeleteIssueSteps.java          # Delete endpoint steps
│   └── EdgeCaseSteps.java             # Edge-case endpoint steps
├── pages/
│   ├── CreateIssuePage.java           # POST issue operations
│   ├── GetIssuePage.java              # GET issue operations
│   ├── UpdateIssuePage.java           # PUT issue operations
│   └── DeleteIssuePage.java           # DELETE issue operations
├── client/
│   └── GitLabIssueClient.java         # REST Assured endpoint client
├── config/
│   └── TestConfig.java                # Environment and request setup
└── model/                             # Request and response models

src/test/resources/features/
├── create_issue.feature
├── get_issue.feature
├── update_issue.feature
├── delete_issue.feature
└── edge_cases.feature
```

## Configuration

Create `.env.local` from `.env.example` and provide a valid token, project ID, and existing issue IID:

```env
GITLAB_TOKEN=your-personal-access-token
GITLAB_PROJECT_ID=your-project-id
GITLAB_ISSUE_IID=created-issue-iid
```

`TestConfig` uses shell environment variables first and `.env.local` as a local fallback. `.env.local` is ignored by Git and must never be committed.

## Prerequisites

- Java 17+
- Maven 3.6+
- GitLab token with `api` scope
- An existing issue for GET, PUT, and DELETE scenarios

Install dependencies and compile:

```bash
mvn clean test-compile
```

## Run From The Runner

There is one runner class. Run all features through it:

```bash
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest test
```

Run DELETE only after confirming that deleting `GITLAB_ISSUE_IID` is intended.

## Run From Terminal Tags

Use the general runner and select a feature by its tag:

```bash
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest -Dcucumber.filter.tags="@createAPITest" test
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest -Dcucumber.filter.tags="@getAPITest" test
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest -Dcucumber.filter.tags="@updateAPITest" test
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest -Dcucumber.filter.tags="@deleteAPITest" test
```

The same tag commands work with credentials loaded from `.env.local` without exporting them first because `TestConfig` loads the file:

```bash
env -u GITLAB_TOKEN -u GITLAB_PROJECT_ID -u GITLAB_ISSUE_IID \
mvn -q -Dtest=com.abnamro.gitlab.runner.CucumberTest -Dcucumber.filter.tags="@getAPITest" test
```

## Coding Standards

- Keep HTTP request construction in `GitLabIssueClient`.
- Keep configuration and authentication in `TestConfig`.
- Keep request payloads in model classes.
- Keep business-readable behavior in `.feature` files.
- Keep HTTP assertions in shared step definitions.
- Use one clear scenario per endpoint feature unless a scenario adds distinct coverage.
- Use unique test data for POST requests and record the returned IID for follow-up scenarios.
- Do not commit tokens, `.env.local`, build output, or test logs.

## Validation

Compile without calling GitLab:

```bash
mvn -q clean test-compile
```

Run a single feature using its runner or tag command above. Live tests require network access and valid GitLab credentials.