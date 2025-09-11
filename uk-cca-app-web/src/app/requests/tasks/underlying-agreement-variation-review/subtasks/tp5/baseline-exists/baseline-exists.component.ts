import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { NotificationBannerComponent, RadioComponent, RadioOptionComponent } from '@netz/govuk-components';
import {
  applyTp5ExistSideEffect,
  BaselineAndTargetPeriodsSubtasks,
  BaseLineAndTargetsStep,
  OVERALL_DECISION_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { WizardStepComponent } from '@shared/components';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';
import {
  BASELINE_EXISTS_FORM,
  BaselineExistsFormModel,
  BaselineExistsFormProvider,
} from './baseline-exists-form.provider';

@Component({
  selector: 'cca-baseline-exists',
  templateUrl: './baseline-exists.component.html',
  standalone: true,
  imports: [
    WizardStepComponent,
    ReactiveFormsModule,
    RadioOptionComponent,
    RadioComponent,
    ReturnToTaskOrActionPageComponent,
    NotificationBannerComponent,
  ],
  providers: [BaselineExistsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BaselineExistsComponent {
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly form = inject<BaselineExistsFormModel>(BASELINE_EXISTS_FORM);

  protected readonly showNotificationBanner = this.router.url.includes('underlying-agreement-application');

  onSubmit() {
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const baselineExists = this.form.get('exist')?.value;

    let updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.exist = baselineExists;
    });

    updatedPayload = applyTp5ExistSideEffect(updatedPayload);

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[BaselineAndTargetPeriodsSubtasks.TARGET_PERIOD_5_DETAILS] = TaskItemStatus.UNDECIDED;
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
      if (baselineExists) {
        this.router.navigate([`../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`], { relativeTo: this.activatedRoute });
      } else {
        this.router.navigate(['../decision'], { relativeTo: this.activatedRoute });
      }
    });
  }
}
