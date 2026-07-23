# CCA Application Refactoring Documentation

This document captures the patterns established during the refactoring of the CCA application from complex DI-based abstractions to a direct, explicit form-handling approach. The refactoring itself is complete — this doc serves as the living reference for the current patterns and remaining cleanup.

## Architecture Overview

The CCA application is structured in a hierarchy:

1. **Requests** — Top-level user workflows (e.g., underlying agreement applications)
2. **Tasks** — Steps within a request (e.g., review, application, activation)
3. **Subtasks** — Components of a task (e.g., review target unit details, authorisation evidence)
4. **Wizard Steps** — Individual screens with forms within a subtask

## The Current Approach: Direct and Explicit

The refactoring replaced complex DI patterns (abstract `TaskService`, `PayloadMutators`, `SideEffects`, `WizardFlowManagers`) with straightforward, explicit code in each component.

### Core Principles

1. **Direct API Calls with Explicit Data Transformation** — Components call `TasksApiService` directly; all transformation steps are visible within the component.

2. **Pure Functions for Transformations** — Data transformations live in `transform.ts` files as composable, pure functions. No side effects, easy to test.

3. **Immutable Updates with Immer** — All state modifications use Immer's `produce` function.

4. **Business Logic Colocated** — Side-effect logic extracted into named `applySideEffects` functions with clear JSDoc documentation explaining the business rules.

5. **Navigation in Components** — Each component handles its own post-save navigation; no separate flow managers.

---

## Standard Form Submission Pattern

Every form submission follows this pattern:

```typescript
onSubmit() {
  // 1. Get current payload from store
  const payload = this.store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UnderlyingAgreementSubmitRequestTaskPayload;

  // 2. Transform to action payload format
  const actionPayload = toUnderlyingAgreementSavePayload(payload);

  // 3. Apply form values using Immer for immutability
  const updatedPayload = produce(actionPayload, (draft) => {
    draft.underlyingAgreementTargetUnitDetails.operatorName = this.form.get('operatorName')?.value;
  });

  // 4. Apply business logic side effects (if any)
  // updatedPayload = applySideEffects(updatedPayload);

  // 5. Update section status
  const currentSectionsCompleted = this.store.select(underlyingAgreementQuery.selectSectionsCompleted)();
  const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
    draft[SUBTASK_NAME] = TaskItemStatus.IN_PROGRESS;
  });

  // 6. Create DTO and make API call
  const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
  });
}
```

### Variation Review Pattern (with decision resets)

When saving in a variation-review context, decisions must be reset:

```typescript
onSubmit() {
  const payload = this.store.select(
    requestTaskQuery.selectRequestTaskPayload,
  )() as UNAVariationReviewRequestTaskPayload;

  const actionPayload = toUnderlyingAgreementVariationSaveReviewPayload(payload);

  let updatedPayload = produce(actionPayload, (draft) => {
    // Apply form values
  });

  updatedPayload = applySideEffects(updatedPayload);

  const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
  const currentReviewSectionsCompleted = this.store.select(
    underlyingAgreementVariationReviewQuery.selectReviewSectionsCompleted,
  )();
  const currDetermination = this.store.select(
    underlyingAgreementVariationReviewQuery.selectDetermination,
  )();

  // Reset decisions when editing
  const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
    draft[SUBTASK_KEY] = TaskItemStatus.UNDECIDED;
    draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
  });

  const determination = resetDetermination(currDetermination);

  const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
    determination,
    reviewSectionsCompleted,
  });

  this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
    this.router.navigate(['../next-step'], { relativeTo: this.route });
  });
}
```

---

## File Organization per Task Area

Each task area follows a consistent structure:

| File | Purpose |
|------|---------|
| `transform.ts` | Pure functions for data transformations and DTO creation (`createSaveActionDTO`, `to*SavePayload`, etc.) |
| `utils.ts` | Utility functions (e.g., `resetDetermination`, wizard-completion checks) |
| `side-effects.ts` | Business logic extracted from old side-effect files as pure `applySideEffects` functions with JSDoc |

---

## Separating User Input from Calculated Values

User input and business-rule calculations are kept separate:

```typescript
onSubmit() {
  // Direct form-to-payload mapping
  let updatedPayload = produce(actionPayload, (draft) => {
    draft.targetPeriod5Details.details.baselineData = {
      energy: this.form.value.energy,
      throughput: this.form.value.throughput,
    };
  });

  // Apply side effects separately
  updatedPayload = applySideEffects(updatedPayload);
}

/**
 * Business Logic:
 * - If agreement type is RELATIVE and both energy and throughput are present,
 *   calculate performance = energy / throughput.
 */
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

---

## Route Guards

Guards only redirect to the appropriate starting point. The API is the single source of truth for validation.

```typescript
export const subtaskRedirectGuard: CanActivateFn = (route) => {
  const store = inject(RequestTaskStore);
  // Determine correct step based on current state
  return createUrlTreeFromSnapshot(route, [appropriateStep]);
};
```

Route structure pattern:

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
      // Individual routes — NO guards on these
      {
        path: 'step-name',
        title: 'Step Title',
        data: { backlink: '../', breadcrumb: false },
        loadComponent: () => import('./component'),
      },
      // ... more routes
    ],
  },
];
```

---

## Special Considerations

### Decision Reset Pattern (Variation Review)

When any data is edited in variation-review, decisions must be reset to `UNDECIDED` and the overall decision must also be reset:

```typescript
const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
  draft[facilityId || SECTION_KEY] = TaskItemStatus.UNDECIDED;
  draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
});
const determination = resetDetermination(currDetermination);
```

### Facility Components

- Work with individual facility items using a `facility()` computed signal
- Reset facility-specific decisions using `facilityId`
- Navigate back to facility list after saves

### TP5/TP6 Edge Cases

- If baseline doesn't exist, skip to decision
- Check wizard completion before navigation
- Handle both absolute and relative target calculations

---

## Common Pitfalls

1. **Don't skip business logic analysis** — Always examine old side-effect files before refactoring to ensure all rules are preserved.
2. **Don't share components across modules** — Each module should have its own localized components.
3. **Don't over-abstract** — Keep code direct and readable.
4. **Don't duplicate validation** — Let the API be the single source of truth for business rule enforcement.
5. **Always add** `<hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />` immediately before `<netz-return-to-task-or-action-page />`.

---

## Remaining Cleanup

The application-level refactoring is complete. The following cleanup remains:

1. **Remove old-pattern infrastructure from `projects/common/forms/`** — The abstract `TaskService`, `TaskApiService`, `PayloadMutatorsHandler`, `SideEffectsHandler`, `WizardFlowManager`, and their providers have zero production consumers. Delete:
   - `services/task.service.ts`, `task-api.service.ts`
   - `base-wizard-step-container/`
   - `form-flow/` (wizard flow managers & providers)
   - `payload-mutators/` (mutators, handlers, providers)
   - `side-effects/` (side effects, handlers, providers, impls)
   - Update `projects/common/forms/index.ts` barrel export

2. **Clean up ~22 spec files** — Remove dead `TaskService` imports and mock providers. The components no longer inject `TaskService`. Files in:
   - `underlying-agreement-review/subtasks/*/` (11 files)
   - `underlying-agreement-variation/subtasks/tp5/summary/`, `tp6/summary/` (2 files)
   - `underlying-agreement-variation-regulator-led/subtasks/*/` (5 files)
   - `underlying-agreement-variation-review/subtasks/*/` (2 files)
