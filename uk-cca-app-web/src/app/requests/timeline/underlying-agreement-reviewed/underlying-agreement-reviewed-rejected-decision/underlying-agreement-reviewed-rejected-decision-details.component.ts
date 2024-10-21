import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { RequestActionStore } from '@netz/common/store';
import { underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { UnderlyingAgreementRejectedRequestActionPayload } from 'cca-api';

import { toRejectedDecisionDetailsSummaryData } from '../underlying-agreement-reviewed-decision-summary-data';

@Component({
  selector: 'cca-underlying-agreement-reviewed-rejected-decision',
  standalone: true,
  imports: [SummaryComponent],
  template: ` <cca-summary [data]="summaryData()" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UnderlyingAgreementReviewedRejectedDecisionDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  readonly summaryData = computed(() =>
    toRejectedDecisionDetailsSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectPayload,
      )() as UnderlyingAgreementRejectedRequestActionPayload,
    ),
  );
}
