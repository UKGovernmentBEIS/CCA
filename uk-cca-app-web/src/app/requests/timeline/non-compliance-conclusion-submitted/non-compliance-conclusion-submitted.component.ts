import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import {
  extractOperatorUsersFromUsersInfo,
  toNonComplianceConclusionSummaryData,
  transformUserContacts,
} from '@requests/common';
import { SummaryComponent, SummaryData } from '@shared/components';

import { NonComplianceConclusionSubmittedRequestActionPayload } from 'cca-api';

@Component({
  selector: 'cca-non-compliance-conclusion-submitted',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceConclusionSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() => {
    const payload = this.actionPayload() as NonComplianceConclusionSubmittedRequestActionPayload;

    const summaryData = toNonComplianceConclusionSummaryData(
      payload.nonComplianceConclusion,
      payload.nonComplianceAttachments,
      false,
      './file-download',
    );

    return this.withRecipients(summaryData, payload);
  });

  private withRecipients(
    summaryData: SummaryData,
    payload: NonComplianceConclusionSubmittedRequestActionPayload,
  ): SummaryData {
    if (payload.nonComplianceConclusion?.details?.penaltyOutcome !== 'WITHDRAW') {
      return summaryData;
    }

    return [
      ...summaryData,
      {
        header: 'Official notice recipients',
        data: [
          {
            key: 'Users notified',
            value: [
              ...transformUserContacts(payload.defaultContacts ?? []),
              ...extractOperatorUsersFromUsersInfo(payload.usersInfo ?? {}, payload.decisionNotification?.operators),
            ],
          },
        ],
      },
    ];
  }
}
