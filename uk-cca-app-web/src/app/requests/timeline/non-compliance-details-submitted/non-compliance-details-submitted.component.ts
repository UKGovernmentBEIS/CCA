import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { NonComplianceDetailsSubmittedRequestActionPayload } from 'cca-api';

import { toNonComplianceDetailsSubmittedSummaryData } from './non-compliance-details-submitted-summary';

@Component({
  selector: 'cca-non-compliance-details-submitted',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceDetailsSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as NonComplianceDetailsSubmittedRequestActionPayload;

  protected readonly data = toNonComplianceDetailsSubmittedSummaryData(this.actionPayload.nonComplianceDetails);
}
