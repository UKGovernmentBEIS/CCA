# Open findings

**Updated:** 2026-11-05

The single list of what still needs fixing, and why. Everything resolved has been removed from the docs —
the history lives in git (`git log -- docs/`). Work in progress and the ticket plan:
[status.md](status.md). Reference material: [architecture.md](architecture.md) and [decisions/](decisions/).

A finding tagged with a Jira key (e.g. `CCA-3502`) is scheduled under that ticket. An untagged finding has no
ticket yet: it waits on an outside answer, or gets fixed when someone next works in that code.

---

## Application review findings

### `govuk-components` migration (ticket to be raised)

`projects/govuk-components` is to be aligned with the shared library in `uk-netz-govuk-components`
(`/home/louk/Projects/netz/uk-netz-govuk-components`), which already follows current Angular and GOV.UK Frontend
patterns. Library findings wait for that task rather than being patched here, because the migration replaces the
components they point at. When a library change in another ticket is unavoidable, keep it minimal and note it here.

- 🔴 **High — `AccordionComponent` writes to child signals from an `effect()`.** The constructor effect
  assigns `this.accordion.id` and calls `item.itemIndex.set(...)` / `item.isExpanded.set(...)` on content
  children. *Why:* bypasses one-way data flow; risks change-detection feedback loops and render-order bugs.
  *Where:* `projects/govuk-components/src/lib/accordion/accordion.component.ts:43-61`. *Fix:* the reference
  `GovukAccordionComponent` provides itself under a token (`useExisting`) and each section derives its index with
  `accordion.indexOf(this)`. It also renames items to `govuk-accordion-section`, uses `model()` for `expanded`
  and restores the remembered state. Today items write `sessionStorage` but never read it back. Moved out of
  `CCA-3505` on 2026-11-05.
- 🔵 **Low — 5 leftover `$safeNavigationMigration` template calls** in `TextareaComponent`. *Why:* obscures
  template intent. *Where:* `projects/govuk-components/src/lib/textarea/textarea.component.html:37-59`. The calls
  in `src` and `projects/common` were removed under `CCA-3505` on 2026-11-05.


### Type safety and strictness

- 🔴 `CCA-3506` **High — `strict` and `strictNullChecks` are disabled at workspace level.** `tsconfig.json` sets
  `strictPropertyInitialization: false`, `strictNullChecks: false`, `useDefineForClassFields: false` and
  `ignoreDeprecations: "6.0"`. *Why:* nullability is assumed away app-wide, producing runtime
  `TypeError: Cannot read properties of undefined` in edge cases. *Where:* `tsconfig.json:4,15-16,39`. Enabling
  strict null checks was started once and abandoned; it needs redoing as a staged effort.
- 🔴 `CCA-3506` **High — Legacy `UntypedFormControl` / `UntypedFormGroup` in the component libraries.** The reusable
  libraries declare untyped controls, groups and builders while feature forms are typed. *Why:* form type
  errors surface only at runtime, and the untyped surface spreads to consumers. *Where:*
  `projects/govuk-components/src/lib/form/form-input.ts:8,41-42`,
  `projects/govuk-components/src/lib/form/form-builder.service.ts:5-6`,
  `src/app/shared/components/wizard/wizard-step.component.ts:27,33`.
- 🟡 `CCA-3506` **Medium — Generated polymorphic payloads are not discriminated unions.** The OpenAPI `discriminator`
  (9 in `swagger.yaml`) is dropped by the `typescript-angular` templates: subtypes extend the base interface
  without narrowing `type` to a literal, so `payload.type === 'AMOUNT_CHANGED'` does not narrow the union and
  consumers fall back to `as` casts or hand-written type guards. *Why:* a new subtype or a wrong branch is not
  caught at compile time. *Where:* `projects/cca-api/src/assets/templates` (fix point), e.g.
  `projects/cca-api/src/lib/model/buyOutSurplusTransactionDetailAmountChangedHistoryPayload.ts`; workaround guard
  in `src/app/buy-out-surplus/transactions-list-tab/transaction-details-container/transaction-history-tab/transaction-history-summary-data.ts`.

### Dates

- 🔵 **Low — `[max]="today"` is frozen when the page is built.** Six components set `today = getToday()` once,
  so a page left open past midnight still caps the date at yesterday. *Fix:* compute today when validating
  (e.g. a shared "not after today" validator). *Where:* `edit-facility-details`, `track-corrective-actions-details`,
  `audit-details`, `non-compliance-details/provide-details`, `pre-audit-review-determination`,
  `pre-audit-review-requested-documents-upload`.
- 🔵 **Low — `DateInputComponent` watches `min`/`max` through `ngDoCheck` and `BehaviorSubject`s.** An `effect`
  or `computed` would do. *Where:* `projects/govuk-components/src/lib/date-input/date-input.component.ts:52-54,87-103`.

### Testing

- 🔵 **Low — Specs mock the Angular Router instead of using real routes.** angular.dev's routing testing guide
  says "Do not mock Angular Router" and to "provide real route configurations and use the harness to
  navigate", because "mocks can hide breaking changes". Most specs stub `ActivatedRoute` with a plain object
  (443 spec files use `{ provide: ActivatedRoute, useValue: ... }`) and spy on `router.navigate` (102 files), so
  a wrong relative path, fragment or resolver wiring still passes; only 10 specs use `RouterTestingHarness`.
  *Why:* navigation and route-data regressions go undetected. *Fix:* migrate app-wide to `provideRouter` with
  real routes plus `RouterTestingHarness`, asserting on `router.url` rather than `navigate` arguments — as one
  effort, not per feature. *Where:* e.g.
  `src/app/sectors/sector/target-units-tab/target-unit/facilities-tab/change-certification-status/change-certification-status.component.spec.ts`;
  reference pattern in `.../facilities-tab/certification-status-tab/certification-status-tab.component.spec.ts`.

### Structure and duplication

- 🟡 **Medium — Parallel table and error-summary components.** `cca-table` vs `govuk-table`, and
  `cca-error-summary` vs `govuk-error-summary`, duplicate sorting/rendering logic. *Why:* every fix has to be
  applied twice — the split of the duplicate-ID finding across both copies is a live example. *Where:*
  `src/app/shared/components/table` vs `projects/govuk-components/src/lib/table`;
  `src/app/shared/components/error-summary` vs `projects/govuk-components/src/lib/error-summary`.
- 🔵 `CCA-3506` **Low — `no-explicit-any` is still `'off'` for `govuk-components`.** *Why:* the library ships untyped code
  without lint pressure, so new `any` slips in unnoticed. *Where:* `eslint.config.js:74,98`.

---

## Facility certification status tab (CCA3)

The certification status moved from the facility details tab to its own "Certification status" tab
(`src/app/sectors/sector/target-units-tab/target-unit/facilities-tab/certification-status-tab/`), grouped by
scheme from `FacilityCertificationStatusDTO.schemeVersion` (CCA-3450).

- 🔵 **Low — Facility's certification start date is not shown read-only.** Following the wireframe, the tab shows
  each CP's own duration, so the facility's effective `startDate` (the date on the Reduced Rate Certificate) is
  only visible to regulators, pre-filled in the change form. *Why:* operators and sector users cannot see from
  when a facility is certified. To confirm with the analyst whether a "Certification start date" row is wanted.
  *Where:* `.../certification-status-tab/certification-status-summary-data.ts`.
- 🔵 **Low — Which periods are listed is ambiguous.** The spec says "current and previous" CPs; the wireframe note
  says "active or past certification periods (up to CP6)". These diverge once CP9 starts. The frontend shows
  whatever `getFacilityCertifications` returns, so the backend owns the filtering. To confirm with the analyst
  and backend.

---

## Out-of-scope findings — CCA-3407 file-upload review

Found while reviewing the file-upload fixes on `fix/CCA-3407`. None of them belong to that work, so they are
recorded here instead of being fixed in the branch.

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

## Out-of-scope findings — received-amount review

Found while comparing the CCA3 buy-out received-amount flow (CCA-3462) with the shared subsistence-fees flow it
resembles. All of them are in the shared flow, not in the CCA3 one. Paths are relative to
`src/app/shared/components/received-amount/`.

- 🟡 **Medium — Amounts go through `toNumber()` before display.** The summary converts `BigNumber` to `number`
  and formats with `DecimalPipe`, whose default keeps at most 3 fraction digits. *Why:* precision loss on large
  amounts, and formatting that differs from the `BigNumber.toFormat(2)` used in buy-out. *Where:*
  `check-your-answers/received-amount-summary.ts:35,38`.
- 🔵 **Low — The sign travels as a string prefix and is parsed back with `split`.** The draft stores
  `'+350'`/`'-350'`, and four places strip the sign with `split('+')[1]`/`split('-')[1]`. *Why:* brittle;
  `BigNumber.abs()`/`isNegative()` say the same thing directly. *Where:* `received-amount-form.provider.ts:65-66`,
  `check-your-answers/received-amount-summary.ts:21`, `history-details/history-details-summary.ts:12`,
  `history-details/history-details.component.ts:20`, `received-amount.component.html:34`.
- 🔵 **Low — The negative-total check is a synchronous check wrapped as an async validator.** It returns `of(...)`
  for every branch. *Why:* async validators leave the group `PENDING` and complicate testing for no gain; a sync
  group validator does the same. *Where:* `received-amount-form.provider.ts:25-47,86`.
- 🔵 **Low — The confirmation page fetches the MoA again just for `transactionId`.** The store already holds it.
  *Where:* `confirmation/confirmation.component.ts:29`.
- 🔵 **Low — `SummaryListRowKeyDirective` is imported twice.** *Where:* `received-amount.component.ts:37-38`.

---

## Out-of-scope findings — CCA-3503 review

Found by the adversarial review of the CCA-3503 fixes. They predate that work; the review's CCA-3503 findings were
fixed on that ticket and the rest landed in `CCA-3505`, except the one below.

- 🔴 **High — Errors thrown into a `subscribe()` without an error callback never reach `ErrorHandler`.** The
  app has been zoneless since CCA-2806 (2026-04-08) and `app.config.ts` does not call
  `provideBrowserGlobalErrorListeners()`, so rxjs reports such errors through `setTimeout` to `window.onerror`
  only; `GlobalErrorHandlingService.handleError` never runs. Under zone.js it did, and the 133
  `catchBadRequest`/`catchElseRethrow`-style call sites still rely on that: every failure they rethrow (400, 404,
  409) now leaves the user on the page with no message and a re-enabled button. *Why:* silent failures across
  the app. *Where:* `src/app/app.config.ts:38-72`, `src/app/shared/services/global-error-handling.service.ts`
  (`handleError`); e.g. `src/app/shared/components/received-amount/check-your-answers/check-your-answers.component.ts:47-49`.
  *Fix (needs its own ticket, moved out of `CCA-3505` on 2026-11-05):* add the listeners, and make `handleError`
  skip `HttpErrorResponse` 401/403/500, which `HttpErrorInterceptor` already handles before rethrowing. Without
  that filter a 403 ends on `/error/500` instead of `landing`, and a 401 flashes the error page before the
  Keycloak redirect. Accepted trade-offs: any other unhandled async error or promise rejection replaces the page
  with the error page and loses unsaved input, as it did under zone.js. Verify by clicking through save flows
  against the backend; specs do not load `app.config.ts`.

---

## Accessibility

### Open defects

- 🔵 **Low (WCAG 4.1.3) — A field error added after a valid submit is not announced.** A save can fail
  server-side and attach the error to a control after the form passed its own validation, so nothing is
  announced: the message renders inline and correctly associated, but the page's only `role="alert"` region
  (the error summary) is revealed only when the form is invalid *at submit*. Affects the 2FA invalid code
  (`change-2fa`), invalid OTP (`submit-otp`) and a failed CSV upload (`tpr-csv-upload-process` /
  `pat-csv-upload-process`).
  CCA-3423 changes the upload hints and their description references on these pages; it does not change
  server-error announcements. This finding remains open independently of hint suppression.
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
  4. `ErrorSummaryComponent` moves focus on mount and after each form submit; a page that sets errors later
     from a server response must call its `focus()`.
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
- 🔵 **Low (WAI-ARIA APG tabs) — Tab panels without focusable content are skipped by keyboard users.** The
  APG tabs pattern says that when a panel has no focusable content "the tabpanel should set `tabindex="0"` to
  include it in the tab sequence of the page"; `govuk-tabs` renders panels without it, so Tab from the tab list
  moves past the panel. Read-only panels hit this — e.g. the facility "Certification status" tab for operator
  and sector users, who get no Change links. *Fix:* add `tabindex="0"` to panels in the library; affects every
  tab set, so it wants its own ticket. *Where:* `projects/govuk-components/src/lib/tabs/tabs.component.html:50`.
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

- 🔴 **High — H5 (unverified): `TPRDF1007` is not mapped.** The code is absent from
  `TpReportingSubmitErrorCode`, and the workflow spec's submit error table does not list it, so the claim may
  be wrong. *Why:* if reachable, the user gets a dead submit button with no message. *Action:* confirm
  reachability with the backend first; do not map error codes speculatively. *Where:* `tp-reporting-errors.ts`.
- 🟡 **Medium — M2 (unverified): `TPRDF1009` is missing from `TpReportingSubmitErrorCode`.** It is already
  mapped in `TP_REPORTING_REFRESH_ERROR_MESSAGES` but absent from the spec's submit error table. *Action:*
  confirm with the backend before adding it to the submit messages. *Where:* `tp-reporting-errors.ts:4`.
- 🟡 **Medium — M3: target-period boundary years are hardcoded in the frontend** (2026/2028/2030) while the
  backend reads them from the database. *Why:* drift when boundaries change. *Where:*
  `common/target-period-reporting/utils.ts:305-310`.
- 🟡 **Medium — M4: mock data contains a CHP value with `usedReportingMechanism: false`.** *Why:* inconsistent
  fixtures can mask SRM-related regressions. *Where:* `mock-data.ts:189`.
- 🟡 **Medium — M9: `formatPercentage` infers its unit from the magnitude** (`value > 1 ? value : value * 100`),
  so `0.5` is ambiguous between 0.5% and 50%. *Why:* fragile; currently only saved by the server
  pre-formatting. *Where:* `results-summary-data.ts:29`.
- 🟡 **Medium — M10: no `productStatus == LIVE` filter in the TPR reference-data path.** Only the base-year
  filter is applied, while the spec's eligibility rule requires Live status; the refresh table's "Product
  excluded" row suggests excluded products stay visible. *Why:* needs a product decision before changing
  anything. *Where:* the frontend TPR reference-data path and the backend `setProducts` mapper (see BL3).
- 🔵 **Low — L3: `isEditableGuard` blocks locked users silently,** so the `TPRDF1004` message is never seen.
  *Why:* no explanation is shown. *Where:* `target-period-reporting-form.routes.ts`.
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
- ✅ **Resolved — Spec gap: product base year below the facility base year (CCA-3245).** The backend now rejects
  TPR create, refresh and submit with `TPRDF1010`, mapped in `tp-reporting-errors.ts`. The frontend
  calculation (`common/target-period-reporting/utils.ts:297`) is unchanged by decision. Pre-facility products
  still render the facility target on tasks created before `TPRDF1010`, but those tasks cannot be refreshed or
  submitted. Still open from the same analysis: `0/0` when `facilityBaseYear == 2026` (backend BH2), missing
  `baselineAndTargets.improvements` keys reading as 0% improvement, and `totalProgress ≥ 1` from corrupt data silently returning 0 — see
  [tpr-digital-form/product-base-year-below-facility-gap.md](tpr-digital-form/product-base-year-below-facility-gap.md).

### Backend

- 🟡 **Medium (partially resolved) — BH1: `TPRDF1006` was fixed frontend-side; `TPRDF1007` and `TPRDF1009` are
  still unverified on submit.** *Why:* an unmapped code leaves the user with a dead submit button. *Where:*
  `CcaErrorCode.java:98-101`; frontend `tp-reporting-errors.ts`. Rule: do not map error codes on the frontend
  speculatively. Also verify TPRDF1007: the calculation step's validation is claimed but not confirmed to return
  a frontend-visible error.
- 🔴 **High — BH2: the TP7 term can divide by zero.** `productBaseYear < facilityBaseYear` is now rejected with
  `TPRDF1010` (CCA-3245). When `facilityBaseYear == 2026`,
  `lastYearOfTP7 - facilityBaseYear` is 0 with no guard, giving an `ArithmeticException` and a 500 on
  `CALCULATE_RESULTS`/submit — reachable because a 2026 baseline passes the facility date-eligibility check for
  TP8/TP9. *Where:* `PerformanceDataFacilityValidator.java:160-184`,
  `PerformanceDataFacilityCalculationCommonFunctionUtil.java`, `CommonFunctionUtil:136-138`.
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
- 🟡 **Medium — Spec gap: the "Effect of Refresh" table does not match the backend.** The spec says a refresh sets
  subtasks to In progress and clears non-standard fuel CO₂ factors (CCA-3226). The backend's `refreshBaselineData`
  instead resets `sectionsCompleted` to `{}`, keeps all energy and throughput inputs, and clears no factors. Section
  statuses are otherwise set only by the frontend, and the backend saves them as sent. The frontend redirect
  guards and check-your-answers pages (CCA-3502) therefore re-check that the data is complete, matching the
  backend submit validation, instead of trusting the status. *Action:* agree with the analyst which behaviour is
  intended and update the spec or the backend. *Where:* `docs/tpr-digital-form/workflow-spec.md` (Effect of
  Refresh on Subtask Statuses), `PerformanceDataFacilityDigitalFormSubmitService.refreshBaselineData`.
