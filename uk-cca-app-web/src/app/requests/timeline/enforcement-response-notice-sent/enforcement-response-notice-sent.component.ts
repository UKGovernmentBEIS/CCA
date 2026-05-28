import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload } from 'cca-api';

import { toEnforcementResponseNoticeSentSummaryData } from './enforcement-response-notice-sent-summary';

@Component({
  selector: 'cca-enforcement-response-notice-sent',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnforcementResponseNoticeSentComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() => {
    const payload = this.actionPayload() as NonComplianceEnforcementResponseNoticeSubmittedRequestActionPayload;

    return toEnforcementResponseNoticeSentSummaryData(
      payload.enforcementResponseNotice,
      payload.nonComplianceAttachments,
      payload.defaultContacts,
      './file-download',
      payload.decisionNotification,
      payload.usersInfo,
    );
  });
}
