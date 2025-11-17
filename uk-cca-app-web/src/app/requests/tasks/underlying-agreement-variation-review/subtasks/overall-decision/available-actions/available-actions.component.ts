import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective, WarningTextComponent } from '@netz/govuk-components';
import {
  OVERALL_DECISION_SUBTASK,
  OverallDecisionWizardStep,
  TaskItemStatus,
  TasksApiService,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { produce } from 'immer';

import { underlyingAgreementVariationReviewTaskQuery } from '../../../+state/una-variation-review.selectors';
import { createSaveDeterminationActionDTO } from '../../../transform';

@Component({
  selector: 'cca-underlying-agreement-available-actions',
  templateUrl: './available-actions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, ButtonDirective, WarningTextComponent, ReturnToTaskOrActionPageComponent],
})
export class AvailableActionsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly tasksApiService = inject(TasksApiService);

  protected readonly canReject = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectCanReject,
  )();

  protected readonly canAccept = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectCanAccept,
  )();

  protected readonly rejectionWarning = this.requestTaskStore.select(
    underlyingAgreementVariationReviewTaskQuery.selectRejectionWarning,
  )();

  submit(type: 'ACCEPTED' | 'REJECTED') {
    const requestTaskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)();

    const reviewSectionsCompleted = produce(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[OVERALL_DECISION_SUBTASK] = TaskItemStatus.UNDECIDED;
      },
    );

    const updatedDetermination = produce(
      this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)(),
      (draft) => {
        draft.type = type;
        draft.reason = null;
      },
    );

    const dto = createSaveDeterminationActionDTO(requestTaskId, updatedDetermination, reviewSectionsCompleted);

    this.tasksApiService
      .saveRequestTaskAction(dto)
      .subscribe(() =>
        this.router.navigate(
          [
            '../',
            type === 'ACCEPTED' ? OverallDecisionWizardStep.ADDITIONAL_INFO : OverallDecisionWizardStep.EXPLANATION,
          ],
          { relativeTo: this.activatedRoute },
        ),
      );
  }
}
