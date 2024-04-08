# UK NETZ API application

The UK NETZ API is a Java(SpringBoot) application.

## Structure

## Running the application

You can run the Spring Boot application by typing:

    $ mvn clean spring-boot:run

or

    $ ./_runme.sh

You can then access the final jar file that contains the API here :

    uk-netz-app-api\target

For the build to succeed uk-netz-swagger-coverage-maven-plugin must have been built prior to building the UK NETZ API application

## REST API Documentation

The API is documented using Swagger 3.

After running the application, the documentation is available here:

- http://localhost:8082/api/swagger-ui/index.html (UI)
- http://localhost:8082/api/v3/api-docs (JSON)

### Actuator

Actuator can be accessed in:

```
http://localhost:8082/actuator
```

Note that the actuator is not secured by default because it is not meant to be
exposed to the public internet but only be accessible from the internal
network.

### Feature flags

Feature flag feature-flag.disabledWorkflows for disabling workflows has been implemented and can take as a value comma-separated workflows(RequestType) that need to be disabled. (Only user initiated workflows are taken under consideration)

### Logging

By default, logging to json format is configured through log4j2-json.xml but default console logging can be chosen by setting LOG4J2_CONFIG_FILE env var to log4j2-local.xml.

Unauthenticated API calls are not logged(RestLoggingFilter is applied after security filters in order to be able to inject user related info in authenticated API calls) so explicit logging should be added for these calls.

## Camunda admin

### REST API

Camunda rest is used to manage camunda processes. It is unauthenticated and can be accessed at /api/admin/camunda-api.

Documentation can be found at
- https://docs.camunda.org/manual/latest/reference/rest/

### WEB APP

Camunda webapp consists of 3 different web apps:
- cockpit: an administration interface for processes and decisions
- tasklist: provides an interface to process user tasks
- admin: is used to administer users, groups and their authorizations 

It is authenticated through keycloak's master realm and can be accessed at /api/admin/camunda-web.

Documentation can be found at
- https://camunda.com/platform-7/cockpit/
- https://camunda.com/platform/tasklist/
- https://github.com/camunda/camunda-bpm-platform/tree/master/webapps


# Use in a New project
- Change banner.txt
- rename package from netz to the new project name
- after the packages have been updated to the new name space, global search with 'netz' keyword should be performed to update where necessary (e.g RequestTaskMapper, hypersistence-utils.properties, log4j2-json.xml)
- Some Dummy enums entries have been created to be able to test them. They should be replaced as soon as a real entry is created. These entries can be found by searching the 'DUMMY' keyword
- Some dummy implementations have been provided in NETZ for the application to be able to deploy. These will have to be removed:
  
  - TestApprovedAccountQueryService
  - TestItemResponseService
  - TestDocumentTemplateCommonParamsAbstractProvider
- Dockerfile
- Jenkinsfile
- docker-compose.yml


## Abstractions provided

### Common
- EmissionTradingScheme

### Competent Authority
- a script similar to competent_authority.xml with the correct emails will be needed

### Account
- AccountStatus
- Account entity
- AccountBaseRepository
- ApprovedAccountQueryService (will use AccountBaseRepository), remove TestApprovedAccountQueryService

### Authorisation
- review au_authority.xml, au_authorization_rules.xml and keep only necessary entries

### Request
- RequestType: public static methods
- RequestTaskType: public static methods
- RequestCreateByAccountValidator, ProcessRequestCreateAspect: filter account create flow?
- DocumentTemplateCommonParamsProvider, remove TestDocumentTemplateCommonParamsAbstractProvider
- ItemResponseService, remove TestItemResponseService
- ItemDTO

### MiReports
- Repositories for predefined reports (e.g. AccountAssignedRegulatorSiteContactsRepository)
- update mi_reports_views sql to include all project related db tables
- a script to include all applicable reports per CA in mi_report table

### Notification
- database entries with correct content for all notifications (NotificationTemplateName)

### application.properties
- spring.datasource.name: same with POSTGRES_DB in docker_compose.yml
- report-datasource
- jwt.claim.audience

- NETZ_APP_API_CLIENT_SECRET change name and sync with keycloak variable

- keycloak.realm will be uk-pmrv if sso is needed
- keycloak.client-id
- change name and sync with keycloak *_APP_API_CLIENT_SECRET

- cloudwatch.namespace

- UI_ANALYTICS_MEASUREMENTID
- UI_ANALYTICS_PROPERTYID
- API_DB_NAME: This is new env var and must be also set in all envs from devops team