# Housekeeping

This file tracks inconsistencies and cleanup items that have not yet been prioritised. Issues should be raised as Jira tickets (label: `Technical Tasks`) before work begins.

---

## Dependencies

We aim to minimise external dependencies to keep installs fast and the project simpler.

---

## Duplicate Code

1. Why do we need a `cca-radio-option` component? The `netz` (govuk-components) library already has a radio option component.

---

## Code Inconsistencies — Possible Refactors

1. File inputs should declare accepted MIME types via the `accept` attribute so the browser file picker filters appropriately. Example for Excel:
   ```html
   accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
   ```

---

## Code Smells

1. Some directives contain HTML templates — these should be components. Similarly, some components use directive-style template syntax — these should use component syntax.

---

## Post-Refactoring Cleanup

1. **Remove unused old-pattern code** from `projects/common/forms/` — the abstract `TaskService`, `PayloadMutatorsHandler`, `SideEffectsHandler`, `WizardFlowManager`, and associated providers have zero production consumers. See `docs/refactor.md` for the full list.

2. **Clean up ~22 spec files** that still import and mock the old `TaskService` even though the components no longer use it.
