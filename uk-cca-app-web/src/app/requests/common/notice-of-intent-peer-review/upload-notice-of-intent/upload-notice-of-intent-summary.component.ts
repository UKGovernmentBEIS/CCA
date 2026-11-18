import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { noticeOfIntentPeerReviewQuery } from '../+state/notice-of-intent-peer-review-selectors';
import { toNoticeOfIntentSummaryData } from '../notice-of-intent-summary-data';

@Component({
  selector: 'cca-upload-notice-of-intent-summary',
  template: `<cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadNoticeOfIntentSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly noticeOfIntent = this.requestTaskStore.select(noticeOfIntentPeerReviewQuery.selectNoticeOfIntent);
  private readonly nonComplianceAttachments = this.requestTaskStore.select(
    noticeOfIntentPeerReviewQuery.selectNonComplianceAttachments,
  );
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  protected readonly summaryData = computed(() =>
    toNoticeOfIntentSummaryData(
      this.noticeOfIntent(),
      this.nonComplianceAttachments(),
      false,
      generateDownloadUrl(this.taskId().toString()),
    ),
  );
}
