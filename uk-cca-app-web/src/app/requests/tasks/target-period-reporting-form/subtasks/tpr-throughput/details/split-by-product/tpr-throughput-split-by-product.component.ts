import { DecimalPipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, map, of } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, GovukTableColumn, TableComponent } from '@netz/govuk-components';
import {
  calculateAdjustedImprovementTargetForProduct,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateFacilityTargetVariableEnergy,
  calculateTargetEnergyForProduct,
  calculateThroughputAdjustmentFactor,
  decideVariableEnergyType,
  MeasurementTypeToUnitPipe,
  TaskItemStatus,
  TasksApiService,
  toNumber,
  toTPRBaselineDataDetails,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
} from '@requests/common';
import { SummaryComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { tprFormQuery } from '../../../../target-period-reporting-form.selectors';
import {
  createRequestTaskActionProcessDTO,
  toPerformanceDataFacilityDigitalFormSavePayload,
} from '../../../../transform';
import {
  ProductsArrayForm,
  TPR_THROUGHPUT_DETAILS_BY_PRODUCT_FORM,
  tprThroughputDetailsByProductFormProvider,
} from './tpr-throughput-split-by-product-form.provider';

@Component({
  selector: 'cca-tpr-throughput-split-by-product',
  templateUrl: './tpr-throughput-split-by-product.component.html',
  imports: [
    DetailsComponent,
    ReactiveFormsModule,
    ReturnToTaskOrActionPageComponent,
    TableComponent,
    TextInputComponent,
    SummaryComponent,
    WizardStepComponent,
    MeasurementTypeToUnitPipe,
    DecimalPipe,
    PercentPipe,
  ],
  providers: [tprThroughputDetailsByProductFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputSplitByProductComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<ProductsArrayForm>(TPR_THROUGHPUT_DETAILS_BY_PRODUCT_FORM);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);
  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);

  protected readonly baselineProducts = computed(
    () => this.referenceData()?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [],
  );

  private readonly currentThroughputValues = toSignal(
    this.form.controls.products.valueChanges.pipe(
      map(() => this.form.controls.products.controls.map((c) => c.getRawValue().actualThroughput)),
    ),
    { initialValue: this.form.controls.products.controls.map((c) => c.getRawValue().actualThroughput) },
  );

  protected readonly tableColumns: Signal<GovukTableColumn[]> = signal([
    { field: 'productName', header: 'Product name' },
    { field: 'baselineYear', header: 'Baseline year' },
    {
      field: 'energy',
      header: 'Baseline energy intensity',
    },
    { field: 'improvementTarget', header: 'Improvement target %' },
    { field: 'throughput', header: 'Actual throughput' },
    {
      field: 'adjustedThroughput',
      header: 'Adjusted throughput',
    },
    { field: 'targetEnergy', header: 'Target energy' },
  ]);

  private readonly variableEnergyExists = computed(
    () => this.referenceData()?.baselineAndTargets?.baselineVariableEnergy,
  );

  protected readonly variableEnergyType = computed(() =>
    decideVariableEnergyType(this.referenceData()?.baselineAndTargets?.variableEnergyType) === 'BY_PRODUCT'
      ? 'Split by product'
      : this.variableEnergyExists()
        ? 'Totals only'
        : 'No variable energy (only fixed energy)',
  );

  protected readonly baselineDetails = computed(() => toTPRBaselineDataDetails(this.referenceData()));
  protected readonly measurementUnit = computed(() => this.referenceData()?.baselineAndTargets?.measurementType);

  // Calculate facility metadata needed for product calculations
  private readonly facilityBaseYear = computed(() => {
    const baselineDate = this.referenceData()?.baselineAndTargets?.baselineDate;
    if (!baselineDate) return null;
    const year = Number.parseInt(baselineDate.substring(0, 4), 10);
    return Number.isNaN(year) ? null : year;
  });

  private readonly improvementTarget = computed(() =>
    calculateFacilityImprovementTarget(this.referenceData(), this.reportType(), this.targetPeriodType()),
  );

  private readonly throughputAdjustmentFactor = computed(() =>
    calculateThroughputAdjustmentFactor(
      toNumber(this.performanceData()?.energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy),
      toNumber(this.performanceData()?.energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy),
      toNumber(this.performanceData()?.energyFuelDetails?.electricitySuppliedFromCHP),
    ),
  );

  protected readonly productCalculations = computed(() => {
    const throughputValues = this.currentThroughputValues();
    const facilityBaseYear = this.facilityBaseYear();
    const facilityImprovementTarget = this.improvementTarget();
    const throughputFactor = this.throughputAdjustmentFactor();
    const useSRM = this.referenceData()?.baselineAndTargets?.usedReportingMechanism;

    return this.baselineProducts().map((product, i) => {
      const productBaseYear = product.baselineYear;
      let improvementTarget = facilityImprovementTarget;

      if (productBaseYear !== facilityBaseYear && facilityBaseYear) {
        improvementTarget = calculateAdjustedImprovementTargetForProduct(
          this.referenceData(),
          this.reportType(),
          this.targetPeriodType(),
          facilityBaseYear,
          productBaseYear,
        );
      }

      const throughputValue = throughputValues[i] == null ? null : String(throughputValues[i]);
      const adjustedThroughput = calculateAdjustedThroughput(throughputValue, throughputFactor, useSRM) ?? 0;
      const targetEnergy = calculateTargetEnergyForProduct(product.energy, adjustedThroughput, improvementTarget);

      return { improvementTarget, adjustedThroughput, targetEnergy };
    });
  });

  protected readonly totalTargetVariableEnergy = computed(() => {
    const throughputValues = this.currentThroughputValues();
    const facilityImprovementTarget = this.improvementTarget();
    const throughputFactor = this.throughputAdjustmentFactor();
    const useSRM = this.referenceData()?.baselineAndTargets?.usedReportingMechanism;
    const hasVariableEnergy = this.referenceData()?.baselineAndTargets?.variableEnergyType != null;

    // Sum of (baseline intensity x adjusted throughput) for all products
    const sumOfIntensityTimesAdjustedThroughput = this.baselineProducts().reduce((sum, product, i) => {
      const throughputValue = throughputValues[i] == null ? null : String(throughputValues[i]);
      const adjustedThroughput = calculateAdjustedThroughput(throughputValue, throughputFactor, useSRM ?? false) ?? 0;
      const intensity = toNumber(product.energy);
      return sum + intensity * adjustedThroughput;
    }, 0);

    // Apply facility improvement target once to the sum, return 0 if no variable energy
    return calculateFacilityTargetVariableEnergy(
      sumOfIntensityTimesAdjustedThroughput,
      facilityImprovementTarget,
      hasVariableEnergy,
    );
  });

  onSubmit() {
    if (this.form.invalid) return;

    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);
    const productCalculations = this.productCalculations();

    const productVariableEnergyData = this.form.controls.products.controls.map((control, index) => ({
      productName: control.getRawValue().productName,
      actualThroughput: String(control.getRawValue().actualThroughput),
      targetImprovement: String(productCalculations[index]?.improvementTarget ?? 0),
      adjustedThroughput: String(productCalculations[index]?.adjustedThroughput ?? 0),
      targetEnergy: String(productCalculations[index]?.targetEnergy ?? 0),
    }));

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.throughputDetails = {
        ...actionPayload.throughputDetails,
        totalTargetVariableEnergy: String(this.totalTargetVariableEnergy()),
        variableEnergyConsumptionDataByProduct: productVariableEnergyData,
      };
    });

    const currentSectionsCompleted = this.requestTaskStore.select(tprFormQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((error) => {
          console.error(error);
          return of(null);
        }),
      )
      .subscribe(() => {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
      });
  }
}
