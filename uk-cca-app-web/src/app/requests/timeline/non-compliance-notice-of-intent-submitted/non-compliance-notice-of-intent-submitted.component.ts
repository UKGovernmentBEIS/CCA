import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { NonComplianceNoticeOfIntentSubmittedRequestActionPayload } from 'cca-api';

import { toNonComplianceNoticeOfIntentSubmittedSummaryData } from './non-compliance-notice-of-intent-submitted-summary';

@Component({
  selector: 'cca-non-compliance-notice-of-intent-submitted',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceNoticeOfIntentSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() => {
    const payload = this.actionPayload() as NonComplianceNoticeOfIntentSubmittedRequestActionPayload;

    return toNonComplianceNoticeOfIntentSubmittedSummaryData(
      payload.noticeOfIntent,
      payload.nonComplianceAttachments,
      payload.defaultContacts,
      './file-download',
      payload.decisionNotification,
      payload.usersInfo,
    );
  });
}
