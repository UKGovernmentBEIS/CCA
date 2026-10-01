# TPR Digital Forms — Workflow Specification

> **Code location:** `src/app/requests/tasks/target-period-reporting-form/`
>
> **Request task type:** `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT`
>
> **Save action:** `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION`

---

## Summary

The completion of a digital form to submit target period performance data is one of the two selected solutions. It is deemed most suitable for:

- Smaller sectors that don't want/need the complexity of generating and uploading CSV files
- All sectors to submit individual facility corrections

The input data for the **final TP reports** and **interim reports** are identical (interim reports are similar to a 1-year target period's input).

For **interim reports**, no potential surplus and/or buy-out amounts are calculated (only performance/progress).

---

## User Access Rights

Any sector user with **Execute** permissions for **CCA-1134: Sector user permissions - Download and Upload Performance Data Reporting** (from CCA2) will be able to use both the bulk (CSV) upload and the digital form solution.

---

## Pre-conditions (Facility Eligibility)

The following facilities are considered eligible for a reporting period:

- Facilities with **first activation date before 1 January** of the relevant reporting period, **AND**
- Are **active on 1 January** of the relevant reporting period (including those terminated/excluded on or after 1 January)

> Facilities activated **on or after 1 January** of the relevant reporting period must **not** be included.

### Digital Form Workflow Initiation Conditions

A digital forms workflow can only be initiated for a facility for a specific target period if:

1. The facility's **locked indicator** for the target period is **NOT** set to Yes
2. No **in-progress** digital forms workflow already exists for the facility and target period

### Split by Product — Product Eligibility

If a facility uses the **"Split by product"** method to account for variable energy, products are included if:

- The product status is **Live**
- The product's **base year is before the end** of the TP (or interim reporting year), inclusive

> **Example:** A product with a 2028 base year will be excluded from TP7 (final) reporting and TP8 interim reporting, but included in TP8 (final) reporting and TP9 reporting (interim & final).

> **⚠️ GAP / Future CR:** Products with a base year **less than** the facility base year are not currently handled. See [CCA-3245](https://jira.example.com/browse/CCA-3245): "CCA3 TP Reporting if ProductBY is earlier than the FacilityBY year." The spec states *"NO products are allowed to have a product base year < facility base year"* — this is currently a **Future CR** and is not implemented.

**Additional rule:** AT LEAST ONE product MUST have a base year **equal to** the facility base year.

---

## Post-conditions

After successful submission:

- A **workflow history entry** and **timeline event** is created for the facility
- The facility's **report version** for the specific target period and report type **increases by 1**

---

## Key Business Rules

### Report Types by Date

| TP | Report Type | Subtype | Submission Dates |
|---|---|---|---|
| TP7 | Final | Primary | 1 Jan 2027 – 30 Jun 2027 (inclusive) |
| TP7 | Final | Secondary | Any date on or after 1 Jul 2027 |
| TP8 | Interim | — | 1 Jan 2028 – 31 Dec 2028 |
| TP8 | Final | Primary | 1 Jan 2029 – 30 Jun 2029 (inclusive) |
| TP8 | Final | Secondary | Any date on or after 1 Jul 2029 |
| TP9 | Interim | — | 1 Jan 2030 – 31 Dec 2030 |
| TP9 | Final | Primary | 1 Jan 2031 – 30 Jun 2031 (inclusive) |
| TP9 | Final | Secondary | Any date on or after 1 Jul 2031 |

- All **primary** reports are submitted between **1 January and 30 June** directly after the end of a target period (this includes late reports submitted after 1 May and before 1 July)
- All **secondary** reports are submitted **on or after 1 July** after the end of a target period (including corrections)
- Upload of a primary report is **NOT** a precondition for upload of a secondary report
- On initiating a workflow, any relevant data previously submitted for the same facility/TP/report type should be **prepopulated**

### Numeric Fields

- The user can input **up to 7 decimals** in all numeric fields — we store up to 7 decimals
- All decimals must be displayed **to the last non-zero decimal** entered/stored — no trailing zeroes

### Locked Indicator

Refer to [CCA3 - Facility Report Tab](https://jira.example.com/browse/CCA3-FacilityReport).

### Interim Reports

- **INTERIM** (also referred to as **Annual**) reports are submitted between **1 January and 31 December** in the second year of a 2-year target period, to indicate progress during the first year
- **NO** interim reports or corrections may be uploaded after **31 December** of the applicable year
- There is **NO overlap** between the interim and final reports for a target period

### Baseline Data Consistency

At task completion, the workflow baseline data snapshot **must match** the most recently submitted underlying agreement baseline data. If not, the user will be forced to refresh the baseline data through the workflow and confirm any changes.

---

## Trigger Point

The digital forms will be triggered by clicking a **"Start Final reporting task"** button on a **"Start a facility task"** page.

A **"Start TP reporting task"** button initiates the step to select the target period (and report type). When this step is successfully completed, the workflow is initiated.

---

## Select Target Period

| Label | Type | Default / Rules / Errors |
|---|---|---|
| Hint text above dropdown | Text | *"Starting dates of the respective reporting periods are the following: TP7 final report from 1 January 2027, TP8 interim report from 1 January 2028, TP8 final report from 1 January 2029, TP9 interim report from 1 January 2030, TP9 final report from 1 January 2031"* |
| **Select target period** | Dropdown | **Mandatory.** Values: TP7 (2026), TP8 (2027-2028), TP9 (2029-2030). Display only target periods for which reporting periods are **active**. TP6 is **not** included (uses CCA2 reporting). |
| Hint text below dropdown | Text | *"Choose the preferred target period from the options that are currently available"* |
| **"The target period I need isn't displayed"** | Expandable | *"The list only includes target periods for which you can submit a report (or a correction to a previously submitted report). Additional target periods will become available once their respective reporting periods start. TP6 reporting should be done through the 'Download target period reporting (Final) spreadsheets' and 'Upload target period reporting (Final) spreadsheets' tasks."* |
| **Report type** | Display only | Derived from current date + selected TP. Values: **Final report** or **Interim report**. Hint: *"The service automatically determines the type of target period report based on the target period you have selected and the current date."* |

### Error Checking on Target Period Selection

| Error Code | Condition | Message |
|---|---|---|
| `TPRDF1001` | A TPR workflow is already in progress for the selected TP | *"There is already a TPR task in progress for the target period you selected. You can locate the relevant task through the main dashboard."* |
| `TPRDF1002` | Facility is not eligible to report for the selected TP | *"A target period report cannot be submitted against the target period you selected for this facility. Select a different target period or exit the task."* |
| `TPRDF1003` | Interim report period has expired | *"The combination you have selected has expired. Make a new selection."* |
| `TPRDF1004` | Locked indicator is **Yes** (secondary report check) | *"The reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request."* |
| `TPRDF1005` | Split by product: no product with base year = facility base year | *"The baseline data for this facility must contain at least one product with a base year equal to the facility base year, and at least one product with a base year less than or equal to the year the report data relates to."* |
| `TPRDF1009` | Baseline start date after facility's first activation date and after start of reporting period | *"There is an error in the facility's baseline data. Please contact your regulator."* |

---

## Task List

The task list contains three tasks in sequence:

| # | Task | Depends On |
|---|---|---|
| 1 | **Energy/fuel details** | — (available immediately) |
| 2 | **Throughput details** | Energy/fuel details = Completed |
| 3 | **Submit TPR** | Energy/fuel details + Throughput details = Completed |

### Subtask Status Matrix

| Task | Status | Conditions |
|---|---|---|
| **Energy/fuel details** | Not yet started | Default when workflow is triggered; after Refresh button is clicked |
| | In progress | Data saved/updated but not yet confirmed |
| | Completed | Data confirmed and completed |
| **Throughput details** | Cannot start yet | Energy / Fuel Details is not Completed; after Refresh button is clicked |
| | Not yet started | Energy / Fuel Details is Completed |
| | In progress | Data saved/updated but not yet confirmed |
| | Completed | Data confirmed and completed |
| **Submit** | Cannot start yet | Not all subtasks are completed |
| | Not yet started | All subtasks are Completed |

> **⚠️ Pending:** [CCA-3224](https://jira.example.com/browse/CCA-3224) — Reset Throughput subtask status if SRM used and Fuels page changed. Prevents Submit error for facilities with SRM when fuels (Grid/renewable energy and/or CHP delivered energy) change.

---

## Subtask 1: Energy/Fuel Amount Consumed

Displays a table of fuel types with these columns:

| Column | Type | Rules |
|---|---|---|
| **Fuel type** | Display (standard) / Text input (non-standard) | Standard fuels displayed in order of frequency of use. Non-standard: mandatory, unique name, max 10. Error if blank: *"Enter a name for the additional fuel type."* Error if not unique: *"Enter a unique name for this fuel."* |
| **CO₂ conversion factor** | Display (standard) / Numeric input (non-standard) | Unit: `kgCO₂e/[measuring unit]` (kWh, MWh, or GJ). Carbon-based facilities always `kgCO₂e/kWh`. Non-standard: mandatory, ≥ 0. Error if blank: *"Enter the conversion factor for the additional fuel type."* |
| **Delivered energy (excluding UK ETS)** | Numeric input | Unit derived from facility energy unit (kWh if carbon-based). Standard fuels: default 0, can be ≥ 0. Non-standard fuels: must be > 0. No negative numbers. |
| **Primary energy conversion factor** | Display only | Default 1.0 (including non-standard). 2.1 for: Grid electricity, Nitrogen cooling, Carbon dioxide cooling. |
| **Primary energy / Primary CO₂e** | Display only (calculated) | **Primary energy = Delivered energy × Primary conversion factor.** **Primary CO₂e = Delivered energy × Primary conversion factor × CO₂ conversion factor.** For tonne-based facilities, divide by 1000. |

### Hint Text

> *"Fill in the delivered energy (excluding UK ETS) amounts that you consumed for each fuel type during the period. You can also add a new fuel type if it is not included in the current list."*

### Expandable: "I need help with the energy terms"

- **Delivered energy:** The amount of usable energy consumed by a facility, expressed in energy units. Includes metered amounts of electricity, natural gas and other fuels.
- **Primary energy:** The amount of energy expressed in its original unconverted form before considering transmission and distribution losses.
- **CO₂e conversion factor:** The amount of greenhouse gases emitted, expressed as total CO₂e, for each unit of energy consumed.
- **Primary energy conversion factor:** The amount of primary energy consumed to produce each unit of delivered energy. For most fuels this is 1. For grid electricity it is 2.1.

### Buttons and Links

- **Add a fuel type** button — adds a non-standard fuel row. Hidden after 10 non-standard fuels. Hint: *"You can add up to 10 new fuel types."*
- **Delete** link (per non-standard row) — removes a non-standard fuel.

### Below the Table

| Field | Type | Rules |
|---|---|---|
| **Is at least 70% of the total energy used in carrying out eligible activities?** | Radio buttons | **Mandatory.** No default. Values: Yes / No. |
| **Total electricity supplied from combined heat and power plant and dedicated generators** | Numeric input | Only displayed if SRM is applicable. Mandatory if displayed. Can be 0 or positive. Same unit as Delivered energy. |

### SRM Validation Rule (New)

> If CHP electricity (`Total electricity supplied from...`) > 0 and **both** Grid electricity and Non-grid electricity = 0 → Error: *"Input inconsistent with SRM rules. Contact your regulator."*

If CHP electricity = 0, Grid and Non-grid electricity can be anything.

### Throughput Adjustment Factor (SRM only)

Display only, calculated:

```
Throughput adjustment factor = (Grid electricity + Non-grid electricity)
  / (Grid electricity + Non-grid electricity + CHP & generators electricity)
```

- If all three are zero → factor = 1
- Value must be ≤ 1 and ≥ 0

### Check Your Answers (Energy/Fuel)

- When **all** Delivered Energy values are zero (0), the Check Your Answers page is differentiated. On confirmation, the task is set to Completed and Actual energy used and Average carbon conversion factor are set to zero.
- Only fuel types with Total Consumption **greater than zero** are displayed on the Check Your Answers page.

---

## Subtask 2: Throughput Details

The layout depends on the facility's baseline information:

### Common Fields

| Field | Type | Rules |
|---|---|---|
| **Facility's way of accounting for variable energy** | Display only | Values: *"No variable energy (only fixed energy)"*, *"Totals only"*, or *"Split by product"* |
| **Baseline energy/CO₂ intensity** | Display only | Energy intensity (kWh, MWh, GJ) or CO₂ intensity (kg, tonne). Not displayed if facility uses only fixed energy. |
| **Improvement target %** | Display only | Facility improvement % for final reports; Interim target % for interim reports |
| **Actual throughput** | Numeric input | **Mandatory.** May be positive or zero (0). Error if blank: *"Enter the total throughput."* |
| **Adjusted throughput** | Display only | If SRM: `Adjusted throughput = Throughput adjustment factor × Actual throughput`. Otherwise: `Adjusted throughput = Actual throughput`. Must be positive or zero. |
| **Throughput unit** | Display only | As stored in facility's baseline information |

### Split by Product (per product)

| Field | Type | Rules |
|---|---|---|
| **Product name** | Display only | As stored in baseline |
| **Baseline year** | Display only | As stored in baseline |
| **Baseline energy/CO₂ intensity** | Display only | As stored in baseline |
| **Improvement target %** | Display only | If base year = facility base year: use facility improvement %. If base year > facility base year: use **Adjusted Target %** (see data model). |
| **Actual throughput** | Numeric input | Default 0. May be positive or zero. |
| **Adjusted throughput** | Display only | Same SRM logic as above applied per product |
| **Target energy/CO₂** | Display only | Calculated per product (see data model) |

### Calculated Amounts

| Field | Type | Notes |
|---|---|---|
| **Total target variable energy** | Display only | Not displayed for fixed-energy-only facilities. For interim reports, uses Interim target %. See data model for calculation. |

### Check Your Answers Pages

Different layouts depending on the facility type:
- **Fixed energy only:** Shows actual throughput, adjusted throughput, throughput unit
- **Totals only:** Includes baseline intensity, improvement %, target variable energy
- **Split by product:** Per-product rows with all calculated values

---

## Submit TPR: Confirm Results and Submit

When the user chooses **Confirm and submit**, the following error checking is performed before submission:

### Submit Error Conditions

| Error Code | Condition | Message |
|---|---|---|
| `TPRDF1008` | Baseline/target data has changed since snapshot | *"A newer version of the baseline data is available. You need to refresh and reconfirm all information before submitting the TPR."* → Link to refresh baseline data page |
| `TPRDF1002` | Facility was excluded during recent variation (exclusion backdated to before reporting period start) | *"This facility is not eligible to report for this target period - the workflow must be cancelled."* → Link to cancel task confirmation page |
| `TPRDF1004` | Facility is locked (buy-out run completed after form initiation) | *"The reporting for this target period must be unlocked before submitting reports or corrections. Contact your regulator to make an unlocking request."* User cannot submit until data has been refreshed AND relevant tasks resubmitted. |
| `TPRDF1005` | Split by product: products not eligible | Product eligibility error → Link to cancel task |

If the reporting period has **expired** for an Interim report, the workflow is automatically terminated as **Expired** and a dedicated expired page is displayed.

### Successful Submit

On success, navigates to the **confirmation** page (`submit/confirmation`).

### Submit Flow (Code)

1. `calculatedResultsResolver` calls `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_CALCULATE_RESULTS` action
2. User reviews results on the submit action page
3. On confirm, calls `PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_APPLICATION` action

---

## Baseline Refresh

The refresh of baseline data may be triggered:

- **Manually** by the user clicking **"Refresh baseline data"** under Related actions
- **Automatically** — the error checking on Submit directs the user here

### Effect of Refresh on Subtask Statuses

| TPR Subtask | Baseline Change | Effect |
|---|---|---|
| **Energy/fuel details** | Energy/carbon units changed | Set to **In progress**. CO₂ conversion factors updated for standard fuels. Non-standard fuel CO₂ factors **cleared** ([CCA-3226](https://jira.example.com/browse/CCA-3226)). Previously entered delivered energy values preserved but unit label updated. All other tasks set to **Cannot start yet**. |
| | SRM applicability changed | Set to **In progress**. If Yes→No: clear CHP electricity. If No→Yes: user must input CHP value, TAF recalculated. If remains Yes: populate latest value. All other tasks → **Cannot start yet**. |
| **Throughput details** | Baseline start date changed | Set to **In progress**. Previously entered throughput values preserved. Target energy/carbon and Improvement % recalculated. Submit → **Cannot start yet**. |
| | Baseline fixed energy changed | Set to **In progress**. Previously entered throughput values preserved. All calculated values recalculated. Submit → **Cannot start yet**. |
| | Variable energy method changed (Totals only ↔ Split by product) | Set to **In progress**. **All input values reset** (previous inputs not relevant). Relevant baseline data populated, all calculations redone. Submit → **Cannot start yet**. |
| | Product added | Set to **In progress**. Previously entered throughput values per product preserved. New product baseline values displayed, Improvement % calculated. User must enter throughput for new product. Submit → **Cannot start yet**. |
| | Product excluded | Set to **In progress**. Previously entered throughput values preserved. User may keep/change excluded product value (likely set to 0). Submit → **Cannot start yet**. |
| | Any baseline values changed | Set to **In progress**. Previously entered throughput values preserved. All calculated values recalculated. Submit → **Cannot start yet**. |
| | Improvement % changed (TP7/TP8/TP9) | Set to **In progress**. Previously entered throughput values preserved. All calculated values recalculated. Submit → **Cannot start yet**. |
| **Submit** | Any of the above | Set to **Cannot start yet**. All calculated values recalculated. |

### Refresh Error Conditions

| Error Code | Condition | Message |
|---|---|---|
| `TPRDF1002` | Facility excluded during recent variation (backdated to before reporting period) | *"This facility is not eligible to report for this target period - the workflow must be cancelled."* → Link to cancel task |
| `TPRDF1004` | Facility locked | Locked error |
| `TPRDF1005` | Variation for Split by product resulted in no eligible products | *"An error occurred during the baseline refresh. Please contact your regulator."* |
| `TPRDF1008` | Generic refresh error | *"An error occurred during the baseline refresh. Please contact your regulator."* |
| `TPRDF1009` | Baseline start date changed to after start of reporting period | *"An error occurred during the baseline refresh. Please contact your regulator."* |

> For errors `TPRDF1005`, `TPRDF1008`, and `TPRDF1009`: the baseline data is **NOT** refreshed (previous values retained). The sector user must contact the regulator and complete a new variation to correct the error.

---

## ⚠️ Known Gaps / Future CRs

1. **[CCA-3224](https://jira.example.com/browse/CCA-3224)** — TPR using Digital Forms - Reset Throughput subtask status if SRM used and Fuels page changed. Prevents a Submit error for facilities with SRM when fuels change.

2. **[CCA-3245](https://jira.example.com/browse/CCA-3245)** — CCA3 TP Reporting if ProductBY is earlier than FacilityBY year. The spec states *"NO products are allowed to have a product base year < facility base year"* but this validation and error handling is not yet implemented. Products with `base year < facility base year` are **not explicitly handled** in the current improvement target calculation logic.

3. **[CCA-3226](https://jira.example.com/browse/CCA-3226)** — TPR using Digital forms - Clear CO₂ factor for non-standard fuels on refresh. Ready for Dev (migrated).
