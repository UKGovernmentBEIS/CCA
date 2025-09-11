import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementVariationAcceptedRequestActionPayload } from 'cca-api';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../+state/underlying-agreement-variation-reviewed-request-action.selectors';
import { toAcceptedDecisionDetailsSummaryData } from '../underlying-agreement-variation-reviewed-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-variation-reviewed-accepted-decision',
  template: `<cca-summary [data]="summaryData()" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewedAcceptedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toAcceptedDecisionDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementVariationAcceptedRequestActionPayload,
    ),
  );
}
