# CCA Application Refactoring Documentation

This document outlines the major refactoring effort undertaken to simplify the CCA (Climate Change Agreements) Angular application by removing complex dependency injection patterns and introducing a more direct, maintainable approach to form handling and API interactions.

## Architecture Overview

The CCA application is structured in a hierarchy:

1. **Requests** - Top-level user workflows (e.g., underlying agreement applications)
2. **Tasks** - Steps within a request (e.g., review, application, activation)
3. **Subtasks** - Components of a task (e.g., review target unit details, authorization evidence)
4. **Wizard Steps** - Individual screens with forms within a subtask

## Motivation for Refactoring

### Problems with the Previous Architecture

The original implementation relied heavily on Angular's Dependency Injection (DI) system to create a complex abstraction layer for form handling:

1. **Over-abstraction**: Multiple layers of indirection made it difficult to trace data flow
2. **Debugging Complexity**: Finding where business logic was applied required navigating through multiple files and DI tokens
3. **Maintenance Burden**: Adding new features required creating multiple classes (PayloadMutator, SideEffect, StepFlowManager) for a single form
4. **Onboarding Difficulty**: New developers struggled to understand the architecture
5. **Hidden Business Logic**: Critical business rules were scattered across side effect files, making them hard to discover

### What Was Removed

1. **Payload Mutators** (`PayloadMutatorsHandler`, `*PayloadMutator` classes, `PAYLOAD_MUTATORS` token)
   - These handled data transformations through a complex chain of responsibility pattern
   - Business logic was hidden in separate mutator classes

2. **Side Effects** (`SideEffectsHandler`, `*SideEffect` classes, `SIDE_EFFECTS` token)
   - Implemented business rules as separate classes injected via DI
   - Made it difficult to understand what happened during form submission

3. **Wizard Flow Managers** (`WizardFlowManager`, `*StepFlowManager` classes, `WIZARD_FLOW_MANAGERS` token)
   - Abstracted navigation logic into separate classes
   - Added complexity without significant benefits

4. **Base Components** (`BaseWizardStepContainer`)
   - Abstract base class that enforced the complex pattern
   - Removed in favor of simple, self-contained components

## Understanding the Old Approach

### Example: Form Submission Flow

The old approach involved multiple layers of abstraction. Here's how a simple form submission worked:
```typescript
  onSubmit() {
    const facility = {
      facilityId: this.facilityId,
      facilityContact: {
        firstName: this.form.getRawValue().firstName,
        lastName: this.form.getRawValue().lastName,
        email: this.form.getRawValue().email,
        address: this.form.getRawValue().address,
        phoneNumber: this.form.getRawValue().phoneNumber,
      },
    };
    this.taskService
      .saveSubtask(FACILITIES_SUBTASK, FacilityWizardStep.CONTACT_DETAILS, this.activatedRoute, facility)
      .subscribe();
  }
```

### Behind the Scenes: Complex DI Configuration

The form submission triggered a complex chain of operations through multiple DI-configured services:
```typescript
export function providePayloadMutators(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: VariationDetailsPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ReviewTargetUnitDetailsPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: ManageFacilitiesPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: FacilityPayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: Tp5PayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: Tp6PayloadMutator },
    { provide: PAYLOAD_MUTATORS, multi: true, useClass: AuthorisationAdditionalEvidencePayloadMutator },
  ]);
}

export function provideTaskServices(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: TaskApiService, useClass: UnderlyingAgreementVariationApiService },
    { provide: TaskService, useClass: UnderlyingAgreementVariationTaskService },
  ]);
}

export function provideSideEffects(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: SIDE_EFFECTS, multi: true, useClass: VariationDetailsSubmitSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: ReviewTargetUnitDetailsSubmitSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: ManageFacilitiesSubmitSideEffects },
    { provide: SIDE_EFFECTS, multi: true, useClass: FacilitySubmitSideEffects },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5BaselineExistsSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5TargetCompositionSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5AddBaselineDataSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp5SubmitSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6TargetCompositionSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6AddBaselineDataSaveSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: Tp6SubmitSideEffect },
    { provide: SIDE_EFFECTS, multi: true, useClass: AuthorisationAdditionalEvidenceSubmitSideEffect },
  ]);
}

export function provideStepFlowManagers(): EnvironmentProviders {
  return makeEnvironmentProviders([
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: VariationDetailsStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ReviewTargetUnitDetailsStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: ManageFacilitiesStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: FacilityStepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: Tp5StepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: Tp6StepFlowManager },
    { provide: WIZARD_FLOW_MANAGERS, multi: true, useClass: AuthorisationAdditionalEvidenceStepFlowManager },
  ]);
}
// example of the UnderlyingAgreementVariationTaskApiService
@Injectable()
export class UnderlyingAgreementVariationApiService extends TaskApiService {
  private readonly pendingRequestService = inject(PendingRequestService);
  private readonly businessErrorService = inject(BusinessErrorService);

  save(
    payload: UnderlyingAgreementVariationSubmitRequestTaskPayload,
  ): Observable<UnderlyingAgreementVariationSubmitRequestTaskPayload> {
    return this.service.processRequestTaskAction(this.createSaveAction(payload)).pipe(
      catchError((err) => {
        if (err.code === ErrorCode.NOTFOUND1001) {
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError);
        }
        return throwError(() => err);
      }),
      this.pendingRequestService.trackRequest(),
    );
  }

  submit(): Observable<void> {
    return this.service
      .processRequestTaskAction({
        requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SUBMIT_APPLICATION',
        requestTaskId: this.store.select(requestTaskQuery.selectRequestTaskId)(),
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  private createSaveAction(
    payload: UnderlyingAgreementVariationSubmitRequestTaskPayload,
  ): RequestTaskActionProcessDTO & {
    requestTaskActionPayload: UnderlyingAgreementVariationSaveRequestTaskActionPayload;
  } {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const {
      underlyingAgreement,
      sectionsCompleted,
      facilitiesReviewGroupDecisions,
      reviewGroupDecisions,
      reviewSectionsCompleted,
    } = payload;

    return {
      requestTaskId,
      requestTaskActionType: 'UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'UNDERLYING_AGREEMENT_VARIATION_APPLICATION_SAVE_PAYLOAD',
        underlyingAgreement: {
          ...underlyingAgreement,
          underlyingAgreementTargetUnitDetails: {
            operatorName: underlyingAgreement.underlyingAgreementTargetUnitDetails.operatorName,
            operatorAddress: underlyingAgreement.underlyingAgreementTargetUnitDetails.operatorAddress,
            responsiblePersonDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails.responsiblePersonDetails,
          },
        },
        sectionsCompleted,
        facilitiesReviewGroupDecisions,
        reviewGroupDecisions,
        reviewSectionsCompleted,
      },
    };
  }
}
// UnderlyingAgreementVariationTaskService
@Injectable()
export class UnderlyingAgreementVariationTaskService extends TaskService {
  get payload(): UNAVariationRequestTaskPayload {
    return this.store.select(underlyingAgreementQuery.selectPayload)() as UNAVariationRequestTaskPayload;
  }

  set payload(payload: UNAVariationRequestTaskPayload) {
    this.store.setPayload(payload);
  }
}
// TaskService base implementation
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
// TaskApiService
export abstract class TaskApiService {
  protected readonly store = inject(RequestTaskStore);
  protected readonly service = inject(TasksService);

  /**
   * Performs the api save operation and returns the saved payload
   *
   * @param payload
   * @return The saved payload as observable
   */
  abstract save(payload: GenericRequestTaskPayload): Observable<GenericRequestTaskPayload>;

  abstract submit(): Observable<void>;
}

```

### Problems with This Approach

1. **Traceability**: To understand what happens on form submission, developers had to:
   - Find the correct TaskService implementation
   - Locate the corresponding PayloadMutator
   - Search for relevant SideEffects
   - Check the StepFlowManager for navigation logic

2. **Scattered Logic**: Business rules were spread across multiple files, making it hard to get a complete picture

3. **Testing Complexity**: Testing required mocking multiple services and understanding their interactions

4. **Performance**: The DI resolution and multiple Observable chains added unnecessary overhead

## The New Approach: Direct and Explicit

The refactoring replaces the complex DI patterns with a straightforward, explicit approach:

1. **Direct API Calls with Explicit Data Transformation**
   - Components call the API service directly without intermediary services
   - Each transformation step is explicit and visible within the component
   - The flow is linear and easy to follow
   - We minimize the use of async code, only when interacting with async processes (rendering, network)

2. **Form Providers Can Still Be Used**
   - While this refactoring removes custom form abstractions, regular Angular form providers are still valid
   - Form providers should focus only on creating and initializing the form
   - Business logic should stay in the component

3. **Clear Data Transformation Steps**
   - Step 1: Retrieve current state data from store
   - Step 2: Transform to the format needed for saving (using pure functions)
   - Step 3: Apply form values to create updated payload
   - Step 4: Apply any side effects inline using `produce()`
   - Step 5: Create final DTO for API submission
   - Step 6: Make direct API call

4. **Pure Functions for Transformations**
   - Data transformations handled by composable, pure functions
   - Functions defined in a central location (transform.ts)
   - No side effects, making testing and debugging easier

5. **Simple API Service**
   - A lightweight service that handles API calls and error handling
   - No complex chains of responsibility or intermediary services

6. **Immutable Updates with Immer**
   - Using Immer's `produce` function for immutable state updates
   - Makes state changes explicit and easy to understand

### Complete Example: New Form Submission Pattern

Here's how the same form submission works in the refactored approach:

```typescript
onSubmit() {
  // Step 1: Get current state from store
  const payload = this.store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UnderlyingAgreementSubmitRequestTaskPayload;
  
  // Step 2: Transform to action payload format
  const actionPayload = toUnderlyingAgreementSavePayload(payload);
  
  // Step 3: Apply form values using Immer for immutability
  const updatedPayload = produce(actionPayload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorName = this.form.get('operatorName')?.value;
  });
  
  // Step 4: Update section status
  const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
    draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = 'IN_PROGRESS';
  });

  // Step 5: Create DTO and make API call directly
  const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);
  
  // Step 6: Submit and navigate - all logic visible in one place
  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
  });
}
```

## Key Benefits of the New Approach

1. **Readability**: All logic for a form submission is in one place
2. **Debuggability**: Easy to set breakpoints and trace execution
3. **Maintainability**: Clear data flow from form to API
4. **Performance**: Direct calls without multiple Observable chains
5. **Testability**: Simple functions that are easy to test in isolation

## Critical Refactoring Process: Extracting Hidden Business Logic

When refactoring components, the most important step is discovering and preserving business logic hidden in the old architecture:

1. **Check All Related Side Effect Files**:
   - `*-payload.mutator.ts` - Contains data transformation logic
   - `*-save-side-effect.ts` - Contains state update logic on save
   - `*-submit-side-effect.ts` - Contains state update logic on submit
   - `*-step-flow-manager.ts` - Contains navigation logic

### Example: Extracting Business Logic from Side Effects

Old side effect file:
```typescript
// tp5-baseline-exists-save-side-effect.ts
export class Tp5BaselineExistsSaveSideEffect implements SideEffect {
  apply(payload: any): Observable<any> {
    return of(
      produce(payload, (p) => {
        // Hidden business logic: if baseline doesn't exist, clear all TP5 data
        if (!p.targetPeriod5Details.exist) {
          p.targetPeriod5Details.details = null;
        } else {
          if (!p.targetPeriod5Details.details) {
            p.targetPeriod5Details.details = {
              targetComposition: null,
              baselineData: null,
              targets: null,
            };
          }
        }
      })
    );
  }
}
```

Refactored approach with extracted logic:
```typescript
// In component file
onSubmit() {
  // ... form value application ...
  
  // Apply extracted business logic with clear documentation
  updatedPayload = applySideEffects(updatedPayload);
}

// Separate function with business logic clearly documented
/**
 * Applies business logic side effects for baseline existence determination.
 * 
 * Business Logic:
 * 1. If the user indicates that a baseline does NOT exist for TP5:
 *    - All TP5 details must be cleared (set to null)
 *    - This prevents any inconsistent data from remaining in the system
 * 
 * 2. If the user indicates that a baseline EXISTS for TP5:
 *    - Initialize the TP5 details structure if it doesn't already exist
 *    - This prepares the data structure for subsequent form steps
 */
function applySideEffects(payload: UnderlyingAgreementApplySavePayload): UnderlyingAgreementApplySavePayload {
  return produce(payload, (p) => {
    if (!p.targetPeriod5Details.exist) {
      p.targetPeriod5Details.details = null;
    } else {
      if (!p.targetPeriod5Details.details) {
        p.targetPeriod5Details.details = {
          targetComposition: null,
          baselineData: null,
          targets: null,
        };
      }
    }
  });
}
```

## Component Transformation Patterns

### From Old Pattern:
```typescript
constructor(private taskService: TaskService) {}

onSubmit() {
  this.taskService.complete(STEP_NAME, formData);
}
```

### To New Pattern:
```typescript
constructor(
  private tasksApiService: TasksApiService,
  private store: RequestTaskStore,
  private router: Router,
  private route: ActivatedRoute
) {}

onSubmit() {
  const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
  const actionPayload = toUnderlyingAgreementSaveReviewPayload(payload);
  
  const updatedPayload = produce(actionPayload, (draft) => {
    // Apply form data
    // Apply side effect logic inline (extracted from side effect files)
  });
  
  const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
    draft[SECTION_KEY] = TaskItemStatus.IN_PROGRESS;
  });
  
  const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
    draft[SECTION_KEY] = TaskItemStatus.UNDECIDED;
    draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
  });
  
  const determination = resetDetermination(currDetermination);
  
  const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
    determination,
    reviewSectionsCompleted,
    sectionsCompleted,
  });
  
  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../next-step'], { relativeTo: this.route });
  });
}
```

## Implementation Patterns and Best Practices

### 1. Standard Form Submission Pattern

```typescript
onSubmit() {
  const payload = this.store.select(requestTaskQuery.selectRequestTaskPayload)();
  const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  
  const actionPayload = toUnderlyingAgreementSavePayload(payload);
  
  // Update payload with form data.
  let updatedPayload = update(actionPayload, this.form)
  
  updatedPayload = applySideEffects(actionPayload) // apply any business side effects, after the application of the user input, if any.

  // Update section status
  const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
    draft[SUBTASK_NAME] = TaskItemStatus.IN_PROGRESS;
  });
  
  // Update review sections status
  const currentReviewSectionsCompleted = this.store.select(
    underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
  )();

  // Create DTO and make API call using the helper function
  const dto = createSaveActionDTO(requestTaskId, updatedPayload, sectionsCompleted);
  
  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../check-your-answers'], { relativeTo: this.route }); // navigate to workflow next step or the check-your-answers page if this is the last step
  });
}
```
## Helper Functions and Utilities

### transform.ts

This file is very important part of the refactoring process. It contains helper functions for transformations and DTO creation. Some examples:

1. **`createSaveActionDTO`** - For standard form submissions
```typescript
createSaveActionDTO(
  requestTaskId: number,
  underlyingAgreement: UnderlyingAgreementApplySavePayload,
  requestTaskProps: {
    sectionsCompleted: Record<string, string>;
    reviewSectionsCompleted: Record<string, string>;
    determination: Determination;
  }
)
```

2. **`createSaveDecisionActionDTO`** - For decision submissions
```typescript
createSaveDecisionActionDTO(
  requestTaskId: number,
  group: string, // E.g., 'AUTHORISATION_AND_ADDITIONAL_EVIDENCE' or 'TARGET_PERIOD5_DETAILS'
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementReviewDecision,
  determination: Determination
)
```

3. **`toUnderlyingAgreementSaveReviewPayload`** - Transforms the request task payload
```typescript
toUnderlyingAgreementSaveReviewPayload(
  payload: UnderlyingAgreementReviewRequestTaskPayload
): UnderlyingAgreementApplySavePayload
```

### utils.ts

Contains utility functions for workflow logic. Some examples:

1. **`resetDetermination`** - Resets the determination type to null
```typescript
resetDetermination(determination: Determination): Determination {
  return produce(determination, (draft) => {
    if (draft?.type) {
      draft.type = null;
    }
  });
}
```

2. **`reviewSectionsCompleted`** - Checks if all review sections are completed
3. **`createProposedUnderlyingAgreementPayload`** - Creates a proposed payload based on decisions

## Handling Complex Business Logic

### Separating User Input from Calculated Values

One of the key patterns in the refactoring is clearly separating what comes from user input versus what is calculated by business rules:

```typescript
onSubmit() {
  // Direct form to payload mapping
  let updatedPayload = produce(actionPayload, (draft) => {
    draft.targetPeriod5Details.details.baselineData = {
      energy: this.form.value.energy,
      throughput: this.form.value.throughput,
      // ... other direct form mappings
    };
  });
  
  // Apply side effects separately
  updatedPayload = applySideEffects(updatedPayload);
}

function applySideEffects(payload) {
  return produce(payload, (draft) => {
    const agreementType = draft.targetPeriod5Details.details?.targetComposition?.agreementCompositionType;
    const baselineData = draft.targetPeriod5Details.details?.baselineData;
    
    if (agreementType === 'RELATIVE' && baselineData?.energy && baselineData?.throughput) {
      baselineData.performance = calculatePerformance(baselineData.energy, baselineData.throughput);
    }
  });
}
```

### Benefits of This Separation:

1. **Clarity**: Developers can immediately see what is user input vs calculated
2. **Testing**: Business logic can be tested independently of form handling
3. **Debugging**: Easy to verify that calculations are correct
4. **Maintenance**: Business rules are documented and centralized


### Navigation Patterns

We removed the navigation logic from the guards. Now each component is responsible for navigating after sending their respective RequestProcessActionDTO.

- Form components should check wizard completion before navigation:
  ```typescript
  this.tasksApiService
    .saveRequestTaskAction(dto)
    .subscribe((payload: UnderlyingAgreementReviewRequestTaskPayload) => {
      const wizardCompleted = isTargetPeriodWizardCompleted(
        true,
        payload.underlyingAgreement.targetPeriod5Details.exist,
        payload.underlyingAgreement.targetPeriod5Details.details,
      );

      wizardCompleted
        ? this.router.navigate(['../decision'], { relativeTo: this.route })
        : this.router.navigate(['../next-step'], { relativeTo: this.route });
    });
  ```
- Check-your-answers redirects to the main task page:
  ```typescript
  this.router.navigate(['../../..'], { relativeTo: this.route });
  ```

## Simplified Route Guards

The refactoring includes a new approach to route guards that eliminates complex validation logic:

### Philosophy Change
- **Old**: Guards prevented users from accessing "invalid" states through complex business rule validation
- **New**: Guards only redirect to the appropriate starting point; API handles all validation

### Benefits
1. **Simpler Code**: Guards only determine the correct step based on current state
2. **Single Source of Truth**: Remove business logic from guards. Trust the API validations and avoid duplicate checks.

### Guard Implementation Pattern

Create a single redirect guard for each subtask that handles all routing logic:

```typescript
export const tp5RedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  
  // Get current state
  const targetPeriodDetails = store.select(underlyingAgreementQuery.selectTargetPeriodDetails(true))();
  const baselineExists = store.select(underlyingAgreementQuery.selectTargetPeriodExists)();
  const decision = store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_PERIOD5_DETAILS'))();
  
  // Handle edge cases based on business logic
  if (baselineExists === false && !decision) {
    return createUrlTreeFromSnapshot(route, [BaseLineAndTargetsReviewStep.DECISION]);
  }
  
  // Rest of routing logic...
};
```

```typescript

export const TARGET_PERIOD_5_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [tp5RedirectGuard], // notice that this guard is responsible for redirecting to the correct wizard step
        children: [],
      },
      {
        path: BaseLineAndTargetsStep.BASELINE_EXISTS,
        title: 'Baseline data exists',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./baseline-exists/baseline-exists.component').then((c) => c.BaselineExistsComponent),
      },
      {
        path: BaseLineAndTargetsStep.TARGET_COMPOSITION,
        title: 'Add target composition',
        data: { backlink: `../${BaseLineAndTargetsStep.BASELINE_EXISTS}`, breadcrumb: false },
        loadComponent: () =>
          import('./target-composition/target-composition.component').then((c) => c.TargetCompositionComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_BASELINE_DATA,
        title: 'Add baseline data',
        data: { backlink: `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`, breadcrumb: false },
        loadComponent: () =>
          import('./add-baseline-data/add-baseline-data.component').then((c) => c.AddBaselineDataComponent),
      },
      {
        path: BaseLineAndTargetsStep.ADD_TARGETS,
        title: 'Add targets',
        data: { backlink: `../${BaseLineAndTargetsStep.ADD_BASELINE_DATA}`, breadcrumb: false },
        loadComponent: () => import('./add-targets/add-targets.component').then((c) => c.AddTargetsComponent),
      },
      {
        path: BaseLineAndTargetsStep.CHECK_YOUR_ANSWERS,
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/tp5-check-your-answers.component').then(
            (c) => c.BaselineAndTargetsCheckYourAnswersComponent,
          ),
      },
      {
        path: BaseLineAndTargetsStep.SUMMARY,
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./summary/tp5-summary.component').then((c) => c.BaselineAndTargetsSummaryComponent),
      },
    ],
  },
];

```


### Key Guard Considerations

1. **Handle Edge Cases**: Consider business logic edge cases (e.g., if baseline doesn't exist for TP5, skip data collection).

2. **Status Checking**: Check both `sectionsCompleted` and `reviewSectionsCompleted` for proper routing.

## Evaluating the Refactoring

### Advantages of the New Approach

1. **Predictable Data Flow** 
   - Each step is visible within the component
   - No hidden transformations or side effects
   - Easy to trace from user action to API call

2. **Improved Maintainability**
   - Less code overall to understand and maintain
   - Fewer abstractions means less cognitive overhead
   - Business logic is colocated with its usage

3. **Better Testability**
   - Pure functions for transformations are easy to test
   - No need to mock complex DI chains
   - Component tests can focus on user interactions

4. **Reduced Framework-Specific Code**
   - Less reliance on Angular DI magic
   - More portable patterns
   - Easier to onboard developers from other frameworks

5. **Better Developer Experience**
   - Debugging is straightforward
   - New developers can understand the codebase quickly
   - IDE navigation works better without DI indirection

### Trade-offs

1. **Some Code Duplication**
   - Similar submission patterns appear in multiple components
   - However, this is explicit and easy to understand
   - Can be mitigated with shared utility functions

2. **More Verbose in Places**
   - Direct approach sometimes requires more lines of code
   - But the code is more readable and maintainable
   - Verbosity is offset by clarity


#### 3. Submission Flow Pattern
Every form submission in variation-review follows this exact pattern:

```typescript
onSubmit() {
  // 1. Get current payload
  const payload = this.requestTaskStore.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UNAVariationReviewRequestTaskPayload;
  
  // 2. Transform to save payload
  const actionPayload = toUnderlyingAgreementVariationSaveReviewPayload(payload);
  
  // 3. Update with form values
  let updatedPayload = produce(actionPayload, (draft) => {
    // Apply form values to draft
  });
  
  // 4. Apply business logic side effects (if any)
  updatedPayload = applySideEffects(updatedPayload);
  
  // 5. Get current state
  const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
  const currentReviewSectionsCompleted = this.store.select(
    underlyingAgreementVariationReviewQuery.selectReviewSectionsCompleted,
  )();
  const currDetermination = this.store.select(
    underlyingAgreementVariationReviewQuery.selectDetermination,
  )();
  
  // 6. Reset decisions when editing
  const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
    draft[SUBTASK_KEY] = TaskItemStatus.UNDECIDED;
    draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
  });
  
  const determination = resetDetermination(currDetermination);
  
  // 7. Create DTO using variation-review specific functions
  const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
    determination,
    reviewSectionsCompleted,
  });
  
  // 8. Submit and navigate
  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../next-step'], { relativeTo: this.route });
  });
}
```
### Transform Functions
The `transform.ts` file contains all DTOs and transformation functions:
- `createSaveActionDTO` - For standard saves
- `createSaveDecisionActionDTO` - For decision saves
- `createSaveFacilityDecisionActionDTO` - For facility decisions
- `toUnderlyingAgreementVariationSaveReviewPayload` - Main transform function

### Utility Functions
The `utils.ts` file contains:
- `resetDetermination` - Resets determination type
- Other helper functions as needed

## Next Steps

1. Complete tp5 subtask refactoring
2. Complete tp6 subtask refactoring
3. Refactor overall-decision subtask
4. Refactor notify-operator functionality
5. Remove any unused code from @requests/common folder


## Special Considerations for Variation Review

### Decision Reset Pattern
When any data is edited in variation-review, decisions must be reset:
```typescript
// In submission methods
const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
  // Reset the current section
  draft[facilityId || SECTION_KEY] = TaskItemStatus.UNDECIDED;
  // Always reset overall decision
  draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
});

const determination = resetDetermination(currDetermination);
```

### Facility Components Pattern
Facility components require special handling:
1. They work with individual facility items
2. Use `facility()` computed signal to get current facility
3. Reset facility-specific decisions using facilityId
4. Navigate back to facility list after saves

### TP5/TP6 Edge Cases
- If baseline doesn't exist, skip to decision
- Check wizard completion before navigation
- Handle both absolute and relative target calculations

## Key Principles That Guided the Refactoring

### 1. Business Logic First
**Always start by understanding the hidden business logic:**
- Examine all payload mutators to find data transformations
- Review side effect files to discover business rules
- Extract and document all logic before refactoring
- Create comprehensive JSDoc comments for complex calculations

### 2. Maintain Feature Parity
**The refactoring must not break existing functionality:**
- All side effects must be preserved
- Edge cases from the original code must be handled
- Business logic must produce identical results
- API contracts must remain unchanged

### 3. Consistency Over Cleverness
**Use established patterns throughout:**
- Follow the same structure for all form submissions
- Use consistent naming conventions
- Apply the same error handling patterns
- Maintain uniform documentation standards

### 4. Type Safety as Documentation
**TypeScript types serve as living documentation:**
- Use proper types instead of `any`
- Add type assertions where necessary
- Let types guide the implementation
- Types should make invalid states impossible

## Step-by-Step Refactoring Process

When refactoring a module to the new pattern, follow this systematic approach:

### Step 1: Create Transform Functions

Start by creating a `transform.ts` file that handles all data transformations between the frontend state and API payloads:
```typescript
// src/app/requests/tasks/underlying-agreement-review/transform.ts
import {
  CcaDecisionNotification,
  Determination,
  Facility,
  FacilityItem,
  RequestTaskActionProcessDTO,
  UnderlyingAgreementFacilityReviewDecision,
  UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload,
  UnderlyingAgreementPayload,
  UnderlyingAgreementReviewDecision,
  UnderlyingAgreementReviewRequestTaskPayload,
  UnderlyingAgreementApplySavePayload,
  UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload,
  UnderlyingAgreementSaveReviewRequestTaskActionPayload,
} from 'cca-api';

type UnaReviewSaveActionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewRequestTaskActionPayload;
};

type UnaReviewSaveDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
};

type UnaReviewSaveFacilityDecisionDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
};
type UnaReviewSaveDeterminationDTO = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
};

type UnaReviewNotifyDto = RequestTaskActionProcessDTO & {
  requestTaskActionPayload: UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
};

export function createSaveActionDTO(
  requestTaskId: number,
  underlyingAgreement: UnderlyingAgreementApplySavePayload,
  requestTaskProps: {
    sectionsCompleted: Record<string, string>;
    reviewSectionsCompleted: Record<string, string>;
    determination: Determination;
  },
): UnaReviewSaveActionDTO {
  const { determination, reviewSectionsCompleted, sectionsCompleted } = requestTaskProps;
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_APPLICATION_REVIEW_PAYLOAD',
      underlyingAgreement,
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
    },
  };
}
export function createSaveDecisionActionDTO(
  requestTaskId: number,
  group: UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementReviewDecision,
  determination: Determination,
): UnaReviewSaveDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      group,
      reviewSectionsCompleted,
      determination,
      decision,
    },
  };
}

export function createSaveDeterminationActionDTO(
  requestTaskId: number,
  determination: Determination,
  reviewSectionsCompleted: Record<string, string>,
): UnaReviewSaveDeterminationDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination,
      reviewSectionsCompleted,
    },
  };
}

export function createSaveFacilityDecisionActionDTO(
  requestTaskId: number,
  facilityId: string,
  reviewSectionsCompleted: Record<string, string>,
  decision: UnderlyingAgreementFacilityReviewDecision,
  determination: Determination,
): UnaReviewSaveFacilityDecisionDTO {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION_PAYLOAD',
      group: facilityId,
      reviewSectionsCompleted,
      determination,
      decision,
    },
  };
}

export function createNotifyOperatorActionDTO(
  requestTaskId: number,
  decisionNotification: CcaDecisionNotification,
  proposedPayload: UnderlyingAgreementPayload,
): UnaReviewNotifyDto {
  return {
    requestTaskId,
    requestTaskActionType: 'UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION',
    requestTaskActionPayload: {
      payloadType: 'UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
      underlyingAgreementProposed: proposedPayload,
      decisionNotification,
    },
  };
}

export function toUnderlyingAgreementSaveReviewPayload(
  payload: UnderlyingAgreementReviewRequestTaskPayload,
): UnderlyingAgreementApplySavePayload {
  if (!payload.underlyingAgreement) throw new Error('Underlying agreement payload is missing');
  return transformUnderlyingAgreement(payload.underlyingAgreement);
}

function transformUnderlyingAgreement(
  underlyingAgreement: UnderlyingAgreementPayload,
): UnderlyingAgreementApplySavePayload {
  return {
    underlyingAgreementTargetUnitDetails: underlyingAgreement.underlyingAgreementTargetUnitDetails,
    facilities: transformFacilities(underlyingAgreement.facilities),
    targetPeriod5Details: underlyingAgreement.targetPeriod5Details,
    targetPeriod6Details: underlyingAgreement.targetPeriod6Details,
    authorisationAndAdditionalEvidence: underlyingAgreement.authorisationAndAdditionalEvidence,
  };
}

function transformFacilities(facilities: Facility[]): FacilityItem[] {
  return (
    facilities?.map((facility) => ({
      facilityId: facility.facilityId,
      facilityDetails: facility.facilityDetails,
      apply70Rule: facility.apply70Rule,
      eligibilityDetailsAndAuthorisation: facility.eligibilityDetailsAndAuthorisation,
      facilityContact: facility.facilityContact,
      facilityExtent: facility.facilityExtent,
    })) || []
  );
}

```

### Step 2: Extract Business Logic from Side Effects

Before refactoring components, analyze all side effect files:

1. Search for `*-payload.mutator.ts` files
2. Search for `*-side-effect.ts` files
3. Document all business logic found
4. Create `applySideEffects` functions with clear documentation

### Step 3: Refactor Components

Transform components from the old pattern to the new:

1. Remove TaskService dependency
2. Inject required services directly
3. Implement the standard submission pattern
4. Apply extracted business logic
5. Ensure proper navigation

### Step 4: Implement Route Guards

Create simple redirect guards that determine the appropriate starting point:

```typescript
export const subtaskRedirectGuard: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  // Determine correct step based on current state
  return createUrlTreeFromSnapshot(route, [appropriateStep]);
};
```

### Step 5: Verify the Refactoring

After each subtask:
1. Run `yarn build` to catch type errors
2. Verify all imports resolve correctly
3. Check that business logic is preserved
4. Test edge cases from original implementation

## Best Practices Established Through the Refactoring

### Code Organization
1. **Transform Functions**: Centralize all data transformations in `transform.ts`
2. **Utility Functions**: Keep helper functions in `utils.ts`
3. **Business Logic**: Document and isolate in `applySideEffects` functions
4. **Component Localization**: Copy and adapt components rather than sharing across modules

### Development Workflow
1. **Build Early and Often**: Run `yarn build` after each subtask
2. **Check Imports**: Verify all imports resolve before proceeding
3. **Preserve Business Logic**: Extract all logic from side effects before removing them
4. **Document Decisions**: Add JSDoc comments explaining why code works a certain way

### Patterns to Follow
1. **Form Submission**: Use the standard 6-step pattern for all submissions
2. **State Updates**: Use Immer's `produce` for all state modifications
3. **Navigation**: Check completion status before routing
4. **Error Handling**: Let the API be the single source of truth for validation

### Common Pitfalls to Avoid
1. **Don't Skip Business Logic Analysis**: Always examine side effect files first
2. **Don't Share Components**: Each module should have its own components
3. **Don't Over-Abstract**: Keep the code direct and readable
4. **Don't Duplicate Validation**: Let the API handle business rule enforcement

## Concrete Examples from Current Refactoring

### Example 1: Facility Details Component Localization
```typescript
// Original component in @requests/common
// Copied to variation-review and adapted:

@Component({
  selector: 'app-facility-details',
  templateUrl: './facility-details.component.html',
  imports: [SharedModule, AddressInputComponent],
  providers: [facilityItemFormProvider('facilityDetails')],
})
export class FacilityDetailsComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  protected readonly form = inject<UntypedFormGroup>(TASK_FORM);
  // ... rest of implementation
  
  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;
    
    const actionPayload = toUnderlyingAgreementVariationSaveReviewPayload(payload);
    const facility = this.facility();
    
    // Update facility in payload
    const updatedPayload = produce(actionPayload, (draft) => {
      const facilityIndex = draft.facilities.findIndex(f => f.facilityId === facility.facilityId);
      if (facilityIndex >= 0) {
        draft.facilities[facilityIndex].facilityDetails = {
          name: this.form.value.name,
          type: this.form.value.type,
          address: this.form.value.address,
        };
      }
    });
    

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementVariationReviewQuery.selectReviewSectionsCompleted,
    )();
    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[facility.facilityId] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });
    
    // Continue with submission...
  }
}
```

### Example 2: Redirect Guard Implementation
```typescript
// facility-redirect.guard.ts
export const facilityRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const facilityId = route.paramMap.get('facilityId');
  
  const payload = store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UNAVariationReviewRequestTaskPayload;
  
  const facility = payload.underlyingAgreement.facilities.find(
    (f) => f.facilityId === facilityId
  );
  
  if (!facility) {
    return createUrlTreeFromSnapshot(route, ['../../../']);
  }
  
  const decision = store.select(
    underlyingAgreementVariationReviewQuery.selectFacilityDecision(facilityId),
  )();
  
  // Route based on completion status
  if (decision) {
    return createUrlTreeFromSnapshot(route, [
      FacilityWizardReviewStep.CHECK_YOUR_ANSWERS
    ]);
  }
  
  if (!facility.facilityDetails?.name) {
    return createUrlTreeFromSnapshot(route, [
      FacilityWizardReviewStep.DETAILS
    ]);
  }
  
  // Continue routing logic...
};
```

### Example 3: Decision Component Pattern
```typescript
// facility-decision.component.ts
@Component({
  providers: [decisionFormProvider('FACILITY')],
  // ... other metadata
})
export class FacilityDecisionComponent {
  submit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;
    
    const facility = this.facility();
    const formValue = this.form.value;
    
    // Create decision object
    const decision: UnderlyingAgreementVariationFacilityReviewDecision = {
      type: formValue.type,
      changeStartDate: formValue.type === 'ACCEPTED' && facility.status === 'NEW' 
        ? !!formValue.changeDate?.[0] : null,
      startDate: formValue.startDate as any,
      details: { 
        notes: formValue.notes, 
        files: formValue.files.map((f) => f.uuid) 
      },
      facilityStatus: facility.status,
    };
    
    // Update review sections
    const reviewSectionsCompleted = produce(
      payload.reviewSectionsCompleted, 
      (draft) => {
        draft[facility.facilityId] = 
          formValue.type === 'ACCEPTED' 
            ? TaskItemStatus.ACCEPTED 
            : TaskItemStatus.REJECTED;
      }
    );
    
    // Create and submit DTO
    const dto = createSaveFacilityDecisionActionDTO(
      requestTaskId,
      facility.facilityId,
      reviewSectionsCompleted,
      decision,
      determination,
    );
    
    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../', FacilityWizardReviewStep.CHECK_YOUR_ANSWERS], {
        relativeTo: this.activatedRoute,
      });
    });
  }
}
```
