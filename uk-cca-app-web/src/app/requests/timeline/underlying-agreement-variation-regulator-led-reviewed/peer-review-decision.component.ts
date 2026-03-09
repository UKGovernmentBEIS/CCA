import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { unaVariationRegulatorLedReviewedQuery } from './+state/underlying-agreement-variation-regulator-led-reviewed.selectors';
import { toDecisionDetailsSummaryData } from './peer-review-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-regulator-led-variation-reviewed-decision',
  template: `<cca-summary [data]="summaryData()" />`,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnARegulatorLedVariationReviewedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toDecisionDetailsSummaryData(
      this.requestActionStore.select(unaVariationRegulatorLedReviewedQuery.selectPayload)(),
      this.requestActionStore.select(requestActionQuery.selectSubmitter)(),
    ),
  );
}
