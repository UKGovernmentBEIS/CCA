import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { NonComplianceAppealDetailsSubmittedRequestActionPayload } from 'cca-api';

import { toAppealDetailsSummaryData } from '../../tasks/non-compliance-conclusion/provide-appeal-details/to-appeal-details-summary-data';

@Component({
  selector: 'cca-non-compliance-appeal-provided',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceAppealProvidedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() => {
    const payload = this.actionPayload() as NonComplianceAppealDetailsSubmittedRequestActionPayload;

    return toAppealDetailsSummaryData(
      {
        registrationDate: payload.registrationDate,
        files: payload.files,
        comments: payload.comments,
      },
      payload.nonComplianceAttachments ?? {},
      false,
      './file-download',
    );
  });
}
