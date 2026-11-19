# UK CCA App Web — Project Status

**Last updated:** 2026-11-05

Tracks only the work that is still open. Resolved work has been removed from the docs — the history is in git
(`git log -- docs/`).

| Concern | Where |
|---------|-------|
| 📋 What still needs fixing, and why | [open-findings.md](open-findings.md) |
| ⏸️ In progress | This file → [⏸️ In progress](#-in-progress) |
| 📦 Remediation tickets (plan of record) | This file → [📦 Remediation tickets](#-remediation-tickets-plan-of-record) |
| Current architecture & patterns reference | [architecture.md](architecture.md) |
| Design decisions not yet implemented | [decisions/](decisions/) |
| TPR digital form — specs, data model, gap analysis | [tpr-digital-form/](tpr-digital-form/) |
| Docs index and conventions | [README.md](README.md) |

---

## ⏸️ In progress

| Item | Status |
|------|--------|
| ⏸️ Branch cleanup | `fix/CCA-3392` … `fix/CCA-3398` are merged; their `origin/` branches can be deleted. |

---

## 📦 Remediation tickets (plan of record)

Open findings are grouped into Jira tickets by the code they change. Each finding in
[open-findings.md](open-findings.md) carries its ticket key; search for the key to get the full list and detail.
Work them in the order below: user-facing defects first, strict mode last.

| Order | Ticket | Title | Findings covered | Notes |
|-------|--------|-------|------------------|-------|
| 5 | `CCA-3506` | Strengthen type safety across the app and libraries | Typed library forms, `no-explicit-any` for `govuk-components`, discriminated unions for API payloads, strict mode | Strict mode last, staged per project: `common` → `cca-api` → `src/app`. `CCA-3505` shrinks the error surface first |

### Not ticketed yet

- **Waiting on the backend:** TPRDF1007/1009 reachability on submit (H5, M2, BH1, spec gap); backend fixes BH2, BH3,
  BM1–BM5.
- **Waiting on product or the analyst:** Live-status filter (M10, BL3); 3/7ths clear-on-load; silent SSO for the
  `angular-oauth2-oidc` migration; certification start-date row and listed periods.
- **`govuk-components` migration (ticket to be raised):** align `projects/govuk-components` with
  `uk-netz-govuk-components`. Library findings wait for it, starting with the `AccordionComponent` effect writes
  moved out of `CCA-3505`. See the `govuk-components` migration section in [open-findings.md](open-findings.md).
- **Global error listeners (ticket to be raised):** errors in a `subscribe()` with no error callback stopped reaching
  `ErrorHandler` when zone.js was removed. Moved out of `CCA-3505` on 2026-11-05 because enabling the listeners
  changes error behaviour across the app. See the High finding under "Out-of-scope findings — CCA-3503 review" in
  [open-findings.md](open-findings.md).
- **Deployment coupling:** L17 needs CCA-3248 to ship before or with the web build.
- **Remaining Lows** without a tag in [open-findings.md](open-findings.md): fix when working in that code.

### PR rules

- One theme per PR, even within a ticket. Do not mix routing fixes with form typing in one PR.
- Keep each PR reviewable. When a finding spans many files (subscriptions, `model()`, `$safeNavigationMigration`),
  split by directory or feature area instead of one giant diff.
- When a finding lands, remove it from [open-findings.md](open-findings.md). When a ticket's last finding lands,
  remove its row here.

---

## Current build & test state (as of 2026-09-22)

| Check | Result |
|-------|--------|
| `yarn build` | ✅ Passes |
| `yarn test:frontend` | ✅ Passes — 642 files / 2720 tests |
| `yarn lint` (all projects) | ✅ Clean — 0 errors (2 pre-existing warnings in the buy-out timeline specs) |
| Format (prettier) | ✅ Clean |
| Editor diagnostics (tsc + ESLint) | ✅ Clean — vitest globals resolved via root `tsconfig.json` types; see [architecture.md](architecture.md) |
