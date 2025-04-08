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
  standalone: true,
  imports: [
    PageHeadingComponent,
    SummaryComponent,
    ReturnToTaskOrActionPageComponent,
    HighlightDiffComponent,
    NgTemplateOutlet,
  ],
  templateUrl: './authorisation-additional-evidence-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AuthorisationAdditionalEvidenceSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryDataOriginal = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementVariationQuery.selectOriginalUnderlyingAgreementAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
    )(),
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );

  protected readonly summaryDataCurrent = toAuthorisationAdditionalEvidenceSummaryDataWithDecision(
    this.requestTaskStore.select(underlyingAgreementQuery.selectAuthorisationAndAdditionalEvidence)(),
    this.requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreementSubmitAttachments)(),
    this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    this.downloadUrl,
    this.requestTaskStore.select(
      underlyingAgreementReviewQuery.selectSubtaskDecision('AUTHORISATION_AND_ADDITIONAL_EVIDENCE'),
    )(),
    this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  );
}
