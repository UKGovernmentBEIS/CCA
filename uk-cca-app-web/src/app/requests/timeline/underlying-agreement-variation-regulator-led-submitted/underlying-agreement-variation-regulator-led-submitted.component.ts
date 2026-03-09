import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload } from 'cca-api';

import { toUnARegulatorLedVariationSubmittedSummaryData } from './underlying-agreement-variation-regulator-led-submitted-summary-data';

@Component({
  selector: 'cca-underlying-agreement-variation-regulator-led-actions-submitted',
  template: `<cca-summary [data]="summaryData()" /> `,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnARegulatorLedVariationSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toUnARegulatorLedVariationSubmittedSummaryData(
      this.requestActionStore.select(
        requestActionQuery.selectActionPayload,
      )() as UnderlyingAgreementVariationRegulatorLedCompletedRequestActionPayload,
    ),
  );
}
