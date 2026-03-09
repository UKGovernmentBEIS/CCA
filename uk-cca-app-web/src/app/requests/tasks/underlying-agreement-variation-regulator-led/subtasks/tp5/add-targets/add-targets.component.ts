import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  BaselineAndTargetPeriodsSubtasks,
  getBaselineUnits,
  TaskItemStatus,
  TasksApiService,
  UNAVariationRegulatorLedRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementVariationRegulatorLedQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { Targets } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnAVariationRegulatorLedSavePayload } from '../../../transform';
import { ADD_TARGETS_FORM, AddTargetsFormModel, addTargetsFormProvider } from './add-targets-form.provider';

@Component({
  selector: 'cca-add-targets',
  templateUrl: './add-targets.component.html',
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    ReturnToTaskOrActionPageComponent,
    DecimalPipe,
  ],
  providers: [addTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddTargetsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly form = inject<AddTargetsFormModel>(ADD_TARGETS_FORM);

  protected readonly targetComposition = this.requestTaskStore.select(
    underlyingAgreementQuery.selectTargetComposition(true), // TP5
  )();

  protected readonly baselineData = this.requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(true))();

  private readonly improvementValue = toSignal(this.form.controls.improvement.valueChanges, {
    initialValue: this.form.controls.improvement.value,
  });

  protected readonly targets = toSignal(this.form.controls.target.valueChanges, {
    initialValue: this.form.controls.target.value,
  });

  protected readonly baselineUnitsSuffix = getBaselineUnits(
    this.targetComposition?.throughputUnit,
    this.targetComposition?.measurementType,
    this.targetComposition?.agreementCompositionType,
  );

  protected readonly showTargets = computed(() => {
    return (
      !!this.improvementValue() &&
      !!this.baselineData?.energy &&
      this.targetComposition?.agreementCompositionType !== 'NOVEM'
    );
  });

  onSubmit() {
    const payload = this.requestTaskStore.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationRegulatorLedRequestTaskPayload;

    const actionPayload = toUnAVariationRegulatorLedSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.details.targets = {
        improvement: this.form.value.improvement,
        target: this.form.value.target,
      } as Targets;
    });

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();

    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const determination = this.requestTaskStore.select(
      underlyingAgreementVariationRegulatorLedQuery.selectDetermination,
    )();

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, determination);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}
