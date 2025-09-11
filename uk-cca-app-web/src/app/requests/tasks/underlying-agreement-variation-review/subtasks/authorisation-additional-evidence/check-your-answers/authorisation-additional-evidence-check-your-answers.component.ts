import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { ButtonDirective } from '@netz/govuk-components';
import {
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
  standalone: true,
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

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.store.select(underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'))(),
    this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.store.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.store.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.store.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.store.select(underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'))(),
    this.store.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  onSubmit() {
    const requestTaskId = this.store.select(requestTaskQuery.selectRequestTaskId)();

    const payload = this.store.select(
      requestTaskQuery.selectRequestTaskPayload,
    )() as UNAVariationReviewRequestTaskPayload;

    const currentReviewSectionsCompleted = this.store.select(
      underlyingAgreementReviewQuery.selectReviewSectionsCompleted,
    )();

    const decision = this.store.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
    )();

    const reviewSectionsCompleted = produce(currentReviewSectionsCompleted, (draft) => {
      draft[AUTHORISATION_ADDITIONAL_EVIDENCE_SUBTASK] =
        decision.type === 'ACCEPTED' ? TaskItemStatus.ACCEPTED : TaskItemStatus.REJECTED;
    });

    const dto = createSaveActionDTO(
      requestTaskId,
      payload.underlyingAgreement as UnderlyingAgreementVariationReviewSavePayload, // These have the same fields, so the casting is to satisfy TypeScript
      {
        sectionsCompleted: payload.sectionsCompleted,
        reviewSectionsCompleted,
        determination: payload.determination,
      },
    );

    this.tasksApiService.saveRequestTaskAction(dto).subscribe(() => {
      this.router.navigate(['../../..'], { relativeTo: this.activatedRoute, replaceUrl: true });
    });
  }
}
