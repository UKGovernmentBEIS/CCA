import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementVariationCompletedRequestActionPayload } from 'cca-api';

import { underlyingAgreementVariationCompletedRequestActionQuery } from './+state/underlying-agreement-variation-completed-request-action.selectors';
import { toUnderlyingAgreementVariationCompletedSummaryData } from './underlying-agreement-variation-completed-summary-data';

@Component({
  selector: 'cca-underlying-agreement-variation-completed',
  template: `<cca-summary [data]="summaryData()" /> `,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementVariationCompletedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toUnderlyingAgreementVariationCompletedSummaryData(
      this.requestActionStore.select(
        underlyingAgreementVariationCompletedRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementVariationCompletedRequestActionPayload,
    ),
  );
}
