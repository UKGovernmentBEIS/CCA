# Product Base Year < Facility Base Year — Gap Analysis

> **Date:** 2026-07-31
> **References:** CCA-3245, `data-model.md` § "Known Gap", `utils.ts:286-337`
> **Code:** `src/app/requests/common/target-period-reporting/utils.ts` — `calculateAdjustedImprovementTarget()`

---

## 1. What the Spec States

From `data-model.md` § "Known Gap: Product Base Year < Facility Base Year":

> *"Future CR: NO products are allowed to have a product base year < facility base year."*

The spec explicitly forbids this scenario. It makes no provision for how improvement targets should be calculated for products whose base year predates the facility base year, because such products are not expected to exist in valid data.

The `calculateAdjustedImprovementTarget` formula in the spec only covers `productBaseYear > facilityBaseYear` — products added *after* the facility's baseline. The progress formula anchors everything to `facilityBaseYear`:

```
progressAtProductBaseYear =
    ((MIN(productBaseYear, 2026) - facilityBaseYear) / (2026 - facilityBaseYear) × facilityTargetTP7)
  + (MAX(MIN(productBaseYear, 2028) - 2026, 0) / (2028 - 2026) × (facilityTargetTP8 - facilityTargetTP7))
  + (MAX(MIN(productBaseYear, 2030) - 2028, 0) / (2030 - 2028) × (facilityTargetTP9 - facilityTargetTP8))
```

If `productBaseYear < facilityBaseYear`, the numerator `(2022 − 2023)` becomes negative, producing undefined results. The spec does not define a fallback.

---

## 2. What the Spreadsheet States

The spreadsheet defines **two independent progress trajectories**:

### Trajectory A: Sector baseline (2022)

Products whose base year equals the sector baseline follow the **full sector improvement targets**:

| Year | Target |
|------|--------|
| 2022 | 0.000% |
| 2023 | 2.000% |
| 2024 | 4.000% |
| 2025 | 6.000% |
| 2026 (TP7) | **8.000%** |
| 2027 (TP8 interim) | **10.000%** |
| 2028 (TP8) | **12.000%** |
| 2029 (TP9 interim) | **14.000%** |
| 2030 (TP9) | **16.000%** |

Linear interpolation: each year from 2022 to 2026 contributes 2.000% (one-quarter of the 8% TP7 target).

### Trajectory B: Facility baseline (2023)

Products whose base year matches the facility base year follow the **facility-specific scaled targets**:

| Year | Target |
|------|--------|
| 2022 | 0.000% |
| 2023 | 0.000% |
| 2024 | 2.041% |
| 2025 | 4.082% |
| 2026 (TP7) | **6.1224490%** |
| 2027 (TP8 interim) | **8.1632653%** |
| 2028 (TP8) | **10.2040816%** |
| 2029 (TP9 interim) | **12.2448980%** |
| 2030 (TP9) | **14.2857143%** |

The facility targets are not a simple uniform scaling of the sector targets (the ratios differ: 6.122449/8 ≠ 10.2040816/12 ≠ 14.2857143/16). They are computed independently from the facility's underlying agreement data.

### Key observation

The spreadsheet assigns targets based on **which baseline the product aligns with**, not the facility's baseline:

- Product base year = sector baseline (2022) → **sector targets**
- Product base year = facility baseline (2023) → **facility targets**
- Product base year > facility baseline (2024+) → **adjusted facility targets** (the `calculateAdjustedImprovementTarget` formula)

The facility base year is essentially treated as a *cutoff*, not an anchor. Products on or before the sector baseline follow the sector trajectory. Products at the facility baseline follow the facility trajectory. Products after get adjusted.

---

## 3. What the code does today

```typescript
// utils.ts:297 — the guard returns the facility target for every product at or before the facility base year
if (!facilityBaseYear || !productBaseYear || productBaseYear <= facilityBaseYear) {
    return facilityTarget;
}
```

All three call sites guard the same way (`product.baselineYear > facilityBaseYear`), so a product whose base
year predates the facility's **skips the adjusted calculation entirely and reports the facility's own target** —
silently, with no validation error. On the worked example below that is 6.122% where the spreadsheet expects
8.000%.

### What removing the guard would do (the Option D analysis — not implemented)

If the guard were `productBaseYear === facilityBaseYear` and the call sites used `!==`, the formula would run
for pre-facility products and produce **negative progress** in the TP7 term
(`tp7ProgressYears = min(productBaseYear, 2026) - facilityBaseYear` is negative), which unwinds the facility's
progress from years before the facility existed and converges the target toward the sector-level value:

```
result = (facilityTarget − (−progress)) / (1 − (−progress))
       = (facilityTarget + |progress|) / (1 + |progress|)
       > facilityTarget
```

### Worked example

**Facility:** base year 2023, TP7 = 6.122449%, sector TP7 = 8.000%

| Product | Base Year | With the guard removed | Spreadsheet expects | Match? |
|---------|-----------|------------------------|---------------------|--------|
| 2022 Product | 2022 | **8.000%** | 8.000% | ✓ |
| 2023 Product | 2023 | 6.122% | 6.122% | ✓ |
| 2024 Product | 2024 | 4.168% | 4.168% | ✓ |
| 2025 Product | 2025 | 2.128% | 2.128% | ✓ |
| 2026 Product | 2026 | 0.000% | 0.000% | ✓ |

Today the 2022 row returns 6.122% (the facility target), not 8.000%.

### Why negative progress is correct

The formula computes:

```
tp7ProgressYears = min(2022, 2026) - 2023 = -1   ← negative
tp7Years = 2026 - 2023 = 3
progress = (-1/3) × 0.06122449 = -0.020408        ← negative progress

result = (0.06122449 - (-0.020408)) / (1 - (-0.020408))
       = 0.08163265 / 1.020408
       = 0.08 → 8.000%  ✓
```

The original assumption — that negative progress would produce "garbled results or silent 0" — was incorrect. The rebasing formula `(target − progress) / (1 − progress)` is mathematically well-behaved for negative progress values, and `result.isNegative()` is never triggered (the numerator and denominator are both positive when `facilityTarget ≥ 0`).

---

## 4. Long-term direction (open)

**Option A — reject pre-facility products at validation time.** Block any product with
`baselineYear < facilityBaseYear` at product-addition or facility level. This is the spec's stated long-term
direction and is under investigation by the analysis team; it is not implemented. If validated there, products
with a base year below the facility baseline never reach the TPR form and the calculation path becomes
unreachable in normal operation. Until one of the two options lands, pre-facility products keep reporting the
facility target instead of the sector-level value.

Two alternatives were considered and rejected: extending the formula with sector targets (unnecessary — the
current formula already matches the spreadsheet) and capping at the facility base year (produced 6.122% where
the spreadsheet expects 8%).

---

## 5. Open risks

- **`facilityBaseYear == 2026` divides by zero.** `tp7Years = 2026 - facilityBaseYear` is 0 and
  `new BigNumber(tp7ProgressYears).div(tp7Years)` has no guard. The frontend guards this (`if (tp7Years > 0)`);
  the backend does not (see the backend BH2 item in [../open-findings.md](../open-findings.md)).
- **Extreme cases can exceed 100% of the facility target** (e.g. 40% when a product's base year is five years
  before the facility's) — business stakeholders still need to validate those outputs.
- **Missing improvement keys silently become 0%.** If `referenceData.baselineAndTargets.improvements` has no key
  for a period (e.g. no `TP9`), `toBigNumber(undefined)` returns 0, so that period reads as 0% improvement.
  This affects all adjusted-target calculations, not just pre-facility products.
- **Corrupt data is swallowed.** When `totalProgress ≥ 1` (≥100% progress, which indicates corrupt data) the
  function returns `BigNumber(0)` without surfacing an error.
