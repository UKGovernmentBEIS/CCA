# UK CCA - Web

## General

This project is intended as a "skeleton" application for jumpstarting new DESNZ UI projects.

## Installation

### Prerequisites

In order to successfully build and run the project you should first install the
local dependencies:

```bash
yarn install
```

## Dependencies

The web application relies on two projects.

### The `govuk-components` library

is an Angular implementation of the [GDS components](https://design-system.service.gov.uk/components/)
and can be found [here](projects/govuk-components/). It is an application agnostic approach, clean of business-specific implementations.
Any custom components based on GDS are built on top of the `govuk-components` library and the `govuk-frontend` library's CSS.

### The `cca-api` library

is an OpenAPI generated library that contains services and models related to the CCA API and can be found [here](projects/cca-api/).

## Build

The libraries of the project must be build first if there are new versions. To build both libraries at once, run the custom script:

```shell script
yarn prebuild
```

If you want to build only the `govuk-components` library you can run:

```shell script
yarn build:govuk-components
```

If you want to build only the `cca-api` library you can run:

```shell script
yarn build:cca-api
```

The build artifacts will be stored in the `dist/` directory.

To build the application you should run the following command:

```shell script
yarn build
```

In order to build the project for the production environment:

```shell script
yarn build:production
```

## Development server

To start the application using the Angular's development server run:

```shell script
yarn start
```

This will build and serve the application under [http://localhost:4200](http://localhost:4200).
The app will automatically rebuild and reload if you change any of the source files.

## Analyze Bundles

To start analyzing bundles run:

```shell script
yarn analyze
```

This will build the app, generate all `source-maps` needed by `source-map-explorer` and open a browser tab where you can view all bundles.

![source-map-explorer](images/source-map-explorer.png)

## How to run Sonarqube locally

1. Make sure you have sonarqube running locally. If you're running the docker compose from the [uk-cca-env-development](https://git.trasys.gr/bitbucket/projects/UKCCA/repos/uk-cca-env-development) this should be enough.
2. Log into your local sonarqube running at http://localhost:9000. If this is your first time you'll be prompted to change your default credentials (initial ones are admin-admin).
3. Create a sonarqube project manually. Project display name, project key and main branch name do not matter. For consistency purposes you can type `uk-cca-web`, `uk-cca-web` and `master` respectively.
4. Choose `global settings` in the next screen and click `Create Project`.
5. Click the `Locally` option to set up sonar-scanner locally.
6. Generate a token and paste it in the `sonar.login` variable in the `sonar-project.properties` file found in the root of this repository.
7. Change the sonarqube password in `package.json` file in the root of this repository. This should be found under the `scripts` section at the `sonar:local` script.
8. Run `yarn sonar:local`. After completion you should be able to see the results at http://localhost:9000/projects, under the sonarqube project you just created.

## Unit tests

When developing for CCA web application try to keep the coverage in a good level.

To execute the application's unit tests run:

```shell script
yarn test:frontend
```

To execute the govuk-components library tests you can run:

```shell script
yarn test:govuk-components
```

If you want to find out the coverage of the both the application and the govuk-compoents
library you can apply the `:coverage` flag in the above commands, e.g.:

```shell script
yarn test:frontend:coverage
```

The coverage can the be found in the command line or in the `coverage/` folder which
will be generated inside the parent folder of each project.

## Functional Tests

Typically, each Jira Story represents one Confluence Page.
Confluence pages include the Scenarios that the app has to cover for a certain feature to be considered complete.

In CCA, we create specific files that test the Confluence Scenarios exclusively. We use [RouterTestingHarness](https://angular.dev/api/router/testing/RouterTestingHarness) and [Testing Library](https://testing-library.com/), in order to approach the process
from a black-box testing perspective and making sure that every Scenario is clearly covered in our tests.

**Each functional test suite should cover one feature set, as described by the scenarios in the respective Confluence Page.**

### Technical details

We use the [RouterTestingHarness](https://angular.dev/api/router/testing/RouterTestingHarness) for implementing our functional tests. [Here are some examples for the RouterTestingHarness in Angular](https://dev.to/this-is-angular/testing-routed-components-with-routertestingharness-22dl).
RouterTestingHarness, combined with TestingLibrary, allows us to test user-like
behavior, in coordination with some real routing inside our testing environment.

Here are some common caveats to keep in mind, while working with the RouterTestingHarness:

- When navigating to a new page, make sure to run `await harness.fixture.whenStable()`. This code block give time to the Angular TestingLibrary
  environment to run any pending change detection cycles as well as send any http requests so that the httpTestingController can catch them.
- Use `httpTestingController` to mock requests and responses. It's extremely convenient and it does not mess with spies and mock implementations
  of services.
- In general, when your tests do not render the template you would expect, use `screen.debug()` and play with `harness.fixture.detectChanges()`
  along with `await harness.fixture.whenStable()`.

### Example

The indicative example for a functional test suite is found under `src/app/sectors/specs/invite-sector.spec.ts`

Note: For password related tests, take a look at `src/app/invitation/sector-user-invitation/specs/sector-user-invitation.spec.ts`

## Release plan and changelog generation

Every two weeks by the end of each sprint a new minor release should be
up and running. There is also a need to generate the changelog for each release.
For these purposes there are three scripts in the `package.json` file

### Patch release

To generate a new patch release you should run:

```bash
yarn release:patch
```

### Minor release

To generate a new minor release you should run:

```bash
yarn release:minor
```

### Major release

To generate a new major release you should run:

```bash
yarn release:major
```

The changelog is generated automatically and can be found under the [CHANGELOG.md](CHANGELOG.md) file.
