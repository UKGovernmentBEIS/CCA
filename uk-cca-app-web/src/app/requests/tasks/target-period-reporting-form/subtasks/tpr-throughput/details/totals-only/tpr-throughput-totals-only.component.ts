import { DecimalPipe, PercentPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { DetailsComponent, GovukValidators } from '@netz/govuk-components';
import {
  calculateThroughputValues,
  decideVariableEnergyType,
  roundHalfUpTo7Decimals,
  TaskItemStatus,
  TasksApiService,
  toTPRBaselineDataDetails,
  TPR_FORM_THROUGHPUT_DETAILS_SUBTASK,
  tprFormQuery,
} from '@requests/common';
import { SummaryComponent, TextInputComponent, WizardStepComponent } from '@shared/components';
import { MeasurementTypeToUnitPipe } from '@shared/pipes';
import { CCAGovukValidators } from '@shared/validators';
import { produce } from 'immer';

import {
  createRequestTaskActionProcessDTO,
  toPerformanceDataFacilityDigitalFormSavePayload,
} from '../../../../transform';

@Component({
  selector: 'cca-tpr-throughput-totals-only',
  templateUrl: './tpr-throughput-totals-only.component.html',
  imports: [
    ReactiveFormsModule,
    MeasurementTypeToUnitPipe,
    DecimalPipe,
    TextInputComponent,
    DetailsComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    WizardStepComponent,
    PercentPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TprThroughputTotalsOnlyComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly referenceData = this.requestTaskStore.select(tprFormQuery.selectReferenceData);
  protected readonly performanceData = this.requestTaskStore.select(tprFormQuery.selectPerformanceData);
  protected readonly reportType = this.requestTaskStore.select(tprFormQuery.selectReportType);
  protected readonly targetPeriodType = this.requestTaskStore.select(tprFormQuery.selectTargetPeriodType);

  protected readonly form = new FormGroup({
    actualThroughput: new FormControl<string | null>(
      this.performanceData()?.throughputDetails?.actualThroughput ?? null,
      [
        GovukValidators.required('Enter the total throughput'),
        GovukValidators.min(0, 'Enter a value equal to or greater than 0'),
        CCAGovukValidators.maxDecimalsWithMessage(7, 'Enter a number up to 7 decimal places'),
      ],
    ),
  });

  private readonly variableEnergyExists = computed(
    () => this.referenceData()?.baselineAndTargets?.baselineVariableEnergy,
  );

  private readonly variableEnergyType = computed(() => this.referenceData()?.baselineAndTargets?.variableEnergyType);

  protected readonly variableEnergyTypeLabel = computed(() =>
    decideVariableEnergyType(this.variableEnergyType()) === 'BY_PRODUCT'
      ? 'Split by product'
      : this.variableEnergyExists()
        ? 'Totals only'
        : 'No variable energy (only fixed energy)',
  );

  protected readonly isTotalsOnlyVariableEnergy = computed(() => this.variableEnergyType() === 'TOTALS');

  protected readonly measurementUnit = computed(() => this.referenceData()?.baselineAndTargets?.measurementType);

  readonly hasEnergyMeasurement = computed(() =>
    ['ENERGY_KWH', 'ENERGY_MWH', 'ENERGY_GJ'].includes(this.measurementUnit()),
  );

  protected readonly actualThroughput = toSignal(this.form.controls.actualThroughput.valueChanges, {
    initialValue: this.form.controls.actualThroughput.value,
  });

  protected readonly baselineDetails = computed(() => toTPRBaselineDataDetails(this.referenceData()));

  protected readonly calculations = computed(() =>
    calculateThroughputValues({
      referenceData: this.referenceData(),
      performanceData: this.performanceData(),
      reportType: this.reportType(),
      targetPeriodType: this.targetPeriodType(),
      actualThroughput: this.actualThroughput(),
    }),
  );

  protected readonly targetLabel = computed(() =>
    this.reportType() === 'INTERIM' ? 'Interim target' : 'Improvement target',
  );

  protected readonly baselineEnergyIntensity = computed(() => this.calculations().baselineEnergyIntensity);
  protected readonly improvementTarget = computed(() => this.calculations().improvementTarget);
  protected readonly adjustedThroughput = computed(() => this.calculations().adjustedThroughput);
  protected readonly targetVariableEnergy = computed(() => this.calculations().targetVariableEnergy);

  onSubmit() {
    if (this.form.invalid) return;

    const payload = this.requestTaskStore.select(tprFormQuery.selectPayload)();
    const actionPayload = toPerformanceDataFacilityDigitalFormSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.throughputDetails = {
        ...actionPayload.throughputDetails,
        actualThroughput: this.actualThroughput(),
        targetImprovement: roundHalfUpTo7Decimals(this.improvementTarget()),
        adjustedThroughput: roundHalfUpTo7Decimals(this.adjustedThroughput() ?? 0),
        totalTargetVariableEnergy: roundHalfUpTo7Decimals(this.targetVariableEnergy() ?? 0),
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
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['../check-your-answers'], { relativeTo: this.route }));
  }
}
