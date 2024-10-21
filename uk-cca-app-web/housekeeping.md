# Housekeeping

This file is a list of chores to do for the repository. Here we write up inconsistencies and stuff that we
want to refactor and did not have the time to.

Issues in this document are not to be taken as is. The team can remove/add entries at will. Whenever we decide to fix one of the issues below, we should always open a corresponding Jira ticket, preferably with the label of `Technical Tasks`.

## Import issues

1. As seen in `withdraw-admin-termination-precontent.component.ts`, we import requests/tasks -> requests/common which should not happend. We need to move all relative logic to the common folders. In this file case, it's clear we need to move the selectors on the common folder.

## Duplicate Code

1. Why do we need a `cca-radio-option` component, since netz already has one?
2. Remove duplicate `PageHeadingComponent`. We have one from shared and one from netz. Use only netz

## Code inconsistencies - Possible refactors

1. Whenever we need to extend the task service logic, we should always do it via the task service and
   avoid exposing methods of api services directly to components. For example, we should refactor `underlying-agreement-activation-task-api.service.ts`. The `notifyOperator` method should be exposed to the component
   via the `UnderlyingAgreementActivationTaskService`. A better implementation can be found on `underlying-agreement-review-task.service.ts`.
2. In underlying agreement application -> subtasks -> baseline and targets, we have a shared `check-your-answers` and `summary` components, whereas in the underlying agreement review we do not. Consider not using a common component in the underlying agreement application for consistency
3. In file inputs, we can define the file type accepted like this, to make the broswer popup accept these types by default.
For example, for EXCEL type files: `accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"`
4. The form manipulation from the `effect` should be removed from the components and be put to the form provider (as done to this component `FacilityApplyRuleComponent`), as we introduce a second point of manipulating the form, instead of a single point, the form provider.

## Code smells

1. There's a `@ts-expect-error` on `manage-facilities-payload.mutator.ts`. We should generally avoid that. The problem in this case is that we need to update the payload with some data that the RequestTaskPayload considers invalid.
