import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementAcceptedRequestActionPayload } from 'cca-api';

import { toAcceptedDecisionDetailsSummaryData } from '../underlying-agreement-reviewed-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-reviewed-accepted-decision',
  standalone: true,
  imports: [SummaryComponent],
  template: `<cca-summary [data]="summaryData()" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  readonly summaryData = computed(() =>
    toAcceptedDecisionDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementAcceptedRequestActionPayload,
    ),
  );
}
