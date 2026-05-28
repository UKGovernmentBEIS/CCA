import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { generateDownloadUrl } from '@shared/utils';

import { nonComplianceEnforcementResponseNoticeQuery } from '../+state/non-compliance-enforcement-response-notice-selectors';
import { toEnforcementResponseNoticeSummaryData } from '../enforcement-response-notice-summary-data';

@Component({
  selector: 'cca-upload-enforcement-response-notice-summary',
  template: `<cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UploadEnforcementResponseNoticeSummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly enforcementResponseNotice = this.requestTaskStore.select(
    nonComplianceEnforcementResponseNoticeQuery.selectEnforcementResponseNotice,
  );
  private readonly nonComplianceAttachments = this.requestTaskStore.select(
    nonComplianceEnforcementResponseNoticeQuery.selectNonComplianceAttachments,
  );
  private readonly isPenaltyReissue = this.requestTaskStore.select(
    nonComplianceEnforcementResponseNoticeQuery.selectIsPenaltyReissue,
  );
  private readonly taskId = this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId);

  protected readonly summaryData = computed(() =>
    toEnforcementResponseNoticeSummaryData(
      this.enforcementResponseNotice(),
      this.nonComplianceAttachments(),
      false,
      generateDownloadUrl(this.taskId().toString()),
      this.isPenaltyReissue(),
    ),
  );
}
