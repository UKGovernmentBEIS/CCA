import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { toOverallDecisionSummaryData, underlyingAgreementReviewQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-overall-decision-summary',
  template: `
    <div>
      <netz-page-heading [caption]="caption">Summary</netz-page-heading>
      <cca-summary [data]="summaryData" />
    </div>

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <netz-return-to-task-or-action-page />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
})
export class OverallDecisionSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
  private readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  protected readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';

  protected readonly summaryData = toOverallDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );
}
