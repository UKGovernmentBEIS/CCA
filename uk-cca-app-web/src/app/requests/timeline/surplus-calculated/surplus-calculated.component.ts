import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload } from 'cca-api';

import { toSurplusCalculatedSummaryData } from './surplus-calculated-summary';

@Component({
  selector: 'cca-surplus-calculated',
  template: `<cca-summary [data]="data" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SurplusCalculatedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload;

  protected readonly data = toSurplusCalculatedSummaryData(this.actionPayload);
}
