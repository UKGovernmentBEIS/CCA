import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { distinct } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  DateInputComponent,
  RadioComponent,
  RadioOptionComponent,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import {
  applyTp5BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  calculatePerformance,
  getMeasurementAndThroughputUnits,
  isTargetPeriodWizardCompleted,
  MeasurementTypeToUnitPipe,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import {
  ADD_BASELINE_DATA_FORM,
  AddBaselineDataFormModel,
  AddBaselineDataFormProvider,
} from './add-baseline-data-form.provider';

@Component({
  selector: 'cca-add-baseline-data',
  templateUrl: './add-baseline-data.component.html',
  imports: [
    WizardStepComponent,
    FormsModule,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    DateInputComponent,
    TextareaComponent,
    TextInputComponent,
    MultipleFileInputComponent,
    MeasurementTypeToUnitPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [AddBaselineDataFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddBaselineDataComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<AddBaselineDataFormModel>(ADD_BASELINE_DATA_FORM);

  protected readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(true), // TP5
  )();

  protected readonly isTwelveMonthsValue = toSignal(this.form.controls.isTwelveMonths.valueChanges, {
    initialValue: this.form.controls.isTwelveMonths.value,
  });
  protected readonly twelveMonthsSelected = computed(() => typeof this.isTwelveMonthsValue() === 'boolean');

  private readonly energyOrCarbonValue = toSignal(this.form.controls.energy.valueChanges, {
    initialValue: this.form.controls.energy.value,
  });

  private readonly throughputValue = toSignal(this.form.controls.throughput.valueChanges, {
    initialValue: this.form.controls.throughput.value,
  });

  protected readonly baselineDateValue = toSignal(this.form.controls.baselineDate.valueChanges, {
    initialValue: this.form.value.baselineDate,
  });

  protected readonly calculatedPerformance = computed(() => {
    if (this.targetComposition?.agreementCompositionType !== 'RELATIVE') return null;
    const energyOrCarbon = this.energyOrCarbonValue();
    const throughput = this.throughputValue();
    return calculatePerformance(energyOrCarbon, throughput);
  });

  protected readonly performance = computed(() => {
    const pipe = new DecimalPipe('en-GB');
    const suffix = getMeasurementAndThroughputUnits(
      this.targetComposition?.throughputUnit,
      this.targetComposition.measurementType,
    );
    return `${pipe.transform(this.calculatedPerformance(), '1.0-7')} ${suffix}`;
  });

  protected readonly performanceSuffix = getMeasurementAndThroughputUnits(
    this.targetComposition?.throughputUnit,
    this.targetComposition.measurementType,
  );

  protected readonly dateIsStartof2018 = computed(
    () => this.baselineDateValue() && this.baselineDateValue().getTime() === new Date('2018-01-01').getTime(),
  );

  constructor() {
    this.form.controls.isTwelveMonths.valueChanges.pipe(takeUntilDestroyed(), distinct()).subscribe((v) => {
      if (typeof v === 'boolean') {
        this.form.controls.baselineDate.reset();
        this.form.controls.explanation.reset();
        this.form.controls.greenfieldEvidences.reset();
        this.form.controls.energy.reset();
        this.form.controls.usedReportingMechanism.reset();
        this.form.controls.throughput.reset();
        this.form.controls.energyCarbonFactor.reset();
        this.form.updateValueAndValidity();
      }
    });

    effect(() => {
      if (this.isTwelveMonthsValue() && this.baselineDateValue() && this.dateIsStartof2018()) {
        this.form.controls.explanation.reset();
      }
    });
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.details.baselineData = {
        isTwelveMonths: this.form.value.isTwelveMonths,
        baselineDate: this.form.value.baselineDate.toISOString().split('T')[0],
        explanation: this.form.value.explanation,
        greenfieldEvidences: fileUtils.toUUIDs(this.form.value.greenfieldEvidences) as string[],
        energy: this.form.value.energy,
        usedReportingMechanism: this.form.value.usedReportingMechanism,
        performance: String(this.calculatedPerformance()),
        throughput: this.form.value.throughput,
        energyCarbonFactor: this.form.value.energyCarbonFactor,
      };
    });

    const finalPayload = applyTp5BaselineDataSideEffect(updatedPayload);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, finalPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const baselineExists = this.requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();

      const targetPeriodDetails = this.requestTaskStore.select(
        underlyingAgreementQuery.selectTargetPeriodDetails(true),
      )();

      const completed = baselineExists === false || isTargetPeriodWizardCompleted(targetPeriodDetails);
      if (completed) {
        this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
      } else {
        this.router.navigate([`../${BaseLineAndTargetsStep.ADD_TARGETS}`], { relativeTo: this.route });
      }
    });
  }
}
