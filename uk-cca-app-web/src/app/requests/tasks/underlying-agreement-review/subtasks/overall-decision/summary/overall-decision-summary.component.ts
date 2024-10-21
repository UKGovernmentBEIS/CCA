import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementReviewQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils/download-url-generator';

import { toOverallDecisionSummaryData } from '../to-overall-decision-summary-data';

@Component({
  selector: 'cca-overall-decision-summary',
  standalone: true,
  template: `
    <div>
      <netz-page-heading [caption]="caption">Summary</netz-page-heading>

      <cca-summary [data]="summaryData" />
    </div>

    <netz-return-to-task-or-action-page></netz-return-to-task-or-action-page>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SummaryComponent, PageHeadingComponent, ReturnToTaskOrActionPageComponent],
})
export class OverallDecisionSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  readonly determination = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectDetermination)();
  readonly attachments = this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)();
  readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );
  readonly isEditable = this.requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  readonly caption = this.determination.type === 'ACCEPTED' ? 'Accept' : 'Reject';
  readonly summaryData = toOverallDecisionSummaryData(
    this.determination,
    this.attachments,
    this.downloadUrl,
    this.isEditable,
  );
}
