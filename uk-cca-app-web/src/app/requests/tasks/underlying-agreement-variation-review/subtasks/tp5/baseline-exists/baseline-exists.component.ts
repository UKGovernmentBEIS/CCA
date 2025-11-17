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
import { deleteDecision, resetDetermination } from '../../../utils';
import {
  BASELINE_EXISTS_FORM,
  BaselineExistsFormModel,
  BaselineExistsFormProvider,
} from './baseline-exists-form.provider';

@Component({
  selector: 'cca-baseline-exists',
  templateUrl: './baseline-exists.component.html',
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

    const originalPayload = (
      this.store.select(requestTaskQuery.selectRequestTaskPayload)() as UNAVariationReviewRequestTaskPayload
    )?.originalUnderlyingAgreementContainer;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);
    const baselineExists = this.form.get('exist')?.value;

    let updatedPayload = produce(actionPayload, (draft) => {
      draft.targetPeriod5Details.exist = baselineExists;
    });

    updatedPayload = applyTp5ExistSideEffect(updatedPayload);

    const originalTP5Exist = originalPayload?.underlyingAgreement?.targetPeriod5Details.exist;
    const currentTP5Exist = updatedPayload?.targetPeriod5Details.exist;
    const areIdentical = currentTP5Exist === originalTP5Exist;

    const currentDecisions = this.store.select(underlyingAgreementReviewQuery.selectReviewGroupDecisions)();
    const decisions = areIdentical ? deleteDecision(currentDecisions, 'TARGET_PERIOD5_DETAILS') : currentDecisions;

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
      reviewGroupDecisions: decisions,
      facilitiesReviewGroupDecisions: this.store.select(
        underlyingAgreementReviewQuery.selectFacilityReviewGroupDecisions,
      )(),
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      const targetPath = areIdentical
        ? '../check-your-answers'
        : baselineExists
          ? `../${BaseLineAndTargetsStep.TARGET_COMPOSITION}`
          : '../decision';

      this.router.navigate([targetPath], { relativeTo: this.activatedRoute });
    });
  }
}
