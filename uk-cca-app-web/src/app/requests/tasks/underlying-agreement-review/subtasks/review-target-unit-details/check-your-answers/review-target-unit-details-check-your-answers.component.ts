import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toReviewTargetUnitDetailsSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveDecisionActionDTO } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Target unit details">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ReviewTargetUnitDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(RequestTaskStore);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  protected readonly summaryData = toReviewTargetUnitDetailsSummaryDataWithDecision(
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)(),
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))(),
    this.attachments,
    this.downloadUrl,
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
  );

  onSubmit() {
    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const decision = this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'))();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] =
        decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const determination = this.store.select(underlyingAgreementReviewQuery.selectDetermination)();
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const dto = createSaveDecisionActionDTO(
      requestTaskId,
      'TARGET_UNIT_DETAILS',
      reviewSectionsCompleted,
      decision,
      determination,
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute });
    });
  }
}
