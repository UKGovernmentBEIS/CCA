import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { BuyOutSurplusRunCompletedRequestActionPayload } from 'cca-api';

import { toBuyoutSurplusBatchRunCompletedSummaryData } from './buy-out-surplus-batch-run-completed-summary';

@Component({
  selector: 'cca-buy-out-surplus-batch-run-completed',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuyOutSurplusBatchRunCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as BuyOutSurplusRunCompletedRequestActionPayload;

  private readonly actionType = this.requestActionStore.select(requestActionQuery.selectActionType)();

  protected readonly data = toBuyoutSurplusBatchRunCompletedSummaryData(this.actionPayload, this.actionType);
}
