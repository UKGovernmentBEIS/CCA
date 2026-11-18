import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { SubsistenceFeesRunCompletedRequestActionPayload } from 'cca-api';

import { toSubsistenceFeesRunCompletedSummaryData } from './subsistence-fees-run-completed-summary-data';

@Component({
  selector: 'cca-subsistence-fees-run-completed',
  template: `<cca-summary [data]="data" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubsistenceFeesRunCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly actionPayload = this.requestActionStore.select(
    requestActionQuery.selectActionPayload,
  )() as SubsistenceFeesRunCompletedRequestActionPayload;

  protected readonly data = toSubsistenceFeesRunCompletedSummaryData(this.actionPayload);
}
