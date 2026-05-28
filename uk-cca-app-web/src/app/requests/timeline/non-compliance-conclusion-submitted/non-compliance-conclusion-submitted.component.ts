import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { toNonComplianceConclusionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

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

    return toNonComplianceConclusionSummaryData(
      payload.nonComplianceConclusion,
      payload.nonComplianceAttachments,
      false,
      './file-download',
    );
  });
}
