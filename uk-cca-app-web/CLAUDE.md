# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Overview

The CCA (Climate Change Agreements) web application is an Angular-based frontend that follows the UK Government Digital Service (GDS) design patterns.

## Application Architecture Overview

The CCA application is structured around many workflows that each consists of:

1. **Requests** - Top-level user workflows (e.g., underlying agreement applications)
2. **Tasks** - Steps within a request (e.g., review, application, activation)
3. **Subtasks** - Components of a task (e.g., review target unit details, authorisation evidence)
4. **Wizard Steps** - Individual screens with forms within a subtask

### Key Libraries

The application consists of three main libraries:
1. **govuk-components**: Angular implementation of the GDS design system components
2. **cca-api**: OpenAPI generated library for API communication 
3. **common**: Shared components and utilities

## Build & Run Commands

### Prerequisites

```bash
# Use correct Node.js version
nvm use

# Install dependencies
yarn install
```

### Build Commands

```bash
# Build all libraries first (needed for changes to libs)
yarn prebuild

# Or build specific libraries:
yarn build:govuk-components
yarn build:cca-api
yarn build:common

# Build the main libraries and the application
yarn build

# Build for production
yarn build:production
```

### Development Server

```bash
# Start the development server
yarn start  # Runs on http://localhost:4202
```

### Testing

```bash
# Run all tests
yarn test:frontend # NEVER run this locally
yarn test:frontend:limit #ALWAYS run this locally

# Test specific libraries
yarn test:govuk-components
yarn test:common

# Run tests with coverage
yarn test:frontend:coverage
yarn test:govuk-components:coverage

# Update test snapshots
yarn test:frontend:update-snapshots

# Lint code
yarn lint
```

## Coding Patterns and Best Practices

### Form Provider Pattern
- Use form providers to encapsulate form creation and initial state
- Structure form providers to handle initial validation and state
- Keep them focused on form creation, not business logic

### Data Transformation
- Use Immer's `produce()` for immutable state updates
- Create pure helper functions for common transformations
- Keep transformations localized and single-purpose

### Component Design
- Make components self-contained with clear responsibilities
- Use explicit navigation rather than complex guards
- Follow consistent patterns for form submission

### Type Safety
- Explicitly handle nullable fields with conditional checks
- Use the correct model interfaces when transforming form data back to API models
- Leverage TypeScript's type system to catch issues at compile time


### Section Status Tracking
Each subtask typically has status indicators to track progress:
- **sectionsCompleted** - Tracks progress of tasks ('IN_PROGRESS', 'COMPLETED')
- For review tasks, additional status tracking like 'UNDECIDED', 'ACCEPTED', 'REJECTED' may be used



## Interacting with the Backend

- Each Task, named RequestTask, has a payload with a specific type.
- The angular app sends RequestTasksActions with specific types, payload type and task action type to a specific endpoint.
- Each subtasks always has a SAVE action. The save action is the one that the angular app uses to save data.
- The final Task submission always has a separate action. This action typically includes the word SUBMIT.
- These actions are part of a request task action process dto.

As mentioned above, the previous implementation included manu indirections. The component called a `TaskService` which in turn, through Dependency Injection Tokens routed the call to a specific
TaskService implementation. 

Generic task service implementation:
```typescript
import { inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { concatMap, map, Observable, tap } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { WIZARD_FLOW_MANAGERS, WizardFlowManager } from '../form-flow';
import { PayloadMutatorsHandler } from '../payload-mutators';
import { SideEffectsHandler } from '../side-effects';
import { GenericRequestTaskPayload } from '../types';
import { TaskApiService } from './task-api.service';

export abstract class TaskService {
  protected store = inject(RequestTaskStore);
  protected apiService = inject(TaskApiService);
  protected payloadMutators = inject(PayloadMutatorsHandler);
  protected sideEffects = inject(SideEffectsHandler);
  protected wizardFlowManagers: WizardFlowManager[] = inject(WIZARD_FLOW_MANAGERS);

  abstract get payload(): GenericRequestTaskPayload;
  abstract set payload(payload: GenericRequestTaskPayload);

  saveSubtask(subtask: string, step: string, route: ActivatedRoute, userInput: any): Observable<string> {
    return this.payloadMutators.mutate(subtask, step, this.payload, userInput).pipe(
      concatMap((payload) => this.sideEffects.apply(subtask, step, payload, 'SAVE_SUBTASK')),
      concatMap((payload) => this.apiService.save(payload)),
      tap((payload) => (this.payload = payload)),
      concatMap(() => this.flowManagerForSubtask(subtask).nextStep(step, route)),
    );
  }

  submitSubtask(subtask: string): Observable<boolean> {
    return this.sideEffects.apply(subtask, null, this.payload, 'SUBMIT_SUBTASK').pipe(
      concatMap((payload) => this.apiService.save(payload)),
      tap((payload) => (this.payload = payload)),
      map(() => true),
    );
  }

  submit(): Observable<void> {
    return this.apiService.submit();
  }

  private flowManagerForSubtask(subtask: string): WizardFlowManager {
    const flowManager = this.wizardFlowManagers.find((sfm) => sfm.subtask === subtask) ?? null;
    if (!flowManager) {
      console.error(`###TaskService### :: Could not find WizardFlowManager for subtask: ${subtask}`);
    }
    return flowManager;
  }
}
```

The new approach is more straigh forward. A component submission looks like this

```typescript
// this function runs whenever a user submits a form inside a wizard step
  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementSavePayload(payload); // transforms RequestTaskPayload to the respective RequestTaskActionPayload

    let updatedPayload = update(actionPayload, this.form); // updates the request task action payload, with the user input

    updatedPayload = applySideEffects(updatedPayload); // applies any business logic side effects to the action payload

    const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = 'IN_PROGRESS';
    }); // updates the section status

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted); // creates the final dto

    // sends the dto to the api
    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const baselineExists = this.store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
      const targetPeriodDetails = this.store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
      const completed = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);

      if (completed) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
      } else {
        this.router.navigate([`../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`], { relativeTo: this.route });
      }
    });
  }
}
```


```typescript
export function createRequestTaskActionProcessDTO(
  requestTaskId: number,
  payload: UnderlyingAgreementApplySavePayload,
  sectionsCompleted: Record<string, string>,
): UnaRequestTaskActionProcessDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SAVE_PAYLOAD',
      underlyingAgreement: payload,
      sectionsCompleted,
    },
  };
}
```

Concluding, the request task action flow goes like this:

1. User submits a form in a wizard step component
2. Component creates a RequestTaskAction with appropriate type and payload
3. The action is sent to the Java API via a REST call
4. Server updates the database and returns updated state
5. Client updates local state with the response

## Development Workflow

After making changes:
1. Use `nvm use` to ensure the correct Node.js version. Use $HOME for running nvm.
2. Run `yarn lint --fix` to fix code style issues
3. Run `yarn build` to verify there are no build errors

## Subtask Route Guards Pattern

When refactoring subtask routes, follow this exact pattern:

```typescript
export const SUBTASK_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [subtaskRedirectGuard],
        children: [],
      },
      // Individual routes as siblings - NO guards on these
      {
        path: 'route-name',
        title: 'Route Title',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./component'),
      },
      // ... more routes
      // NO default '**' redirect - the guard handles all routing
    ],
  },
];
```

Key points:
- Empty parent route with children
- Nested empty route with ONLY the redirect guard
- NO guards on individual routes
- NO default '**' redirect
- The redirect guard handles ALL routing logic

## Git Commit Guidelines

- Create clean, descriptive commit messages following conventional commit format
- Do not include "Generated with Claude Code" or similar attributions in commit messages
- Focus on the actual changes and their purpose in the commit message