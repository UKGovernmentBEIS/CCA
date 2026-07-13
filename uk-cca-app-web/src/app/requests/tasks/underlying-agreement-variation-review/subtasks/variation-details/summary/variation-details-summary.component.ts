import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  toVariationDetailsSummaryDataWithDecision,
  underlyingAgreementReviewQuery,
  underlyingAgreementVariationQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-variation-details-summary',
  template: `
    <div>
      <netz-page-heading caption="Variation details">Summary</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class VariationDetailsSummaryComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly taskId = this.activatedRoute.snapshot.paramMap.get('taskId');

  protected readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly summaryData = toVariationDetailsSummaryDataWithDecision({
    variationDetails: this.requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)(),
    isEditable: this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
    downloadUrl: this.downloadUrl,
    decision: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectSubtaskDecision('VARIATION_DETAILS'))(),
    reviewAttachments: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
  });
}
