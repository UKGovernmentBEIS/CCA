import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

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
  AddBaselineDataFormModel,
  applyTp6BaselineDataSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  calculatePerformance,
  getMeasurementAndThroughputUnits,
  isTargetPeriodWizardCompleted,
  MeasurementTypeToUnitPipe,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementQuery,
} from '@requests/common';
import { MultipleFileInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { BaselineData, UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO } from '../../../transform';
import { toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';
import { ADD_BASELINE_DATA_FORM, addBaselineDataFormProvider } from './add-baseline-data-form.provider';

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
  providers: [addBaselineDataFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddBaselineDataComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly form = inject<AddBaselineDataFormModel>(ADD_BASELINE_DATA_FORM);

  protected readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(false),
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

  protected readonly baselineUnitsSuffix = getMeasurementAndThroughputUnits(
    this.targetComposition?.throughputUnit,
    this.targetComposition?.measurementType,
  );

  protected readonly showCalculatedPerformance = computed(() => {
    return this.targetComposition?.agreementCompositionType === 'RELATIVE' && this.calculatedPerformance() !== null;
  });

  protected readonly performanceSuffix = this.baselineUnitsSuffix;

  protected readonly performance = computed(() => this.calculatedPerformance());

  protected readonly dateIsStartof2018 = computed(() => {
    const date = this.baselineDateValue() as Date;
    if (!date) return false;
    return date.getFullYear() === 2018 && date.getMonth() === 0 && date.getDate() === 1;
  });

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod6Details.baselineData = {
        isTwelveMonths: this.form.value.isTwelveMonths,
        baselineDate: this.form.value.baselineDate?.toISOString(),
        explanation: this.form.value.explanation,
        greenfieldEvidences: this.form.value.greenfieldEvidences?.map((file) => file.uuid) || [],
        energy: this.form.value.energy,
        throughput: this.form.value.throughput,
        energyCarbonFactor: this.form.value.energyCarbonFactor,
        performance: String(this.calculatedPerformance()),
        usedReportingMechanism: this.form.value.usedReportingMechanism,
      } as BaselineData;
    });

    const payloadWithSideEffects = applyTp6BaselineDataSideEffect(updatedPayload);

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);

    const dto = createRequestTaskActionProcessDTO(
      requestTaskId,
      payloadWithSideEffects,
      sectionsCompleted,
      reviewProps,
    );

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe((payload: UnderlyingAgreementVariationSubmitRequestTaskPayload) => {
        const completed = isTargetPeriodWizardCompleted(payload.underlyingAgreement.targetPeriod6Details);
        if (completed) {
          this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
        } else {
          this.router.navigate(['../', BaseLineAndTargetsStep.ADD_TARGETS], { relativeTo: this.route });
        }
      });
  }
}
