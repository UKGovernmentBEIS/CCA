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
  areEntitiesIdentical,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  calculatePerformance,
  filterFieldsWithFalsyValues,
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

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { deleteDecision, resetDetermination } from '../../../utils';
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
  private readonly activatedRoute = inject(ActivatedRoute);
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
    )() as UNAVariationReviewRequestTaskPayload;

    const originalPayload = (
      this.requestTaskStore.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const updatedPayload = updateBaselineData(actionPayload, this.form, this.calculatedPerformance());
    const finalPayload = applyTp5BaselineDataSideEffect(updatedPayload);

    const originalTP5 = filterFieldsWithFalsyValues(originalPayload?.underlyingAgreement?.targetPeriod5Details);
    const currentTP5 = filterFieldsWithFalsyValues(finalPayload?.targetPeriod5Details);

    const areIdentical = areEntitiesIdentical(currentTP5, originalTP5);

    const currentDecisions = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? deleteDecision(currentDecisions, 'TARGET_PERIOD5_DETAILS') : currentDecisions;

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
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.requestTaskStore.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe((payload: UNAVariationReviewRequestTaskPayload) => {
      const wizardCompleted = isTargetPeriodWizardCompleted(payload.underlyingAgreement.targetPeriod5Details.details);
      const shouldNavigateToDecision = !areIdentical && wizardCompleted;

      const targetPath = shouldNavigateToDecision
        ? '../decision'
        : wizardCompleted
          ? '../check-your-answers'
          : `../${BaseLineAndTargetsStep.ADD_TARGETS}`;

      this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
    });
  }
}

function updateBaselineData(
  actionPayload: UnderlyingAgreementVariationReviewSavePayload,
  form: AddBaselineDataFormModel,
  calculatedPerformance: number,
): UnderlyingAgreementVariationReviewSavePayload {
  return produce(actionPayload, (draft) => {
    draft.targetPeriod6Details.baselineData = {
      isTwelveMonths: form.value.isTwelveMonths,
      baselineDate: form.value.baselineDate.toISOString().split('T')[0],
      explanation: form.value.explanation,
      greenfieldEvidences: fileUtils.toUUIDs(form.value.greenfieldEvidences) as string[],
      energy: String(form.value.energy),
      usedReportingMechanism: form.value.usedReportingMechanism,
      throughput: String(form.value.throughput),
      energyCarbonFactor: String(form.value.energyCarbonFactor),
      performance: String(calculatedPerformance),
    };
  });
}
