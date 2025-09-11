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
  underlyingAgreementQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { Targets, UnderlyingAgreementVariationSubmitRequestTaskPayload } from 'cca-api';

import { createRequestTaskActionProcessDTO, toUnderlyingAgreementVariationSavePayload } from '../../../transform';
import { extractReviewProps } from '../../../utils';
import { ADD_TARGETS_FORM, AddTargetsFormModel, addTargetsFormProvider } from './add-targets-form.provider';

@Component({
  selector: 'cca-add-targets',
  templateUrl: './add-targets.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
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
    underlyingAgreementQuery.selectTargetComposition(false), // TP6
  )();

  protected readonly baselineData = this.requestTaskStore.select(underlyingAgreementQuery.selectBaselineData(false))();

  private readonly improvementValue = toSignal(this.form.get('improvement').valueChanges, {
    initialValue: this.form.get('improvement').value,
  });

  protected readonly targets = toSignal(this.form.get('target').valueChanges, {
    initialValue: this.form.get('target').value,
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
    )() as UnderlyingAgreementVariationSubmitRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod6Details.targets = {
        improvement: this.form.value.improvement,
        target: this.form.value.target,
      } as Targets;
    });

    const currentSectionsCompleted = this.requestTaskStore.select(underlyingAgreementQuery.selectSectionsCompleted)();
    const sectionsCompleted = produce(currentSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.IN_PROGRESS;
    });

    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();
    const reviewProps = extractReviewProps(this.requestTaskStore);
    const dto = createRequestTaskActionProcessDTO(requestTaskId, updatedPayload, sectionsCompleted, reviewProps);

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../check-your-answers'], { relativeTo: this.route });
    });
  }
}
