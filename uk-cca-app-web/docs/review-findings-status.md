# UK CCA App Web — Review Findings Status

**Last updated:** 2026-07-13
**References:** `docs/code-review-2026-07-01.md`, `docs/lint-review-2026-07-08.md`

---

## Status Summary

| Category | Count | Description |
|----------|-------|-------------|
| 🟠 High | 1 | Keycloak-js upgrade (deferred) |
| 🟡 Lint warnings | 271 + 3 | `no-explicit-any` warnings — catalogued, not yet addressed |

All other findings from the code review have been resolved (see resolved list below).

---

## Current Build & Test State

| Check | Result |
|-------|--------|
| `yarn build` | ✅ Passes (~19s) |
| `yarn test:frontend` | ✅ 2355 passed — 0 errors |
| `yarn lint` (main app) | ⚠️ 175 warnings (0 errors) — all `no-explicit-any` |
| `yarn lint` (common) | ⚠️ 3 warnings (0 errors) — all `no-explicit-any` |
| `yarn lint` (govuk-components) | ✅ Clean |
| `yarn lint` (cca-api) | ✅ Clean |

---

## ⏸️ Deferred

| ID | Finding | Reason |
|----|---------|--------|
| **H-3b** | Keycloak-js 25.0.6 EOL | Terminal 25.x release — 26.x needs import path changes and server version coordination |

---

## ⚠️ Pending — Lint Warnings

271 `no-explicit-any` warnings catalogued in [`lint-review-2026-07-08.md`](lint-review-2026-07-08.md). Breakdown:

| Category | Count | Description |
|----------|-------|-------------|
| 🟢 A (Fixable) | ~80 remaining | Being resolved per feature area |
| 🔵 B (Use unknown) | 2 | `unknown` is more appropriate than `any` |
| 🟡 C (ESLint-ignore) | 108+ | Genuinely required — add `// eslint-disable-next-line` |
| 🟠 D (Needs refactor) | 3 | Requires broader refactor beyond single-line change |

**Recommended approach:** Tackle phases in order — Phase 1 (source files, ~1h) → Phase 2 (spec files, ~30m) → Phase 3 (mass eslint comments, ~1h) → Phase 4 (refactors, future sprints).

---

## ✅ Resolved

| ID | Finding | Fix |
|----|---------|-----|
| **M-4** | `console.error` in production | All 22 app-level calls migrated to `logger.error()`; 4 library-level calls wrapped in `isDevMode()` guard |
| **M-6** | ESLint `no-explicit-any` | Rule set to `'warn'` in main app and common; `'off'` in govuk-components and cca-api. 271 warnings reviewed and catalogued — see above for action plan. |
| **M-12** | No MIME type validation | 12 common formats added to `commonFileValidators` (PDF, DOCX, DOC, XLSX, XLS, PPTX, PPT, JPEG, JPG, PNG, CSV, TXT); validator optimized with `Set.has()` O(1) lookup |
| **C-1–C-5** | Critical findings | SonarQube token, source maps, orphaned subscriptions, strict mode, userIsAssigneeGuard |
| **H-1** | `[innerHTML]` XSS risk | Replaced with `{{ }}` interpolation in task-list |
| **H-2** | Missing security headers | Added to nginx config |
| **H-3a** | Keycloak `enableLogging` | Set to `false` in `environment.prod.ts` |
| **H-4** | Store `reset()` | Uses `structuredClone` consistently |
| **H-5** | Dockerfile | Alpine base, `USER nginx`, `HEALTHCHECK` |
| **H-6** | `SendForPeerReview` | Uses centralized `TasksApiService` |
| **M-1** | `OnPush` on `AppComponent` | Added |
| **M-2** | Selectors in `RequestTaskPageComponent` | Pre-created as class properties |
| **M-3** | `[innerHTML]` in govuk-components | Replaced in hints |
| **M-7** | `SignalStore.updateState` | Uses `structuredClone` |
| **M-9** | Lockfile in CI | `--frozen-lockfile` added |
| **M-11** | `selectTaskReassignedTo` | Uses `createSelector` |
| **L-1–L-5** | Low-severity findings | All resolved |

---

## Test Fixes Applied (2026-07-13)

Initial run: **37 failing tests** across 23 files → **0 failing, 2355 passing** (620 test files).

### 1. ActivatedRoute Stubs Ignored Initial Params (2 files)

**Impact:** ~15 tests rendered empty (`<!--container-->`) because `snapshot.params` was always `{}`.

**Root cause:** `ActivatedRouteSnapshotStub`'s constructor hardcoded `this.params = {}` instead of `this.params = initialParams ?? {}`. Additionally, `ActivatedRouteStub.setParamMap()` didn't sync `snapshot.params`.

**Fix:** Use `initialParams` and `initialQueryParams` in snapshot stub; sync `snapshot.params` in `setParamMap()`.

**Commit:** `dcd49c70`

### 2. Missing `TestBed.createComponent()` Calls (2 files)

**Impact:** `TypeError: Cannot read properties of undefined (reading 'componentInstance')`

**Root cause:** `fixture` was declared but never assigned — `TestBed.createComponent()` was never called.

**Files:** `non-compliance-enforcement-response-notice-peer-reviewer-decision.component.spec.ts`, `non-compliance-notice-of-intent-peer-reviewer-decision.component.spec.ts`

### 3. Missing `AuthStore` Injection (2 files)

**Impact:** `TypeError: Cannot read properties of undefined (reading 'setUserState')`

**Root cause:** Variable `authStore` was declared but never initialized with `TestBed.inject(AuthStore)`.

**Files:** `tpr-csv-upload-process.component.spec.ts`, `submission-results.component.spec.ts`

### 4. Wrong Date Types in Form + Spec (2 files)

**Impact:** `TypeError: value.getDate is not a function` — `DateInputComponent` expected `Date` but received ISO strings.

**Fix:** `Date` objects instead of ISO strings in spec; fixed form model type to `FormControl<Date | string | null>`.

**Files:** `provide-details-form.provider.ts`, `provide-details.component.spec.ts`

### 5. `ActivatedRouteStub` Parameter Order (13 files)

**Impact:** Tests rendered empty/partial content because `facilityId` was passed as query param instead of route param.

**Root cause:** `ActivatedRouteStub(undefined, { facilityId: '...' })` — 2nd arg is `initialQueryParams`, not `initialParams`. Changed to `ActivatedRouteStub({ facilityId: '...' })`.

**Files:** All `check-answers`, `summary`, `decision`, and `contact-details` specs across `underlying-agreement-review`, `underlying-agreement-variation`, `underlying-agreement-variation-regulator-led`, `underlying-agreement-variation-review`, and timeline components.

### 6. Unhandled Router Rejections — `NG04002` (8 files)

**Impact:** 12 unhandled promise rejections (`Cannot match any routes. URL Segment: 'check-your-answers'`). Tests passed but produced error noise.

**Root cause:** Components' `onSubmit()` methods call `router.navigate()` inside the subscribe callback of a mocked `saveRequestTaskAction` (returns `of({})` synchronously). The real Angular Router had no routes, so navigation rejected.

**Fix:** Added `provideRouter([{ path: '**', component: DummyComponent }])` with a catch-all wildcard route and inline `@Component({ template: '' })` dummy component.

**Files:** `provide-details`, `choose-relevant-facilities`, `choose-relevant-workflows`, `issue-enforcement`, `complete-task`, `audit-details`, `track-corrective-actions-details`, `pre-audit-review-requested-documents-upload` specs.

### 7. Stale Snapshots (11 files)

**Legitimate changes caught by snapshots:** "Change" links moved from generic `/?change=true` to specific paths (e.g., `/details?change=true`); file download URLs changed from `/` to `/file-download/UUID`; navigation links updated (e.g., `/` → `/products`).

**Fix:** Updated 11 `.snap` files to match current rendered output.

**Commit:** `13311144`

---

## Refactoring Status

The major refactoring (documented in [`docs/refactor.md`](refactor.md)) is complete. All application components now use the direct pattern. Remaining cleanup:

1. Delete unused old-pattern infrastructure from `projects/common/forms/`
2. Remove dead `TaskService` mocks from ~22 spec files

---

## Key Architectural Decisions

| Decision | Rationale |
|----------|-----------|
| `SignalStore` abstract class has **no** `@Injectable()` | Abstract base — never instantiated directly. `inject()` resolves through subclass context |
| `SignalStore.updateState` uses `structuredClone` | Consistent with `reset()` and constructor; prevents shared-reference bugs |
| `logger` utility uses `isDevMode()` | Avoids `environment` import (pulls `keycloak-js` CJS → breaks Vitest module graph) |
| `no-explicit-any` set to `'warn'` in main app and common; `'off'` in govuk-components (rewrite in progress) and cca-api (auto-generated) | 271+3 warnings catalogued — see `docs/lint-review-2026-07-08.md` for phased action plan |
| Build budgets kept at 3MB/4MB | Needs performance baseline before reducing; `source-map-explorer` is configured |
