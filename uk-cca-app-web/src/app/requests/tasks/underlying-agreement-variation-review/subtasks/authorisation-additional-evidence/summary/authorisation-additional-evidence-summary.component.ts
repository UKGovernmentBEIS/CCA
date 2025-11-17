import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toAuthorisationAdditionalEvidenceSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { HighlightDiffComponent, SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-authorisation-additional-evidence-summary',
  template: `
    <div>
      <netz-page-heading caption="Authorisation and additional evidence">Summary</netz-page-heading>

      <ng-template #contentTpl let-showOriginal="showOriginal">
        <cca-summary [data]="showOriginal ? summaryDataOriginal : summaryDataCurrent" />
      </ng-template>

      <cca-highlight-diff>
        <ng-container slot="previous" *ngTemplateOutlet="contentTpl; context: { showOriginal: true }" />
        <ng-container slot="current" *ngTemplateOutlet="contentTpl; context: { showOriginal: false }" />
      </cca-highlight-diff>
    </div>

    <netz-return-to-task-or-action-page />
  `,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  private readonly reviewAttachments = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectReviewAttachments,
  )();

  private readonly currentAdditionalEvidence = this.requestTaskStore.select(
    underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence,
  )();
  private readonly currentAttachments = this.requestTaskStore.select(
    underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments,
  )();

  private readonly originalAdditionalEvidence = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence,
  )();
  private readonly originalAttachments = this.requestTaskStore.select(
    underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments,
  )();

  private readonly decision = this.requestTaskStore.select(
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
}
