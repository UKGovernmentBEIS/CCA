import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { toBuyoutFeeCalculatedSummaryData } from './buy-out-fee-calculated-summary';

@Component({
  selector: 'cca-buy-out-fee-calculated',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuyOutFeeCalculatedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as TP6BuyOutCalculatedAccountProcessingSubmittedRequestActionPayload;

  protected readonly data = toBuyoutFeeCalculatedSummaryData(this.actionPayload);
}
