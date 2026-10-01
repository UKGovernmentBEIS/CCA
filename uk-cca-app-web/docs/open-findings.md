# Open findings

**Updated:** 2026-09-23

The single list of what still needs fixing, and why. Everything resolved has been removed from the docs —
the history lives in git (`git log -- docs/`). Work in progress and the branch/batch plan:
[status.md](status.md). Reference material: [architecture.md](architecture.md) and [decisions/](decisions/).

---

## Application review findings

### Routing

- 🔴 **High — Dynamic barrel imports in route `loadComponent` break tree-shaking.** Lazy routes reach
  `@shared/components` through its barrel (`import('@shared/components').then((m) => m.FileDownloadComponent)`),
  and a barrel behind a dynamic import cannot be tree-shaken, so all 47 shared components land in the lazy
  chunk. *Why:* oversized lazy chunks and slower route loads. *Where:* 21 lazy `import('@shared/components')`
  calls across 20 route tables in `src/app/**/*.routes.ts` — e.g.
  `src/app/requests/tasks/tasks.routes.ts:298,303`, `src/app/sectors/sector/sector.routes.ts:40`.
- 🟡 **Medium — `withComponentInputBinding()` is not enabled.** `provideRouter()` in `app.config.ts` does not
  bind router inputs, so components hand-roll parameter/query/history extraction
  (`activatedRoute.paramMap.pipe(map(() => window.history.state['userId']))`). *Why:* duplicated boilerplate and
  route values silently missed. *Where:* `src/app/app.config.ts:58-65`,
  `src/app/two-fa/reset-two-fa/reset-two-fa.component.ts:27-34`.
- 🟡 **Medium — The root route table statically imports 10 content pages.** `Accessibility`, `ContactUs`,
  `Feedback`, `Legislation`, `PrivacyNotice`, `TermsAndConditions`, `TimedOut`, `Version`, `LandingPage` and
  `DashboardPage` are imported directly, so they and their dependencies ship in the initial bundle. *Why:*
  larger first-load payload for every user. *Where:* `src/app/app.routes.ts:12-24`.

### Signals and state

- 🔴 **High — `AccordionComponent` writes to child signals from an `effect()`.** The constructor effect
  assigns `this.accordion.id` and calls `item.itemIndex.set(...)` / `item.isExpanded.set(...)` on content
  children. *Why:* bypasses one-way data flow; risks change-detection feedback loops and render-order bugs.
  *Where:* `projects/govuk-components/src/lib/accordion/accordion.component.ts:43-61`.
- 🟡 **Medium — `SignalStore.rxSelect` builds a new observable on every call.** Each call creates a fresh
  `computed` through `this.select(selector)` and wraps it in `toObservable(...)`, so repeated calls from
  getters or templates allocate un-memoized streams. *Why:* allocation churn and duplicate subscriptions where
  one cached stream would do. *Where:* `projects/common/store/signal-store.ts:94-96`.
- 🟡 **Medium — `SignalStore.updateState` deep-clones the whole state on every partial update.**
  `this.setState({ ...structuredClone(this._state()), ...state })`. *Why:* CPU and memory cost that scales with
  state size on a hot path. *Where:* `projects/common/store/signal-store.ts:83`. **Note:** treat as undecided —
  one review records this `structuredClone` as the fix for an earlier defect, the other as the defect.
- 🔵 **Low — Two-way bindings not migrated to `model()`.** `currentPage` + `pageChange` remain separate
  `input()`/`output()` pairs. *Why:* boilerplate and room for the pair to drift apart; no correctness bug today.
  *Where:* `src/app/shared/components/pagination/pagination.component.ts`,
  `src/app/shared/components/table/table.component.ts`.

### Subscriptions and templates

- 🟡 **Medium — More than 370 `.subscribe()` calls have no teardown.** Most subscriptions in components and
  services use neither `takeUntilDestroyed()`, the async pipe nor `toSignal()`, although the pattern has been
  adopted in about 95 places. *Why:* leaks, and callbacks running against destroyed components when navigation
  precedes the response. *Where:* examples — `src/app/two-fa/reset-two-fa/reset-two-fa.component.ts:62`,
  `src/app/shared/components/wizard/wizard-step.component.ts`.
- 🔵 **Low — 105 leftover `$safeNavigationMigration` template calls** (the reviews say "120+", the repo is now
  at 105 across `src` and `projects`). *Why:* obscures template intent; optional chaining or a computed signal
  belongs there. *Where:* examples — `src/app/shared/components/summary/summary.component.html:3`,
  `src/app/shared/components/workflow-details/workflow-details.component.html:6-17`,
  `projects/govuk-components/src/lib/textarea/textarea.component.html:31-53`.

### Type safety and strictness

- 🔴 **High — `strict` and `strictNullChecks` are disabled at workspace level.** `tsconfig.json` sets
  `strictPropertyInitialization: false`, `strictNullChecks: false`, `useDefineForClassFields: false` and
  `ignoreDeprecations: "6.0"`. *Why:* nullability is assumed away app-wide, producing runtime
  `TypeError: Cannot read properties of undefined` in edge cases. *Where:* `tsconfig.json:15-16,39`. Enabling
  strict null checks was started once and abandoned; it needs redoing as a staged effort.
- 🔴 **High — Legacy `UntypedFormControl` / `UntypedFormGroup` in the component libraries.** The reusable
  libraries declare untyped controls, groups and builders while feature forms are typed. *Why:* form type
  errors surface only at runtime, and the untyped surface spreads to consumers. *Where:*
  `projects/govuk-components/src/lib/form/form-input.ts:8,40-42`,
  `projects/govuk-components/src/lib/form/form-builder.service.ts:5-7`,
  `src/app/shared/components/wizard/wizard-step.component.ts:28`.
- 🟡 **Medium — `futureDateValidator` compares a string against a `Date`.** `control.value > date` relies on JS
  coercion when the value is a string such as `'2026-09-02'`, so the comparison is meaningless. *Why:* invalid
  dates can pass and valid dates can fail. *Where:* `src/app/shared/validators/validators.ts:31-36`.

### Structure and duplication

- 🟡 **Medium — Parallel table and error-summary components.** `cca-table` vs `govuk-table`, and
  `cca-error-summary` vs `govuk-error-summary`, duplicate sorting/rendering logic. *Why:* every fix has to be
  applied twice — the split of the duplicate-ID finding across both copies is a live example. *Where:*
  `src/app/shared/components/table` vs `projects/govuk-components/src/lib/table`;
  `src/app/shared/components/error-summary` vs `projects/govuk-components/src/lib/error-summary`.
- 🔵 **Low — Directory typo `teriminated-transaction`.** The folder name carries an extra "i" and imports point
  at the misspelling. *Why:* cosmetic but durable — the wrong name propagates into every future import and
  search. *Where:* `src/app/shared/components/teriminated-transaction/`.
- 🔵 **Low — `logger`/`isDevMode` workaround no longer needed.** The ESM upgrade that made it necessary has
  landed. *Why:* dead complexity. *Where:* `src/app/shared/utils/logger.ts:1,13,16,19`.
- 🔵 **Low — `no-explicit-any` is still `'off'` for `govuk-components`.** *Why:* the library ships untyped code
  without lint pressure, so new `any` slips in unnoticed. *Where:* `eslint.config.js:74,98`.
- 🔵 **Low — Build budgets (3MB/4MB) have no performance baseline.** *Why:* budgets cannot be tightened
  objectively until initial-bundle and lazy-chunk sizes are measured. *Where:* `angular.json:54-55`;
  `source-map-explorer` is already a dev dependency.

---

## Out-of-scope findings — CCA-3407 file-upload review

Found while reviewing the file-upload fixes on `fix/CCA-3407`. None of them belong to that work, so they are
recorded here instead of being fixed in the branch.

- 🔴 **High — `'null'` string reaches the payload and crashes the baseline summary.** `String(...)` over a
  control that can be `null` produces the literal `'null'`, which `DecimalPipe` rejects
  (`invalidPipeArgumentError`) and which is truthy, so it also survives the "unchanged" filter and wipes a saved
  regulator decision. *Why:* the baseline-and-targets summary throws where the other task families (which pass
  the raw value) render blank, and a decision disappears silently. *Where:*
  `src/app/requests/tasks/underlying-agreement-variation-review/subtasks/tp5/add-baseline-data/add-baseline-data.component.ts:217`
  and `.../tp6/add-baseline-data/add-baseline-data.component.ts:220` — the same file's `:210` also writes the TP5
  baseline into `targetPeriod6Details` — consumed by
  `src/app/requests/common/underlying-agreement/summaries/baseline-and-targets-summary-data.ts:358-360`.
- 🟡 **Medium — Disabled controls read through `form.value` lose their value.** Angular omits disabled controls
  from `form.value`, so a control the form disabled and reset reaches the payload writer as `undefined` and the
  key is dropped instead of cleared. *Why:* the server keeps the previous value while the screen shows it as
  cleared. *Where:*
  `.../underlying-agreement-review/subtasks/review-target-unit-details/company-registration-number/company-registration-number.component.ts:58-62`
  (same component in the other underlying-agreement families),
  `src/app/sectors/sector/target-units-tab/create-target-unit/company-registration-number/company-registration-number.component.ts:56-60`,
  `src/app/buy-out-surplus/**/change-status/change-status.component.ts:90`.
- 🟡 **Medium — File-control value shape is not guaranteed at the form boundary.** `buildFormControl` picks the
  upload validator from `Array.isArray(uuid)` and still types the argument `string | string[]`, while
  `fileUtils.toUUIDs` returns `[]` for a non-array: a control whose shape does not match its template silently
  drops stored files, or uploads only the first pending one. *Why:* the defect class the file-upload branch keeps
  patching at call sites needs one owner. *Where:* `src/app/shared/services/request-task-file.service.ts:38,56-58`,
  `src/app/shared/utils/files.ts:19-21`,
  `src/app/shared/components/multiple-file-input/multiple-file-input.component.ts:154-158`.
- 🟡 **Medium — The 3/7ths fields are only cleared after the energy consumed changes.** The facility apply-rule
  provider disables and resets `energyConsumedProvision` and `startDate` inside a `valueChanges` subscription, so
  a facility loaded with ≥70% keeps the previously saved provision while the template hides it, and saving
  submits it. *Why:* hidden-but-saved value, and the same trap the branch just fixed for the fields the user
  touches. Deciding whether to clear on load needs product input — it would blank the 3/7ths row on review
  screens. *Where:* `.../underlying-agreement-{application,review,variation,variation-regulator-led,variation-review}/subtasks/manage-facilities/facility/apply-rule/facility-apply-rule-form.provider.ts:72-83`.
- 🔵 **Low — `...form.value` spreads still put the whole form into api objects.** Disabled form-only controls stay
  out of the payload only because they are disabled, so any move to `getRawValue()` leaks them. *Why:* same shape
  of defect as the eligibility-details `name` leak. *Where:*
  `src/app/requests/common/underlying-agreement/facility/target-composition/transform.ts:19`,
  `.../underlying-agreement-application/.../facility/apply-rule/facility-apply-rule.component.ts:158`,
  `.../underlying-agreement-application/.../facility/extent/facility-extent.component.ts:140`.
- 🔵 **Low — `cca-file-input` dereferences an optional `downloadUrl`.** `this.downloadUrl()(uuid)` throws inside
  change detection for any consumer that does not bind the input; all 55 current usages bind it. *Why:* latent for
  the next consumer. *Where:* `src/app/shared/components/file-input/file-input.component.ts:57,133`.
- 🔵 **Low — The 240×140 signature check is best-effort.** `maxImageDimensionsSize` passes when the browser never
  reports dimensions — the normal outcome for a bitmap it cannot decode — so an oversized regulator signature is
  accepted client-side. *Why:* needs a server-side check, or a probe that reads the header instead of decoding.
  *Where:* `src/app/shared/components/file-input/file-validators.ts:69-88`, consumer
  `src/app/regulators/regulators-users-tab/details/details.form.ts:103`.
- 🔵 **Low — File rows are keyed by `file` identity rather than a stable id.** Two nameless entries with the same
  uuid render rows with the same `key`, which Angular reports as NG0955 and can reuse on update. *Why:* dev-only
  warning today. *Where:* `src/app/shared/components/file-upload-list/file-upload-list.component.ts:41`.
- 🔵 **Low — `''` and `null` are both used for a cleared extent file.** The application family sends `''` for the
  four required extent files, the review and variation families send `null`; the swagger marks the fields
  non-nullable. *Why:* the api has to tolerate both. *Where:*
  `.../underlying-agreement-application/.../facility/extent/facility-extent.component.ts:141-145` vs
  `.../underlying-agreement-review/.../facility/extent/facility-extent.component.ts:139`.
- 🔵 **Low — The multiple-file size hint is inline, so its button can sit beside it.** The same layout defect the
  single-file input just had; the custom hint span in the same template is inline too. *Where:*
  `src/app/shared/components/multiple-file-input/multiple-file-input.component.html:5,11`,
  `src/app/shared/components/file-input/file-input.component.html:5` (the unused `text()` branch).
- 🔵 **Low — Only the application family has eligibility-details/extent/apply-rule specs.** Their four copies in
  the review and variation families are duplicated payload builders with no test to catch a divergence.
  *Where:* `src/app/requests/tasks/underlying-agreement-{review,variation,variation-regulator-led,variation-review}/subtasks/manage-facilities/facility/`.
- 🔵 **Low — `selectFacility(id)` branches in spec mocks are dead.** `underlyingAgreementQuery.selectFacility`
  returns a new closure per call, so `selector === underlyingAgreementQuery.selectFacility(id)` never matches, the
  fallback signal is used and the form is never hydrated — the assertion then holds for the wrong reason.
  *Where:* `.../facility-apply-rule.component.spec.ts:95`, `.../facility-eligibility-details.component.spec.ts:71`,
  `.../facility-extent.component.spec.ts:83`.

---

## Accessibility

### Open defects

- 🔵 **Low (WCAG 4.1.3) — A field error added after a valid submit is not announced.** A save can fail
  server-side and attach the error to a control after the form passed its own validation, so nothing is
  announced: the message renders inline and correctly associated, but the page's only `role="alert"` region
  (the error summary) is revealed only when the form is invalid *at submit*. Affects the 2FA invalid code
  (`change-2fa`), invalid OTP (`submit-otp`) and a failed CSV upload (`tpr-csv-upload-process` /
  `pat-csv-upload-process`).
  A fix was prototyped in 2026-09 and rejected: `cca-wizard-step` watching `statusChanges` for the first
  invalid transition after a submit reacted to editing rather than to the request (inputs stay enabled while a
  request is in flight — `PendingButtonDirective` disables the button only, and only 4 files call
  `trackRequest()`), so clearing or typing in a field popped the summary, marked every control touched and
  pulled focus out of the input being edited; and a rejection whose `setErrors` lands while the form is already
  invalid was missed. Constraints established for the next attempt, all reproduced by tests:
  1. Only one `role="alert"` region may exist per page, or the same problem is announced twice.
  2. A page-owned summary bound to the wizard's form is redundant: the wizard's summary already lists every
     control error, including server-set ones.
  3. Page-owned summaries double up in one reachable case: when a resubmit is blocked by the wizard (the form
     is still invalid from the previous server error), the page's failure handler never runs, so its display
     flag cannot be cleared and both summaries render.
  4. `ErrorSummaryComponent` moves focus only on mount (see the next finding).
  5. A fully `DISABLED` form must not submit (`cca-wizard-step` emits `formSubmit` only for `VALID`).

  Recommended direction: keep the summary owned by `cca-wizard-step` and expose an explicit entry point
  (`showErrorSummary()`) that the failing page calls from its own error branch — one owner, no status watching,
  no double region; the cost is a per-page call that can be forgotten. Caller-owned summaries (the
  `mi-reports` / `regulators-users` / notify-operator pattern) only work for pages whose form is not the
  wizard's.
- 🔵 **Low (WCAG 1.3.1, 4.1.1) — Duplicate static IDs inside `govuk-components`.** `notification-banner` and
  `error-summary` hardcode their title ids, so two instances on one page produce duplicate ids and
  `aria-labelledby` can resolve to the wrong element. The app-side copy in
  `src/app/shared/components/error-summary` has the same hardcoded id, and the wizard summary and a page
  summary can render together. *Where:* `projects/govuk-components/src/lib/notification-banner/notification-banner.component.html:10`,
  `projects/govuk-components/src/lib/error-summary/error-summary.component.html:3`,
  `src/app/shared/components/error-summary/error-summary.component.html:2`.
- 🟡 **Medium — Focus does not move to an error summary that is already mounted.**
  `ErrorSummaryComponent` calls `container().focus()` only in `ngAfterViewInit()`. On `@if`-gated pages that is
  enough for the first reveal (`cca-wizard-step`, `submit-otp`, `mi-reports/custom`, `mi-report-form`), but not
  for a *second* failure on the same page (the region is already mounted and only its list re-renders), and not
  at all on pages that mount the summary unconditionally (`operator-user-invitation-details`,
  `edit-sector-user-details`, `edit-sector-association-details` and the other `edit-*` / `manage-facilities`
  pages). In both cases the region is (re)inserted, so the errors are announced and only the focus move is
  missing. *Why:* WCAG 2.4.3 focus order and the GDS error-summary pattern require focus on the summary.
  *Fix:* transfer focus reactively when errors transition from empty to non-empty. *Where:*
  `projects/govuk-components/src/lib/error-summary/error-summary.component.ts:64-68`. Shared library, so it
  needs its own ticket.
- 🟡 **Medium (WCAG 2.4.3) — Focus is lost when a row is removed.** The SIC-code, corrective-action and fuel
  list components `removeAt`/emit and never move focus, so focus falls to `<body>`; nothing in the app restores
  it after a list removal. *Fix:* move focus to the next surviving row's control, or to the Add button when the
  last row goes. *Where:* `target-unit-details-input.component.ts:66`,
  `corrective-actions-summary-details.component.ts:36`, `energy-fuel-amount-details.component.ts:157`.
- 🟡 **Medium — Product-draft wizard routes throw when opened in a fresh tab.**
  `BaselineEnergyDraftService` is route-scoped and initialised only by the *sibling*
  `baseline-energy-consumption` form provider, so deep-linking to `add-product` leaves the draft `null` and
  every mutating helper (an immer recipe over that `null`) throws
  `TypeError: Cannot set properties of null`. Affects `add-product`, `delete-product`, `exclude-product` and
  `undo-product`. *Fix:* make the draft self-seeding — call `initializeFromStore` from the add-product form
  provider, or have the helpers seed from the store when the draft is null. *Where:*
  `src/app/requests/common/underlying-agreement/facility/baseline-energy-consumption/baseline-energy-draft.service.ts`
  and `.../baseline-energy-consumption-form.provider.ts:40`. Touches `@requests/common`, so it wants its own
  ticket.
- 🔵 **Low — Repeated Remove/Delete controls have no item-specific name.** Corrective actions render `Remove`
  with no context while the neighbouring Change link carries
  `<span class="govuk-visually-hidden"> corrective action {{ i + 1 }}</span>`; SIC codes and fuels are the
  same. *Where:* `corrective-actions-summary-details.component.html:36` (contrast `:29-31`),
  `target-unit-details-input.component.html:34`, `energy-fuel-amount-details.component.html:127`.
- 🔵 **Low (WCAG 4.1.3) — `netzPendingButton`'s pending state is not announced.** *Why:* a keyboard or
  screen-reader user gets no confirmation that the request started. *Where:*
  `projects/common/directives/pending-button/pending-button.directive.ts` and its consumers.
- 🔵 **Low — Remaining decorative `.govuk-label` usage.** *Where:* `change-amount.component.html`,
  `change-certification-status.component.html`, add-product's
  `<p class="govuk-label govuk-label--s">Energy intensity</p>`, and `additional-info`'s
  `<p class="govuk-label--m">`. These should be headings or plain text, not label classes.
- 🔵 **Low — Service-name consolidation is incomplete.** The full-name prose sites (header, accessibility
  statement, privacy notice) still hardcode the service name instead of using the
  `serviceName` / `serviceNameShort` constants. *Where:* `src/environments/environment.ts`,
  `environment.prod.ts` and the templates that duplicate the strings.
- 🔵 **Low — CCA-1012 drag-drop variant ticket stays open.** The single-file variant is done (the visible
  `govuk-file` "Choose file" button carries `aria-labelledby="l.<id> ld.<id>"`); for the drag-drop variant the
  accessible name already contains "Choose files", but the ticket should stay open and the accessibility
  statement should note the difference.

### `govuk-components` API and README gaps

- **Label-API inconsistency:** `date-input`, `radio` and `checkbox` have no `labelHidden`;
  `FileUploadComponent.labelHidden` is orphaned (no consumer); `file-upload/README.md` says `Upload a file`
  where the code renders `Upload file`.
- **Undocumented inputs:** `labelHidden` is missing from the `select`, `text-input` and `textarea` READMEs
  under `projects/govuk-components/src/lib/`; `select` also omits its `hint` input.
- **No drag-drop variant of `govuk` file-upload:** the library component renders the standard GDS pattern
  (visible native input + label + hint + error) and cannot replace `cca-multiple-file-input` as-is, so
  consolidating the two is still blocked.

### Browser verification queue

The whole audit was a source audit plus `govuk-components` unit tests, with no browser or assistive-technology
pass. Still to verify in a browser: **2.5.8 Target Size** (Delete/Change buttons), **2.4.11 Focus Not
Obscured**, **3.2.6 Consistent Help**, and a **3.3.7 full journey run**.

### Redesign in flight

- **`HighlightDiffComponent` still uses the serialized-DOM + `html-diff-ts` pipeline.** The structural redesign
  is decided but not implemented. *Why:* the current pipeline matches text from different rows and relies on
  `innerHTML`; a11y of the added/removed batches must not rely on colour alone. Full design and the seven
  implementation steps: [decisions/highlight-diff-design.md](decisions/highlight-diff-design.md). *Where:*
  `src/app/shared/components/highlight-diff/` (32 templates consume it), `html-diff-ts` in `package.json`.

---

## Auth — `angular-oauth2-oidc` migration (assessment in [decisions/angular-oauth2-oidc-migration.md](decisions/angular-oauth2-oidc-migration.md))

- 🟡 **Medium — the migration is blocked on a product decision.** Moving from `keycloak-js`'s `check-sso` to
  `angular-oauth2-oidc` loses silent SSO: `loadDiscoveryDocumentAndLogin()` bounces anonymous users to the
  Keycloak login page. Emulating `check-sso` with iframe silent refresh fights browser cookie restrictions. If
  silent SSO for pre-authenticated users is a hard requirement, staying on `keycloak-js` is justified.
- 🟡 **Medium — the timeout banner needs reworking for the migration.** The library exposes only a raw
  `getRefreshToken()` with no parsed `exp`/`iat`, so the facade must decode the refresh JWT and re-publish it
  after every refresh; today the banner reads `refreshTokenParsed` (`keycloak.service.ts:54-56`).
- 🟡 **Medium — refresh dedupe has to be re-implemented.** The library has no in-flight refresh dedupe; the
  keycloak-js `refreshQueue` behaviour must be rebuilt over `refreshToken()`, or the loser of two concurrent
  refreshes presents a consumed token (`400 invalid_grant`). *Depends on* confirming whether the `uk-pmrv`
  realm rotates refresh tokens single-use.
- 🔵 **Low — `createLoginUrl`'s per-call `redirectUri` has no direct equivalent.** The library's builder is
  `protected` and async, so the facade must set and restore `oauthService.redirectUri` (or use
  `customQueryParams`). The app relies on that override (`auth.service.ts:40-49`).
- 🔵 **Low — cross-tab logout detection is lost** unless `sessionChecksEnabled` is turned on (off by default).
- 🔵 **Low — `KeycloakProfile` leaks into `projects/common`.** `auth.state.ts`, `auth.selectors.ts` and
  `auth.store.ts` import it; replace it with a local interface as part of the migration.
- **Plan of record:** spike branch re-implementing the facade over `OAuthService` and verifying sign-in,
  refresh, logout, the forgot-password link and the timeout banner against the live realm.

---

## TPR digital form

Frontend and backend review findings for the TPR digital form. Frontend bullets abbreviate paths under
`src/app/requests/tasks/target-period-reporting-form/` (components, providers, guards) and
`src/app/requests/common/target-period-reporting/` (`utils.ts`, shared helpers). The base-year gap analysis is in
[tpr-digital-form/product-base-year-below-facility-gap.md](tpr-digital-form/product-base-year-below-facility-gap.md);
the workflow and data model in [tpr-digital-form/](tpr-digital-form/).

### Frontend

- 🔴 **Critical — C1: by-product throughput rows report 0 for products the user never saw.**
  `buildByProductThroughputDetails` loops all baseline products and writes
  `actualThroughput: String(savedProduct?.actualThroughput ?? '0')`, so a brand-new product silently reports
  zero; the zero-energy guard was fixed on 2026-09-18, this part was not. *Where:*
  `tpr-throughput-details-check-your-answers.component.ts:118-169`.
- 🔴 **High — C1b / CCA-3226: cleared non-standard fuel CO₂ factors are confirmed without re-validation.** The
  energy/fuel subtask can be completed and its check-your-answers page confirmed while the data is still
  incomplete: `energy-fuel-amount-redirect.guard.ts:13` sends any `IN_PROGRESS` section straight to
  check-your-answers, and that page's `onSubmit` marks the section `COMPLETED` without re-deriving or
  re-validating anything. Cleared factors then render as 0 in the confirmed data. *Why:* a user can confirm
  numbers that were never computed from valid input. The throughput twin of this guard has since been fixed
  with `isThroughputDataConfirmable`; the energy side has not. *Where:*
  `energy-fuel-amount-redirect.guard.ts:13`,
  `energy-fuel-amount-details-check-your-answers.component.ts:85-110`.
- 🟡 **High — C2: the summary page and the check-your-answers save path disagree about which products
  exist.** The summary filters to products with `savedProduct?.actualThroughput != null`, while the save path
  writes rows for all baseline products, so a product is reported as 0 without ever appearing on the page the
  user confirmed. (Originally recorded as Critical; the review that followed downgraded it to High.) *Where:*
  `throughput-details-summary.component.ts:105`,
  `tpr-throughput-details-check-your-answers.component.ts:133`.
- 🔴 **High — H1: `submit` is reachable with both subtasks incomplete.** The submit route carries only
  `isEditableGuard`, so a user can type `/submit` directly and fire `CALCULATE_RESULTS` on incomplete data.
  *Why:* the server recalculates from invalid input. *Where:* `target-period-reporting-form.routes.ts:22-24`.
- 🔴 **High — H2: `uniqueCustomFuelTypeValidator` puts the error at `FormArray` level.** The summary renders
  it, but there is no per-row inline error and the summary anchor focuses nothing; the wording also differs
  from the spec ("Enter a unique name for this fuel."). *Why:* the user cannot see which fuel row is wrong.
  *Where:* `energy-fuel-amount-details-form.provider.ts:106-116`.
- 🔴 **High — H3: throughput inputs default to `''`/`null` but carry `required`.** The spec says the default is
  `0`, so the required error fires on every new entry. *Where:*
  `tpr-throughput-split-by-product-form.provider.ts:63,66-69`, `tpr-throughput-totals-only.component.ts:70-73`.
- 🔴 **High — H5 (unverified): `TPRDF1007` is not mapped.** The code is absent from
  `TpReportingSubmitErrorCode`, and the workflow spec's submit error table does not list it, so the claim may
  be wrong. *Why:* if reachable, the user gets a dead submit button with no message. *Action:* confirm
  reachability with the backend first; do not map error codes speculatively. *Where:* `tp-reporting-errors.ts`.
- 🔴 **High — H6: `err.error.code` is read unguarded in the submit action.** On a network failure `err.error` is
  a `ProgressEvent`, so `err.error.code` is `undefined` and no message is produced: the user gets nothing and
  the submit button stays live. *Why:* a failed submit looks like a dead one. *Where:*
  `tpr-form-submit-action.component.ts:85-93`.
- 🔴 **High — H7: values are displayed with fewer decimals than they are stored.** Adjusted throughput and
  target energy show 3 decimals while stored at 7, and the spec wants the value displayed down to its last
  non-zero decimal. *Why:* shown values differ from saved values. *Where:*
  `tpr-throughput-split-by-product.component.html:66,72`, `throughput-details-summary.component.html:35,39`,
  `tpr-throughput-totals-only.component.html:38`.
- 🟡 **Medium — M1: the expired page hardcodes "interim" wording** instead of parameterising on `reportType`.
  *Why:* wrong wording if a final report expires. *Where:* `tpr-form-submit-expired.component.ts:12`.
- 🟡 **Medium — M2 (unverified): `TPRDF1009` is missing from `TpReportingSubmitErrorCode`.** It is already
  mapped in `TP_REPORTING_REFRESH_ERROR_MESSAGES` but absent from the spec's submit error table. *Action:*
  confirm with the backend before adding it to the submit messages. *Where:* `tp-reporting-errors.ts:4`.
- 🟡 **Medium — M3: target-period boundary years are hardcoded in the frontend** (2026/2028/2030) while the
  backend reads them from the database. *Why:* drift when boundaries change. *Where:*
  `common/target-period-reporting/utils.ts:305-310`.
- 🟡 **Medium — M4: mock data contains a CHP value with `usedReportingMechanism: false`.** *Why:* inconsistent
  fixtures can mask SRM-related regressions. *Where:* `mock-data.ts:189`.
- 🟡 **Medium — M5: the `TPRDF1004` (locked) error has an empty `message` and `link`** while `linkText` is
  populated, so the link text renders over `routerLink=""` — visible text, inert link. *Where:*
  `tp-reporting-errors.ts:28-33`.
- 🟡 **Medium — M6: the `TPRDF1008` error renders the submit button next to the refresh link,** so the user can
  re-click submit in a loop. *Where:* `tpr-form-submit-action.component.ts:84-98`.
- 🟡 **Medium — M7: `toTotalsOnlySummaryData` still omits spec-required rows.** The adjusted-throughput row
  has since been added, but baseline intensity and improvement % are still missing, for the totals-only *and*
  fixed-energy-only variants, while the spec's check-your-answers page requires them. *Where:*
  `throughput-details-summary-data.ts:127-157`.
- 🟡 **Medium — M8: the 70% rule radios are ordered No/Yes** instead of the spec's and GDS's Yes/No. *Where:*
  `energy-fuel-amount-details.component.html:165-166`.
- 🟡 **Medium — M9: `formatPercentage` infers its unit from the magnitude** (`value > 1 ? value : value * 100`),
  so `0.5` is ambiguous between 0.5% and 50%. *Why:* fragile; currently only saved by the server
  pre-formatting. *Where:* `results-summary-data.ts:29`.
- 🟡 **Medium — M10: no `productStatus == LIVE` filter in the TPR reference-data path.** Only the base-year
  filter is applied, while the spec's eligibility rule requires Live status; the refresh table's "Product
  excluded" row suggests excluded products stay visible. *Why:* needs a product decision before changing
  anything. *Where:* the frontend TPR reference-data path and the backend `setProducts` mapper (see BL3).
- 🔵 **Low — L3: `isEditableGuard` blocks locked users silently,** so the `TPRDF1004` message is never seen.
  *Why:* no explanation is shown. *Where:* `target-period-reporting-form.routes.ts`.
- 🔵 **Low — L4: the `TPRDF1004` error keeps an active submit button** alongside it — the same retry loop as
  M6. *Where:* the button is chosen in `tpr-form-submit-action.component.html:88-96`, and the message branch at
  `tpr-form-submit-action.component.ts:90` covers only TPRDF1002/1005, so TPRDF1004/1008 stay live.
- 🔵 **Low — L5: `resolveCalculatedResults` is a dead-code identity function.** *Where:* `utils.ts:45-62`.
- 🔵 **Low — L6: the non-standard fuel CO₂ factor is not unit-converted in the carbon path.** Marked benign and
  currently correct for kWh-based carbon facilities, but latent wrongness for non-kWh data. *Where:*
  `utils.ts:555-580` (carbon branch of `calculateWeightedConversionFactor`).
- 🔵 **Low — L7: the API type `PerformanceDataFacilityNonStandardFuel` lacks
  `primaryEnergyConversionFactor`,** which blocks a clean fix in the C1 area. *Where:* `cca-api` model.
- 🔵 **Low — L8: the SRM consistency validator closes over the form reference,** so it dies silently if the
  form is recreated. *Where:* `energy-fuel-amount-details-form.provider.ts:71-105`.
- 🔵 **Low — L9: `detailsLink` is not explicitly bound in the CYA template** and works by accident. *Why:*
  breaks on template refactor. *Where:* `energy-fuel-amount-details-check-your-answers.component.ts:31-36`.
- 🔵 **Low — L10: the TAF is saved as a snapshot on the energy page,** creating a second source of truth.
  *Why:* divergence risk on recalculation. *Where:* `energy-fuel-amount-details.component.ts:222-223`.
- 🔵 **Low — L11: dead code — `targetPeriodYear` is read but unused** in the check-your-answers and summary
  components.
- 🔵 **Low — L12: the form stores raw `product.energy`** while the display uses
  `resolveProductEnergyCarbonIntensity`. *Why:* cosmetic data mismatch. *Where:*
  `tpr-throughput-split-by-product-form.provider.ts:63`.
- 🔵 **Low — L13: `sumDeliveredTimesPrimaryFactor()` uses the CO₂ factor as the primary factor** for
  non-standard fuels instead of `1.0`. Transitively dead (its only caller has no production callers), but wrong
  if revived. *Where:* `utils.ts:510` (called at `:530-531`).
- 🔵 **Low — L14: `calculateWeightedConversionFactor`'s CARBON_TONNE branch divides by 1000** where the spec
  requires multiplying by 1000. Dead code, wrong units if revived. *Where:* `utils.ts:563-564`.
- 🔵 **Low — L15: `resolveFacilityBaselineYear` infers the facility base year as `Math.min(...baseYear)`,**
  which disagrees with the canonical `baselineAndTargets.baselineYear`. Only called from tests. *Where:*
  `utils.ts:628-630`.
- 🔵 **Low — L16: two intensity paths disagree.** One prefers a derived `baselineVariableEnergy /
  totalThroughput`, the other the stored `energyCarbonIntensity`, while the spec says "as stored" in both
  places. *Why:* silent disagreement if the backend stores a different value. *Where:*
  `utils.ts:381-385` vs `utils.ts:348-359`.
- 🔴 **High — L17: three sites read `baselineAndTargets.baselineYear`, a field that exists only on API branch
  CCA-3248.** Against API `master` the field is `undefined`, the guard fails and every product silently gets
  the plain facility target instead of its adjusted per-product target. *Requirement:* CCA-3248 must ship to
  each environment before or with the web build. *Where:* `tpr-throughput-details-check-your-answers.component.ts:85`,
  `tpr-throughput-split-by-product.component.ts:139`, `throughput-details-summary.component.ts:88`.
- 🟡 **Medium — Spec gap: product base year below the facility base year (CCA-3245) is not rejected.** Nothing
  rejects an invalid product: the frontend's `calculateAdjustedImprovementTarget` still returns the plain
  facility target for `productBaseYear <= facilityBaseYear`
  (`common/target-period-reporting/utils.ts:297`), and the backend clamps the numerator with
  `.max(BigDecimal.ZERO)`, which produces the same collapse; `0/0` is possible when `facilityBaseYear == 2026`.
  Current risk recorded as Low. The same analysis carries four more open items: the product decision on blocking
  pre-facility products at validation time (long-term direction), extreme cases exceeding 100% of the facility
  target (needs business validation), missing `baselineAndTargets.improvements` keys reading as 0% improvement,
  and `totalProgress ≥ 1` from corrupt data silently returning 0 — see
  [tpr-digital-form/product-base-year-below-facility-gap.md](tpr-digital-form/product-base-year-below-facility-gap.md).

### Backend

- 🟡 **Medium (partially resolved) — BH1: `TPRDF1006` was fixed frontend-side; `TPRDF1007` and `TPRDF1009` are
  still unverified on submit.** *Why:* an unmapped code leaves the user with a dead submit button. *Where:*
  `CcaErrorCode.java:98-101`; frontend `tp-reporting-errors.ts`. Rule: do not map error codes on the frontend
  speculatively. Also verify TPRDF1007: the calculation step's validation is claimed but not confirmed to return
  a frontend-visible error.
- 🔴 **High — BH2: `productBaseYear < facilityBaseYear` is not rejected, and the TP7 term can divide by
  zero.** The validator rejects an empty product list, products after the target year and products with no
  matching facility base year, but not a base year below the facility's. When `facilityBaseYear == 2026`,
  `lastYearOfTP7 - facilityBaseYear` is 0 with no guard, giving an `ArithmeticException` and a 500 on
  `CALCULATE_RESULTS`/submit — reachable because a 2026 baseline passes the facility date-eligibility check for
  TP8/TP9. *Where:* `PerformanceDataFacilityValidator.java:160-184`,
  `PerformanceDataFacilityCalculationCommonFunctionUtil.java`, `CommonFunctionUtil:136-138`. Tagged as future
  change request CCA-3245.
- 🔴 **High — BH3: `WEIGHTED_CONVERSION_FACTOR` double-counts CO₂ in the carbon branch.**
  `PRIMARY_ENERGY_STANDARD_FUEL` (already CO₂-inclusive for `CARBON_KG`/`CARBON_TONNE`) is multiplied by the CO₂
  factor again and divided by `actualEnergyCarbon`, yielding `Σ(d·p·CF²) / Σ(d·p·CF)` instead of the spec's
  `Σ(d·p·CF) / Σ(d·p)`. *Why:* the displayed value is wrong (0.138533 vs the spec's 0.126937 on the worked
  example) and the carbon and energy branches contradict each other. *Where:* `FunctionUtil:158-175`. Backend
  fix required.
- 🟡 **Medium — BM1: the refresh validator throws the same `BusinessException` for all four eligibility
  failures** (TPRDF1002/1004/1005/1009) although the spec distinguishes cancel / locked / data-preserved-contact
  regulator. *Why:* "data preserved" happens only because the throw precedes `refreshBaselineData()` — fragile
  under refactor. *Fix:* explicit guard clauses. *Where:*
  `PerformanceDataFacilityDigitalFormRefreshValidator.java:17-24`.
- 🟡 **Medium — BM2: `THROUGHPUT_ADJUSTMENT_FACTOR` is not clamped to [0,1]** (the frontend isn't either).
  Defensive only today. *Fix:* `.max(BigDecimal.ZERO).min(BigDecimal.ONE)`. *Where:*
  `PerformanceDataFacilityCalculationCommonFunctionUtil.java:87-101`.
- 🟡 **Medium — BM3: `validateFacilityEligibility` has no explicit "active on 1 January" check,** covering it
  only implicitly through the target-period-year boundaries. *Why:* the rule is not self-documenting and is
  easy to break. *Where:* `PerformanceDataFacilityValidator.java:84-98`.
- 🟡 **Medium — BM4: the calculation mapper rounds to 7 decimals before storage,** so stored
  `calculatedResults` are pre-rounded if read for further computation. Acceptable today but the contract is
  undocumented. *Where:* `PerformanceDataFacilityCalculationMapper.java:42-63`.
- 🟡 **Medium — BM5: the TAF iterator does not filter null standard-fuel entries** (`getValue() != null`) while
  `TOTAL_STANDARD_FUELS_DELIVERED_ENERGY` does. *Why:* the CSV upload path can NPE. *Where:*
  `CommonFunctionUtil:70`.
- 🔵 **Low — BL1: `reportType` validation is a case-sensitive enum comparison.** No live risk — the frontend
  sends API-generated types. *Where:* `PerformanceDataFacilityValidator.java:56-60`.
- 🔵 **Low — BL2: the in-progress check loads all requests for a facility and filters in memory,** with no
  pagination. Performance only. *Where:* `PerformanceDataFacilityDigitalFormCreateValidator.java:70-78`.
- 🔵 **Low — BL3: `PerformanceDataFacilityReferenceDataMapper.setProducts` filters on
  `baselineYear <= targetPeriodYear` only,** where the spec also requires Live status. Needs the same product
  decision as M10.
- 🔵 **Low — BL4: `baselineYear` deployment coupling with CCA-3248,** and the API's `getBaselineYear()` rolls to
  the next year after 1/2 July, so it is not interchangeable with `baselineDate.getYear()`.
- 🟡 **Medium — Spec gap: TPRDF1009 on submit is not documented.** The spec's submit table lists only
  TPRDF1002/1004/1005/1008, so neither the spec nor the frontend mapping can be updated until reachability is
  confirmed.
