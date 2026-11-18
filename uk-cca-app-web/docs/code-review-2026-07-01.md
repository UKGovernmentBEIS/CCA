# UK CCA App Web — Comprehensive Code Review Report

**Date:** 2026-07-01 (updated 2026-07-08)  
**Scope:** Full codebase — security, architecture, state management, components, infrastructure, and configuration  
**Reviewers:** Automated multi-agent review (4 sub-agents: Security, Store & State, Component & Pattern, Deps & Config)  
**Validation links:**
Every code implementation should be validated against the following guidelines:
1) https://angular.dev/assets/context/llms-full.txt
2) https://angular.dev/assets/context/guidelines.md
3) https://angular.dev/assets/context/best-practices.md

---

## Executive Summary

This review covers the entire UK CCA (Climate Change Agreements) Angular web application. The codebase is generally well-structured, with clean routing architecture, proper lazy loading, and a thoughtful signal-based state management approach using Immer for immutable updates.

**19 of the original 20 findings have been resolved.** This report retains the 1 outstanding item below. A follow-up [lint review](lint-review-2026-07-08.md) was conducted on 2026-07-08 covering 271 `no-explicit-any` warnings.

| Severity | Count |
|----------|-------|
| 🟠 High | 1 |
| ⚠️ Lint (see separate doc) | 271 warnings |

---

## 🟠 HIGH Findings

### H-3: Keycloak-js 25.0.6 is EOL

**File:** `package.json:63`  
**Severity:** 🟠 HIGH  
**Category:** Dependencies

Keycloak-js `~25.0.6` locks to a major version that has reached end-of-life. Security patches in newer versions won't be applied.

> **Note:** The `enableLogging: true` issue (H-3a) has been resolved — `environment.prod.ts` now sets `false`.

**Action:** Plan upgrade to Keycloak 26.x JS adapter (verify compatibility with your Keycloak server version).

---

## 🟡 MEDIUM Findings

*All medium-severity findings have been resolved.* See the [Resolved Findings](#resolved-findings-for-reference) section for details.

---

## ✅ Positive Observations

The following patterns and practices deserve recognition:

| # | Observation |
|---|-------------|
| 1 | **PKCE enabled** — `pkceMethod: 'S256'` protects against authorization code interception |
| 2 | **Bearer token auth mitigates CSRF** — no `withCredentials` set, tokens not auto-sent by browsers |
| 3 | **No `eval()` / `new Function()` usage** anywhere in the codebase |
| 4 | **No hardcoded API secrets** in environment files |
| 5 | **Good signal-based store architecture** — Immer `produce()` for immutable updates is well-chosen |
| 6 | **Proper lazy loading** — all 30+ task types use `loadChildren` with dynamic imports |
| 7 | **New control flow adoption** — templates migrating to `@if`/`@for`/`@switch` with `track` |
| 8 | **Consistent form provider pattern** — `InjectionToken`-based form providers across all subtasks |
| 9 | **Guard deactivation cleanup** — `getRequestTaskPageCanDeactivateGuard()` calls `store.reset()` |
| 10 | **Centralized `NOTFOUND1001` error handling** in `TasksApiService` |
| 11 | **`highlight-diff` properly uses `DomSanitizer.sanitize()`** for HTML diffs |
| 12 | **`strictTemplates: true` enabled** despite general strict flags being off |
| 13 | **Consistent refactoring toward the new direct-API-call pattern** documented in `AGENTS.md` |

---

## Recommended Action Priority

| Priority | Finding | Effort |
|----------|---------|--------|
| 🟠 H-3b | Upgrade Keycloak-js to v26.x | 1 week |
| 🟡 Lint | Address `no-explicit-any` warnings per [lint review](lint-review-2026-07-08.md) Phase 1 | ~1 hour |

---

## Resolved Findings (for reference)

The following findings have been fully resolved and removed from the active report:

| ID | Resolution |
|----|-----------|
| C-1–C-5 | All critical findings resolved (SonarQube token, source maps, orphaned subscriptions, strict mode, userIsAssigneeGuard) |
| H-1 | `[innerHTML]` replaced with `{{ }}` interpolation in task-list |
| H-2 | Security headers added to nginx config |
| H-3a | Keycloak `enableLogging: false` in production |
| H-4 | Store `reset()` uses `structuredClone` consistently |
| H-5 | Dockerfile: Alpine, `USER nginx`, `HEALTHCHECK` |
| H-6 | `SendForPeerReview` uses centralized `TasksApiService` |
| H-7 | Deferred — global error handler in place |
| M-1 | `OnPush` added to `AppComponent` |
| M-2 | Selectors pre-created as class properties in `RequestTaskPageComponent` |
| M-3 | `[innerHTML]` replaced in govuk-components hints |
| M-4 | All `console.error` calls migrated to `logger.error()` or `isDevMode()` guard |
| M-6 | ESLint `no-explicit-any`: `'warn'` in main app and common, `'off'` in govuk-components and cca-api. 271 warnings catalogued in [`docs/lint-review-2026-07-08.md`](lint-review-2026-07-08.md) — 32 fixable, 108+ need eslint-ignore, 2 should use `unknown`, 3 need broader refactors. |
| M-7 | `SignalStore.updateState` uses `structuredClone` |
| M-8 | Deferred — 0.18.5 is latest version |
| M-9 | `--frozen-lockfile` in CI |
| M-10 | Deferred |
| M-11 | `selectTaskReassignedTo` uses `createSelector` |
| M-12 | 12 common MIME types added to `commonFileValidators` |
| L-1–L-5 | All low findings resolved |

---

## Methodology

This review was conducted by 4 specialized sub-agents examining:

1. **Security** — XSS, CSRF, authentication, authorization, crypto, file upload, secrets, CSP, headers
2. **Store & State** — SignalStore architecture, immutability, memory leaks, selectors, Immer usage, RX interop
3. **Components & Patterns** — Component lifecycle, subscriptions, form providers, guards, routing, error handling, template patterns
4. **Dependencies & Configuration** — npm packages, TypeScript config, ESLint, build setup, Docker, nginx, CI/CD, budgets

Each agent reviewed its assigned files in full, cross-referencing patterns across the codebase. Findings were then synthesized, de-duplicated, and prioritized in this consolidated report.

---

*Report generated 2026-07-01. Updated 2026-07-08 — 19 of 20 findings resolved; M-6 (ESLint) re-evaluated and tracked in [lint-review-2026-07-08.md](lint-review-2026-07-08.md). Remaining: H-3b (Keycloak upgrade).*
