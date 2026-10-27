# Docs

Documentation for the UK CCA web application, organised by concern:

| Concern | Where | Description |
|---------|-------|-------------|
| 📋 **Open findings** | [open-findings.md](open-findings.md) | The single list of what still needs fixing, and why — application, accessibility and TPR findings |
| 📊 **Current state** | [status.md](status.md) | Work in progress, the remediation batch plan, and the build/test state |
| 🏗️ **Architecture** | [architecture.md](architecture.md) | Living reference for the current patterns (direct API submission, form providers, route guards, pitfalls) |
| ✅ **Decisions** | [decisions/](decisions/) | Design assessments and decisions that are not implemented yet (summary-diff redesign, `angular-oauth2-oidc` migration) |
| 📝 **TPR digital form** | [tpr-digital-form/](tpr-digital-form/) | TPR workflow specs, data model, and the base-year gap analysis |

## Reading order

- **New to the project?** Start with [architecture.md](architecture.md) for how the code works, then
  [status.md](status.md) for where it stands, then [open-findings.md](open-findings.md) for what is broken.
- **Planning work?** Start with [open-findings.md](open-findings.md), then [status.md](status.md) → the
  📦 Remediation batches, then the linked decision records.
- **Tracking a finding?** Findings live in [open-findings.md](open-findings.md) — one bullet each, with
  severity, why it matters and where the code is. Work in flight lives in [status.md](status.md).

## Conventions

- **One concern per file.** [open-findings.md](open-findings.md) is the only findings list;
  [status.md](status.md) tracks work in progress and the batch plan; reference material lives in
  [architecture.md](architecture.md), [decisions/](decisions/) and [tpr-digital-form/](tpr-digital-form/).
- **Resolved work is deleted, not annotated.** When a finding is fixed, remove it from
  [open-findings.md](open-findings.md); git holds the history. Do not keep "done" or "resolved" narrative in
  the docs.
- New architectural decisions go in [decisions/](decisions/); TPR specs and analyses in
  [tpr-digital-form/](tpr-digital-form/).
