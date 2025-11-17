import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
  areEntitiesIdentical,
  AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK,
  TaskItemStatus,
  TasksApiService,
  toAuthorisationAdditionalEvidenceSummaryDataWithDecision,
  UNAVariationReviewRequestTaskPayload,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';
import { produce } from 'immer';

import { UnderlyingAgreementVariationReviewSavePayload } from 'cca-api';

import { createSaveActionDTO } from '../../../transform';

@Component({
  selector: 'cca-check-your-answers',
  template: `
    <div>
      <netz-page-heading caption="Authorisation and additional evidence">Check your answers</netz-page-heading>

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
    ButtonDirective,
    PageHeadingComponent,
    PendingButtonDirective,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceCheckYourAnswersComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly tasksApiService = inject(TasksApiService);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  private readonly isEditable = this.store.select(requestTaskQuery.selectIsEditable)();
  private readonly reviewAttachments = this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly currentAdditionalEvidence = this.store.select(
    underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
  )();
  private readonly currentAttachments = this.store.select(
    underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments,
  )();

  private readonly originalAdditionalEvidence = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence,
  )();
  private readonly originalAttachments = this.store.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly areIdentical = areEntitiesIdentical(this.currentAdditionalEvidence, this.originalAdditionalEvidence);

  private readonly decision = this.store.select(
    underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
  )();

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.originalAdditionalEvidence,
    this.originalAttachments,
    this.isEditable,
    this.downloadUrl,
    this.decision,
    this.reviewAttachments,
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.currentAdditionalEvidence,
    this.currentAttachments,
    this.isEditable,
    this.downloadUrl,
    this.decision,
    this.reviewAttachments,
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] = this.areIdentical
        ? TaskItemStatus.UNCHANGED
        : this.decision.type === 'ACCEPTED'
          ? TaskItemStatus.ACCEPTED
          : TaskItemStatus.REJECTED;
    });

    const dto = createSaveActionDTO(
      requestTaskId,
      payload.underlyingAgreement as UnderlyingAgreementVariationReviewSavePayload, // These have the same fields, so the casting is to satisfy TypeScript
      {
        sectionsCompleted: payload.sectionsCompleted,
        reviewSectionsCompleted,
        determination: payload.determination,
        reviewGroupDecisions: payload.reviewGroupDecisions,
        facilitiesReviewGroupDecisions: payload.facilitiesReviewGroupDecisions,
      },
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
