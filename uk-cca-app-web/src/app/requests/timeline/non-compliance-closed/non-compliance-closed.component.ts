import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { NonComplianceClosedRequestActionPayload } from 'cca-api';

import { toNonComplianceClosedSummaryData } from './non-compliance-closed-summary-data';

@Component({
  selector: 'cca-non-compliance-closed',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonComplianceClosedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  protected readonly data = computed(() =>
    toNonComplianceClosedSummaryData(
      this.actionPayload() as NonComplianceClosedRequestActionPayload,
      './file-download',
    ),
  );
}
