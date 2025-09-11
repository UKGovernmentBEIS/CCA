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
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import { ADD_TARGETS_FORM, AddTargetsFormModel, AddTargetsFormProvider } from './add-targets-form.provider';

@Component({
  selector: 'cca-add-targets',
  templateUrl: './add-targets.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    WizardStepComponent,
    TextInputComponent,
    DecimalPipe,
    ReturnToTaskOrActionPageComponent,
  ],
  providers: [AddTargetsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddTargetsComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly form = inject<AddTargetsFormModel>(ADD_TARGETS_FORM);

  protected readonly targets = toSignal(this.form.controls.target.valueChanges, {
    initialValue: this.form.value.target,
  });

  protected readonly showTargets = computed(() => typeof this.targets() === 'number');

  protected readonly baselineData = this.store.select(underlyingAgreementQuery.selectBaselineData(false))();

  protected readonly targetComposition = this.store.select(underlyingAgreementQuery.selectTargetComposition(false))();

  readonly baselineUnitsSuffix = getBaselineUnits(
    this.targetComposition?.throughputUnit,
    this.targetComposition?.measurementType,
    this.targetComposition?.agreementCompositionType,
  );

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod6Details.targets = {
        improvement: this.form.value.improvement,
        target: this.form.value.target,
      };
    });

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_6_DETAILS] = TaskItemStatus.UNDECIDED;
      draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
    });

    const currDetermination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const determination = resetDetermination(currDetermination);

    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveActionDTO(requestTaskId, updatedPayload, {
      determination,
      reviewSectionsCompleted,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
    });
  }
}
