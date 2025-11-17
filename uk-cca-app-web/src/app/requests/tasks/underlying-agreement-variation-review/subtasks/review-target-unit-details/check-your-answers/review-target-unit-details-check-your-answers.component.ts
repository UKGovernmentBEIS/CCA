import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  filterFieldsWithFalsyValues,
  TaskItemStatus,
  TasksApiService,
  toVariationReviewTargetUnitDetailsSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
} from '@requests/common';
import {
  REVIEW_TARGET_UNIT_DETAILS_SUBTASK,
  transformAccountReferenceData,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { createSaveActionDTO, toUnderlyingAgreementVariationReviewSavePayload } from '../../../transform';
import { resetDetermination } from '../../../utils';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Target unit details">Check your answers</netz-page-heading>

      <ng-template #contentTpl let-showOriginal="showOriginal">
        <cca-summary [data]="showOriginal ? summaryDataOriginal : summaryDataCurrent" />
      </ng-template>

      <cca-highlight-diff>
        <ng-container slot="previous" *ngTemplateOutlet="contentTpl; context: { showOriginal: true }" />
        <ng-container slot="current" *ngTemplateOutlet="contentTpl; context: { showOriginal: false }" />
      </cca-highlight-diff>

      <button netzPendingButton govukButton type="button" (click)="onSubmit()">Confirm and complete</button>
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);
  private readonly store = inject(RequestTaskStore);

  private readonly downloadUrl = generateDownloadUrl(
    this.store.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();
  private readonly attachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  private readonly accountReferenceData = this.store.select(underlyingAgreementQuery.selectAccountReferenceData)();
  private readonly originalTargetUnitDetails = transformAccountReferenceData(this.accountReferenceData);
  private readonly currentTargetUnitDetails = this.store.select(
    underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
  )();

  private readonly areIdentical = areEntitiesIdentical(
    filterFieldsWithFalsyValues(this.currentTargetUnitDetails),
    filterFieldsWithFalsyValues(this.originalTargetUnitDetails),
  );

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
  )();

  protected readonly summaryDataOriginal = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.originalTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );

  protected readonly summaryDataCurrent = toVariationReviewTargetUnitDetailsSummaryDataWithDecision(
    this.currentTargetUnitDetails,
    this.decision,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
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
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = TaskItemStatus.COMPLETED;
      },
    );

    const reviewSectionsCompleted = produce(
      this.store.select(underlyingAgreementReviewQuery.selectReviewSectionsCompleted)(),
      (draft) => {
        draft[REVIEW_TARGET_UNIT_DETAILS_SUBTASK] = this.areIdentical
          ? TaskItemStatus.UNCHANGED
          : this.decision.type === 'ACCEPTED'
            ? TaskItemStatus.ACCEPTED
            : TaskItemStatus.REJECTED;
      },
    );

    const determination = resetDetermination(this.store.select(underlyingAgreementReviewQuery.selectDetermination)());

    const dto = createSaveActionDTO(requestTaskId, actionPayload, {
      sectionsCompleted,
      reviewSectionsCompleted,
      determination: determination,
      reviewGroupDecisions: payload.reviewGroupDecisions,
      facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
    });

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
