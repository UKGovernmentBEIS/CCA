# TPR Digital Forms — Data Model & Calculations

> **Companion to:** `workflow-spec.md`
>
> **Code location:** `src/app/requests/tasks/target-period-reporting-form/`
>
> **API payload type:** `PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload`

---

## Standard Fuels — Conversion Factors

Grid electricity is always displayed first, followed by Non-grid electricity from renewable sources, then the rest in order of frequency of use.

All conversion factors are stated in **kgCO₂e/[measuring unit]** (improved from the kgC/[measuring unit] used in CCA2/TP6).

The values for `kgCO₂e/kWh` are the base. Values for MWh and GJ are calculated by multiplying/dividing appropriately (displayed to max 7 decimal places for GJ).

| Fuel Type | kgCO₂e/kWh | kgCO₂e/MWh (×1000) | kgCO₂e/GJ (×1000÷3.6) | Primary Factor |
|---|---|---|---|---|
| **Grid electricity** and electricity from combustion of a renewable fuel | 0.10046 | 100.46000 | 27.9055556 | 2.1 |
| **Non-grid electricity** from renewable sources (PV, hydro, wind) | 0.00000 | 0.00000 | 0.00000 | 1.0 |
| Natural gas | 0.18254 | 182.54000 | 50.7055556 | 1.0 |
| LPG | 0.21449 | 214.49000 | 59.5805556 | 1.0 |
| Gas oil / Diesel | 0.25679 | 256.79000 | 71.3305556 | 1.0 |
| Fuel oil | 0.26816 | 268.16000 | 74.4888889 | 1.0 |
| Kerosene | 0.24677 | 246.77000 | 68.5472222 | 1.0 |
| Coal | 0.32463 | 324.63000 | 90.1750000 | 1.0 |
| Coke | 0.42900 | 429.00000 | 119.1666667 | 1.0 |
| Petrol | 0.22719 | 227.19000 | 63.1083333 | 1.0 |
| Nitrogen cooling | 0.10046 | 100.46000 | 27.9055556 | 2.1 |
| Carbon dioxide cooling | 0.10046 | 100.46000 | 27.9055556 | 2.1 |
| Ethane | 0.19983 | 199.83000 | 55.5083333 | 1.0 |
| Naphtha | 0.23651 | 236.51000 | 65.6972222 | 1.0 |
| Petroleum coke | 0.34095 | 340.95000 | 94.7083333 | 1.0 |
| Refinery gas | 0.18324 | 183.24000 | 50.9000000 | 1.0 |

---

## Input Data Types

### Select Target Period

| Field | Type | Values |
|---|---|---|
| `targetPeriodType` | String | `TP7`, `TP8`, or `TP9` |
| `reportType` | String | `FINAL` or `INTERIM` (derived from date + TP) |
| `targetPeriodYear` | Number | The calendar year being reported on |

### Energy / Fuel Details

```typescript
interface PerformanceDataFacilityInputEnergyFuelDetails {
  standardFuels: Record<StandardFuelType, FuelEntry>;
  nonStandardFuels: NonStandardFuel[];
  atLeastSeventyPercentEnergyUsed: boolean;
  electricitySuppliedFromCHP?: string; // Only if SRM applicable
}

interface FuelEntry {
  deliveredEnergy: string;
  primaryEnergy: string; // Calculated: deliveredEnergy × primaryFactor
}

interface NonStandardFuel {
  fuelTypeName: string;
  co2ConversionFactor: string;
  deliveredEnergy: string;
  primaryEnergy: string; // Calculated
}
```

### Throughput Details

```typescript
interface PerformanceDataFacilityInputThroughputDetails {
  actualThroughput: string;
  targetImprovement: string;
  adjustedThroughput: string; // Calculated
  totalTargetVariableEnergy: string; // Calculated
  variableEnergyConsumptionDataByProduct: ProductThroughput[];
}

interface ProductThroughput {
  productName: string;
  baselineYear: number;
  baselineEnergyIntensity?: string;
  baselineCo2Intensity?: string;
  improvementTargetPercent: string;
  actualThroughput: string;
  adjustedThroughput: string; // Calculated
  targetEnergy?: string; // Calculated
  targetCo2?: string; // Calculated
}
```

---

## Calculations

### Primary Energy (per fuel)

```
Primary Energy = Delivered Energy × Primary Conversion Factor
```

### Primary CO₂ (per fuel)

```
Primary CO₂ = Delivered Energy × Primary Conversion Factor × CO₂ Conversion Factor
```

For facilities using **tonne** as a measuring unit, divide the result by 1000.

### Throughput Adjustment Factor (SRM only)

```
TAF = (Grid electricity + Non-grid electricity)
    / (Grid electricity + Non-grid electricity + CHP & generators electricity)
```

- If all three are zero → `TAF = 1`
- Must satisfy: `0 ≤ TAF ≤ 1`

### Adjusted Throughput

```
Adjusted Throughput = TAF × Actual Throughput  (if SRM)
Adjusted Throughput = Actual Throughput         (otherwise)
```

---

### Improvement Target % (Split by Product — Adjusted)

For products where `base year ≠ facility base year` (either above or below), the target percentage must be adjusted. When the product base year equals the facility base year, the unadjusted facility improvement target is used directly. The calculation has two steps:

#### Step 1: Total Progress at Product Base Year

```
progressAtProductBaseYear =
    ((MIN(productBaseYear, 2026) - facilityBaseYear) / (2026 - facilityBaseYear) × facilityTargetTP7)
  + (MAX(MIN(productBaseYear, 2028) - 2026, 0) / (2028 - 2026) × (facilityTargetTP8 - facilityTargetTP7))
  + (MAX(MIN(productBaseYear, 2030) - 2028, 0) / (2030 - 2028) × (facilityTargetTP9 - facilityTargetTP8))
```

> Calculates progress across 3 periods because there are 3 improvement % milestones (at the end of each TP).

**Example:** `facilityBaseYear = 2022`, `productBaseYear = 2027`, `facilityTargetTP7 = 8%`, `facilityTargetTP8 = 10%`, `facilityTargetTP9 = 16%`

```
progress = [(2026-2022)/(2026-2022) × 0.08] + [(2027-2026)/(2028-2026) × (0.10-0.08)] + 0
         = [4/4 × 0.08] + [1/2 × 0.02]
         = 0.08 + 0.01
         = 0.09 (9%)
```

#### Step 2: Adjusted Target % per TP

```
AdjustedTarget% = MAX(0, (facilityTargetForTP - progressAtProductBaseYear)
                        / (1 - progressAtProductBaseYear))
```

**Continuing the example** (progress = 9%):

| Period | Calculation | Result |
|---|---|---|
| TP7 | `MAX(0, (0.08 - 0.09) / (1 - 0.09))` | **0%** (before product base year) |
| TP8 interim (2027) | `MAX(0, (Interim target % - 0.09) / 0.91)` = `MAX(0, (0.09 - 0.09) / 0.91)` | **0%** (product base year) |
| TP8 | `MAX(0, (0.10 - 0.09) / 0.91)` = `0.01 / 0.91` | **1.099%** |
| TP9 interim (2029) | `MAX(0, (Interim - 0.09) / 0.91)` = `MAX(0, (0.13 - 0.09) / 0.91)` = `0.04 / 0.91` | **4.396%** |
| TP9 | `MAX(0, (0.16 - 0.09) / 0.91)` = `0.07 / 0.91` | **7.692%** |

> For interim reports, replace `facilityTargetForTP` with the **Interim target %** in the formula.

---

### Target Energy / Carbon at TP Throughput

> **1-year TP** (TP7 final, TP8 interim, TP9 interim): Multiplier = 1 for fixed energy
>
> **2-year TP** (TP8 final, TP9 final): Multiplier = 2 for fixed energy

#### For 1-year target periods:

```
Target Energy =
    [Σ (BY energy intensity × Adjusted throughput) + BY fixed energy]
  × (1 - improvement% or Interim target%)
```

- **Split by product:** `Σ [ (BY intensity × Adjusted Tput) × (1 - Target%) ] + [BY fixed energy × (1 - Target%) ]`
- **Fixed only:** `BY fixed energy × (1 - Target%)`
- **Totals only:** `[(BY intensity × Adjusted Tput) × (1 - Target%)] + [BY fixed energy × (1 - Target%)]`

#### For 2-year target periods:

Replace `BY fixed energy` with `2 × BY fixed energy` in all formulas above.

> Carbon-based: same calculation using BY CO₂ intensity. Result in kgCO₂e or tCO₂e.

---

### BY Energy/Carbon at TP Throughput

> Background calculation — not displayed directly.

**1-year TP:** `BY Energy = Σ (BY intensity × Adjusted throughput) + BY fixed energy`

**2-year TP:** `BY Energy = Σ (BY intensity × Adjusted throughput) + (2 × BY fixed energy)`

---

### Total Target Variable Energy

```
Target Variable Energy = Σ (BY energy intensity × Adjusted throughput) × (1 - improvement% or Interim target%)
```

- Same for 1-year and 2-year TPs (throughput already accounts for the period length)
- Not applicable for fixed-energy-only facilities (value = 0)
- For interim reports: use Interim target %

---

### Interim Target %

For interim reports, replaces the improvement target %:

```
Interim Target % = (TP Target % + Previous TP Target %) / 2
```

This reflects that the calculation is only up to the end of the first year of a 2-year target period.

---

## Results / Calculated Data

| Field | Type | Formula | Notes |
|---|---|---|---|
| **TP weighted conversion factor** | Display only | Energy-based: `Σ (Primary energy × CO₂e factor) / Σ Primary energy`<br>Carbon-based (kg): `Σ (Primary CO₂) / Primary energy total`<br>Carbon-based (tonne): × 1000 to get kgCO₂e/kWh | |
| **Actual energy total** | Display only | `Σ Primary energy` for all fuels where value ≠ 0 | Not displayed for carbon-based facilities |
| **Target energy** | Display only | See formula above | Not displayed for carbon-based facilities |
| **Energy difference** | Display only | `Actual energy - Target energy`<br>−/0 = Target met<br>+ = Target not met | Not displayed for carbon-based facilities |
| **Actual tCO₂e emitted** | Display only | Energy-based: `Σ (Primary energy × CO₂ factor) / 1000`<br>Carbon-based (kg): `Σ (Primary CO₂) / 1000`<br>Carbon-based (tonne): `Σ Primary CO₂` (already in tCO₂e) | Always displayed in tCO₂e |
| **Target tCO₂e** | Display only | Energy-based: `Target energy × Weighted avg CF / 1000`<br>Carbon-based (kg): `Target CO₂ / 1000`<br>Carbon-based (tonne): `Target CO₂` | |
| **tCO₂e difference** | Display only | `Actual tCO₂e − Target tCO₂e`<br>− = surplus<br>+ = buyout required | |
| **Actual improvement %** | Display only | `1 − (Actual TP energy/carbon / BY energy/carbon at TP throughput)`<br>If BY energy/carbon = 0 → improvement % = 0 | |

### Final Report Results (not displayed for interim reports)

| Field | Type | Values |
|---|---|---|
| **Target period result** | Display only | `Target met` (if Energy difference ≤ 0) or `Target not met` (if Energy difference > 0) |
| **Total surplus gained (tCO₂e)** | Display only | tCO₂e difference **if negative** — rounded **down** to closest integer |
| **Total buy-out required (tCO₂e)** | Display only | tCO₂e difference **if positive** — rounded **up** to closest integer |

---

## ⚠️ Product Base Year ≠ Facility Base Year

> **Status: unresolved — pre-facility products silently receive the facility target; long-term resolution under investigation by the analysis team**
>
> **References:** CCA-3245, `product-base-year-below-facility-gap.md`, `../open-findings.md`

The original spec stated that products should not have a base year earlier than the facility base year. In practice, such products exist in real data.

**Current behaviour:** `calculateAdjustedImprovementTarget` returns the facility's own improvement target when `productBaseYear <= facilityBaseYear`, and all three call sites skip the adjusted calculation for `product.baselineYear <= facilityBaseYear`. A pre-facility product therefore reports the facility target (6.122% in the example below) where the spreadsheet expects the sector-level value (8.000%) — silently, with no validation error.

**Long-term direction:** the analysis team is investigating whether `productBaseYear < facilityBaseYear` should be blocked at the product-addition / facility level, before the TPR process begins — which would stop the scenario reaching the TPR form at all. The alternative, analysed in `product-base-year-below-facility-gap.md`, is to let the rebasing formula run with negative progress, which reproduces the spreadsheet's 8.000%; it is **not** implemented.

**Known pre-existing limitation:** The formula silently degrades to `BigNumber(0)` when improvement keys are missing from `referenceData.baselineAndTargets.improvements` (e.g., no `TP9` key). This is not specific to the `productBaseYear < facilityBaseYear` case but affects all adjusted-target calculations.

---

## Code Implementation Notes

### Key Types (cca-api)

| Type | Purpose |
|---|---|
| `PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload` | Full request task payload (includes `referenceData`, `performanceData`, `sectionsCompleted`) |
| `PerformanceDataFacilityInputData` | Input data subset sent for save (`energyFuelDetails`, `throughputDetails`, `calculatedResults`) |
| `PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload` | Save action DTO payload |

### API Actions

| Action Type | Purpose |
|---|---|
| `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION` | Save energy/fuel or throughput data |
| `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CALCULATE_RESULTS` | Calculate results (server-side, triggered before submit page) |
| `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION` | Final submission |
| `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION` | Refresh baseline data |

### Section Status Keys (from `@requests/common`)

| Key | Used For |
|---|---|
| `TPR_FORM_ENERGY_FUEL_DETAILS_SUBTASK` | Energy/fuel details task status |
| `TPR_FORM_THROUGHPUT_DETAILS_SUBTASK` | Throughput details task status |

### Key Files

| File | Purpose |
|---|---|
| `transform.ts` | Transforms payload to save format; creates DTO for save and submit |
| `submit/calculated-results.resolver.ts` | Server-side calculation before submit page loads |
| `target-period-reporting-form-task-content.ts` | Task list content factory (status calculation, dependencies) |
| `../common/target-period-reporting/tp-reporting-errors.ts` | All error codes, messages, and error summary info |
| `../common/target-period-reporting/summaries/` | Shared summary components (energy/fuel, throughput, results) |
