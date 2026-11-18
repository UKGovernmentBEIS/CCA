import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementVariationRejectedRequestActionPayload } from 'cca-api';

import { toRejectedDecisionDetailsSummaryData } from '../underlying-agreement-variation-reviewed-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-variation-reviewed-rejected-decision',
  template: ` <cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationReviewedRejectedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toRejectedDecisionDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementVariationRejectedRequestActionPayload,
    ),
  );
}
