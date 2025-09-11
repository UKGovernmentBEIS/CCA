import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementAcceptedRequestActionPayload } from 'cca-api';

import { underlyingAgreementReviewedRequestActionQuery } from '../+state/underlying-agreement-reviewed-request-action.selectors';
import { toAcceptedDecisionDetailsSummaryData } from '../underlying-agreement-reviewed-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-reviewed-accepted-decision',
  template: `<cca-summary [data]="summaryData()" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewedAcceptedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toAcceptedDecisionDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementAcceptedRequestActionPayload,
    ),
  );
}
