# UK CCA App Web — Project Status

**Last updated:** 2026-09-22

Tracks only the work that is still open. Resolved work has been removed from the docs — the history is in git
(`git log -- docs/`).

| Concern | Where |
|---------|-------|
| 📋 What still needs fixing, and why | [open-findings.md](open-findings.md) |
| ⏸️ In progress | This file → [⏸️ In progress](#-in-progress) |
| 📦 Remediation batches (plan of record) | This file → [📦 Remediation batches](#-remediation-batches-pr-plan) |
| Current architecture & patterns reference | [architecture.md](architecture.md) |
| Design decisions not yet implemented | [decisions/](decisions/) |
| TPR digital form — specs, data model, gap analysis | [tpr-digital-form/](tpr-digital-form/) |
| Docs index and conventions | [README.md](README.md) |

---

## ⏸️ In progress

| Item | Status |
|------|--------|
| ⏸️ Branch `fix/CCA-3397` — mixed fixes | Head `8e478f8ee`, pushed (`origin/fix/CCA-3397` in sync): one commit, rebased onto `master` (`a54defd2d`) and then extended. It fixes the 2FA invalid-code path on `change-2fa`, drops an obsolete workflow-history mapping, and fixes the forgot-password confirmation never appearing on `submit-email` (plain view flags notify no zoneless change detection). Only the PR is left. |
| ⏸️ Batch #1 — Type safety quick fixes | PR `feat/CCA-3338` open for review; the `futureDateValidator` fix from the same batch is still to do ([open-findings.md](open-findings.md)). |
| ⏸️ Branch cleanup | `fix/CCA-3392` … `fix/CCA-3396` are merged and can be deleted, with their `origin/` counterparts. |

---

## 📦 Remediation batches (PR plan)

Open findings are resolved in themed batches, each sized to review as a single PR. Order: quick wins and
low-risk changes first; strict mode last. Each batch row names its findings; the detail is in
[open-findings.md](open-findings.md).

| # | PR theme | Findings covered | Notes |
|---|----------|------------------|-------|
| 1 | Type safety quick fixes | `futureDateValidator` string-vs-Date | the `as File` casts ship with PR `feat/CCA-3338` |
| 2 | Routing cleanup | Dynamic barrel imports, 10 static content pages, `withComponentInputBinding()` | One theme: routing and lazy loading |
| 3 | SignalStore fixes | `rxSelect` per-call observable, `updateState` `structuredClone` | `common` library only |
| 4 | `$safeNavigationMigration` cleanup | 105 template artifacts | Mechanical |
| 5 | Typed forms (app + common) | `UntypedFormControl`/`UntypedFormGroup` | |
| 6 | `model()` two-way bindings | Two-way bindings not using `model()` | Mechanical |
| 7 | Unmanaged subscriptions | 370+ `.subscribe()` without `takeUntilDestroyed` | Split by area: `requests`, then `sectors` + `shared`, then the remainder |
| 8 | Housekeeping | Directory typo, syntax smells | Raise Jira tickets (label `Technical Tasks`) before work begins |
| 9 | Strict mode | `strict` + `strictNullChecks` | Last: batches 1–8 shrink the error surface first. Stage per project: `common` → `cca-api` → `src/app` |

### Batch rules

- One theme per PR. Do not mix routing fixes with form typing in one PR.
- Keep each PR reviewable. When a finding spans many files (subscriptions, `model()`, `$safeNavigationMigration`),
  split by directory or feature area instead of one giant diff.
- Finish a batch before starting the next. When a finding lands, remove it from
  [open-findings.md](open-findings.md).

---

## Current build & test state (as of 2026-09-22)

| Check | Result |
|-------|--------|
| `yarn build` | ✅ Passes |
| `yarn test:frontend` | ✅ Passes — 642 files / 2720 tests |
| `yarn lint` (all projects) | ✅ Clean — 0 errors (2 pre-existing warnings in the buy-out timeline specs) |
| Format (prettier) | ✅ Clean |
| Editor diagnostics (tsc + ESLint) | ✅ Clean — vitest globals resolved via root `tsconfig.json` types; see [architecture.md](architecture.md) |
