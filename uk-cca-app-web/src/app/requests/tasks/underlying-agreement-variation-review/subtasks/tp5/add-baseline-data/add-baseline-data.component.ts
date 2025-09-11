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
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { fileUtils } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  ADD_BASELINE_DATA_FORM,
  AddBaselineDataFormModel,
  AddBaselineDataFormProvider,
} from './add-baseline-data-form.provider';

@Component({
  selector: 'cca-add-baseline-data',
  templateUrl: './add-baseline-data.component.html',
  standalone: true,
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
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<AddBaselineDataFormModel>(ADD_BASELINE_DATA_FORM);

  protected readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(true), // TP5
  )();

  protected readonly isTwelveMonthsValue = toSignal(this.form.get('isTwelveMonths').valueChanges, {
    initialValue: this.form.get('isTwelveMonths').value,
  });
  protected readonly twelveMonthsSelected = computed(() => typeof this.isTwelveMonthsValue() === 'boolean');

  private readonly energyOrCarbonValue = toSignal(this.form.get('energy').valueChanges, {
    initialValue: this.form.get('energy').value,
  });

  private readonly throughputValue = toSignal(this.form.get('throughput').valueChanges, {
    initialValue: this.form.get('throughput').value,
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

  protected readonly dateIsStartof2018 = computed(() => {
    return this.baselineDateValue() && this.baselineDateValue().getTime() === new Date('2018-01-01').getTime();
  });

  constructor() {
    this.form.controls.isTwelveMonths.valueChanges.pipe(takeUntilDestroyed(), distinct()).subscribe((v) => {
      if (typeof v === 'boolean') {
        this.form.get('baselineDate').reset();
        this.form.get('explanation').reset();
        this.form.get('greenfieldEvidences').reset();
        this.form.get('energy').reset();
        this.form.get('usedReportingMechanism').reset();
        this.form.get('throughput').reset();
        this.form.get('energyCarbonFactor').reset();
        this.form.updateValueAndValidity();
      }
    });

    effect(() => {
      if (this.isTwelveMonthsValue() && this.baselineDateValue() && this.dateIsStartof2018()) {
        this.form.get('explanation').reset();
      }
    });
  }

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.details.baselineData = {
        isTwelveMonths: this.form.value.isTwelveMonths,
        baselineDate: this.form.value.baselineDate.toISOString().split('T')[0],
        explanation: this.form.value.explanation,
        greenfieldEvidences: fileUtils.toUUIDs(this.form.value.greenfieldEvidences) as string[],
        energy: this.form.value.energy,
        usedReportingMechanism: this.form.value.usedReportingMechanism,
        throughput: this.form.value.throughput,
        energyCarbonFactor: this.form.value.energyCarbonFactor,
        performance: this.calculatedPerformance(),
      };
    });

    // Apply business logic side effects for baseline data changes
    const finalPayload = applyTp5BaselineDataSideEffect(updatedPayload);

    const currentReviewSectionsCompleted = this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, finalPayload, {
      determination,
      reviewSectionsCompleted,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const wizardCompleted = isTargetPeriodWizardCompleted(payload.underlyingAgreement.targetPeriod5Details.details);

      wizardCompleted
        ? this.router.navigate(['../decision'], { relativeTo: this.activatedRoute })
        : this.router.navigate(['../', BaseLineAndTargetsStep.ADD_TARGETS], { relativeTo: this.activatedRoute });
    });
  }
}
