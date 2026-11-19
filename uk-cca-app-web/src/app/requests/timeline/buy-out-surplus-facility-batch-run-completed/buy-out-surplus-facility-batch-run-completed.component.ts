import { Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { BuyOutSurplusFacilityRunCompletedRequestActionPayload } from 'cca-api';

import { toBuyOutSurplusFacilityBatchRunCompletedSummaryData } from './buy-out-surplus-facility-batch-run-completed-summary';

@Component({
  selector: 'cca-buy-out-surplus-facility-batch-run-completed',
  template: `<cca-summary [data]="data()" />`,
  imports: [SummaryComponent],
})
export class BuyOutSurplusFacilityBatchRunCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(requestActionQuery.selectActionPayload);

  private readonly actionType = this.requestActionStore.select(requestActionQuery.selectActionType);

  private readonly runId = this.requestActionStore.select(requestActionQuery.selectRequestId);

  protected readonly data = computed(() =>
    toBuyOutSurplusFacilityBatchRunCompletedSummaryData(
      this.actionPayload() as BuyOutSurplusFacilityRunCompletedRequestActionPayload,
      this.actionType() ?? '',
      this.runId() ?? '',
    ),
  );
}
