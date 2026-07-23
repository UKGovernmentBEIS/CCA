import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  calculateAdjustedImprovementTarget,
  calculateAdjustedThroughput,
  calculateFacilityImprovementTarget,
  calculateProductTargetEnergy,
  calculateThroughputAdjustmentFactor,
  calculateThroughputValues,
  decideVariableEnergyType,
  resolveProductEnergyCarbonIntensity,
  roundHalfUpTo7Decimals,
  TaskItemStatus,
  TasksApiService,
  ThroughputDetailsSummaryComponent,
  toTotalsOnlySummaryData,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
  tprFormQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { logger } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toPerformanceDataFacilityDigitalFormSavePayload } from '../../../transform';

@Component({
  selector: 'cca-tpr-throughput-details-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Provide target period throughput details">Check your answers</netz-page-heading>

      @if (variableEnergyType() === 'BY_PRODUCT') {
        <cca-throughput-details-summary
          [referenceData]="referenceData()"
          [performanceData]="performanceData()"
          [isEditable]="isEditable()"
          [reportType]="reportType()"
          [targetPeriodType]="targetPeriodType()"
        />
      } @else {
        <cca-summary [data]="summaryData()" />
      }

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    SummaryComponent,
    ThroughputDetailsSummaryComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputDetailsCheckYourAnswersComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);
  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);
  protected readonly targetPeriodYear = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodYear);
  protected readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable);

  private readonly facilityBaseYear = computed(() => {
    const baselineDate = this.referenceData()?.baselineAndTargets?.baselineDate;
    if (!baselineDate) return null;

    const year = Number.parseInt(baselineDate.substring(0, 4), 10);
    return Number.isNaN(year) ? null : year;
  });

  private readonly throughputAdjustmentFactor = computed(() =>
    calculateThroughputAdjustmentFactor(
      Number(this.performanceData()?.energyFuelDetails?.standardFuels?.['GRID_ELECTRICITY']?.deliveredEnergy ?? 0),
      Number(this.performanceData()?.energyFuelDetails?.standardFuels?.['NON_GRID_ELECTRICITY']?.deliveredEnergy ?? 0),
      Number(this.performanceData()?.energyFuelDetails?.electricitySuppliedFromCHP ?? 0),
    ),
  );

  private readonly calculations = computed(() =>
    calculateThroughputValues({
      referenceData: this.referenceData(),
      performanceData: this.performanceData(),
      reportType: this.reportType(),
      targetPeriodType: this.targetPeriodType(),
      actualThroughput: this.performanceData().throughputDetails?.actualThroughput ?? null,
    }),
  );

  protected readonly variableEnergyType = computed(() =>
    decideVariableEnergyType(this.referenceData()?.baselineAndTargets?.variableEnergyType),
  );

  protected readonly summaryData = computed(() =>
    toTotalsOnlySummaryData({
      referenceData: this.referenceData(),
      performanceData: this.performanceData(),
      targetVariableEnergy: this.calculations().targetVariableEnergy,
      isEditable: this.isEditable(),
    }),
  );

  private buildByProductThroughputDetails() {
    const baselineProducts = this.referenceData()?.baselineAndTargets?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProducts = this.performanceData()?.throughputDetails?.variableEnergyConsumptionDataByProduct ?? [];
    const savedProductsByName = new Map(savedProducts.map((product) => [product.productName, product]));
    const facilityBaseYear = this.facilityBaseYear();
    const facilityImprovementTarget = calculateFacilityImprovementTarget(
      this.referenceData(),
      this.reportType(),
      this.targetPeriodType(),
    );
    const throughputFactor = this.throughputAdjustmentFactor();
    const useSRM = this.referenceData()?.baselineAndTargets?.usedReportingMechanism ?? false;

    let totalTargetVariableEnergy = 0;

    const variableEnergyConsumptionDataByProduct = baselineProducts.map((product) => {
      const savedProduct = savedProductsByName.get(product.productName);
      let improvementTarget = facilityImprovementTarget;

      if (facilityBaseYear != null && product.baselineYear > facilityBaseYear) {
        improvementTarget = calculateAdjustedImprovementTarget(
          this.referenceData(),
          this.reportType(),
          this.targetPeriodType(),
          facilityBaseYear,
          product.baselineYear,
        );
      }

      const adjustedThroughput =
        calculateAdjustedThroughput(savedProduct?.actualThroughput ?? null, throughputFactor, useSRM) ?? 0;

      const baselineEnergyIntensity = resolveProductEnergyCarbonIntensity(product);

      const targetEnergy = calculateProductTargetEnergy(baselineEnergyIntensity, adjustedThroughput, improvementTarget);
      totalTargetVariableEnergy += targetEnergy;

      return {
        productName: product.productName,
        actualThroughput: String(savedProduct?.actualThroughput ?? '0'),
        targetImprovement: roundHalfUpTo7Decimals(improvementTarget),
        adjustedThroughput: roundHalfUpTo7Decimals(adjustedThroughput),
        targetEnergy: roundHalfUpTo7Decimals(targetEnergy),
      };
    });

    return {
      totalTargetVariableEnergy: roundHalfUpTo7Decimals(totalTargetVariableEnergy),
      variableEnergyConsumptionDataByProduct,
    };
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);
    const calculations = this.calculations();

    const currentSectionsCompleted = this.requestTaskStore.select(tprFormQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[TPR_FORM_THROUGHPUT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const updatedPayload = produce(actionPayload, (draft) => {
      if (this.variableEnergyType() === 'BY_PRODUCT') {
        const byProductThroughputDetails = this.buildByProductThroughputDetails();

        draft.throughputDetails = {
          ...draft.throughputDetails,
          ...byProductThroughputDetails,
        };
      } else if (draft.throughputDetails?.targetImprovement != null) {
        const targetImprovement = Number(draft.throughputDetails.targetImprovement);

        const normalizedTargetImprovement = targetImprovement > 1 ? targetImprovement / 100 : targetImprovement;

        const calculatedTargetVariableEnergy = calculations.targetVariableEnergy;
        const adjustedThroughput = calculations.adjustedThroughput;

        draft.throughputDetails = {
          actualThroughput: draft.throughputDetails?.actualThroughput,
          targetImprovement: roundHalfUpTo7Decimals(normalizedTargetImprovement),
          adjustedThroughput: roundHalfUpTo7Decimals(adjustedThroughput ?? 0),
          totalTargetVariableEnergy:
            calculatedTargetVariableEnergy != null && calculatedTargetVariableEnergy > 0
              ? roundHalfUpTo7Decimals(calculatedTargetVariableEnergy)
              : roundHalfUpTo7Decimals(draft.throughputDetails.totalTargetVariableEnergy),
        };
      }
    });

    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .pipe(
        catchError((error) => {
          logger.error(error);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../../..'], { relativeTo: this.route, replaceUrl: true }));
  }
}
