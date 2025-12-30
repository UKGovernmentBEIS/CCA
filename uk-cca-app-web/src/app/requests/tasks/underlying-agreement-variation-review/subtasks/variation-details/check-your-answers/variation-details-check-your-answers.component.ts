import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  TaskItemStatus,
  TasksApiService,
  toVariationDetailsSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
  VARIATION_DETAILS_SUBTASK,
} from '@requests/common';
import { underlyingAgreementVariationReviewQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-variation-details-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Variation details">Check your answers</netz-page-heading>
      <cca-summary [data]="summaryData" />
      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toVariationDetailsSummaryDataWithDecision(
    this.store.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'))(),
    this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();
    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const actionPayload = toUnderlyingAgreementVariationReviewSavePayload(payload);

    const sectionsCompleted = produce(
      this.store.select(underlyingAgreementQuery.selectSectionsCompleted)(),
      (draft) => {
        draft[VARIATION_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
      },
    );

    const decision = this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'))();

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[VARIATION_DETAILS_SUBTASK] =
          decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
      },
    );

    const determination = resetDetermination(
      this.store.select(underlyingAgreementVariationReviewQuery.selectDetermination)(),
    );

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination,
      reviewGroupDecisions: payload.reviewGroupDecisions,
      facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
