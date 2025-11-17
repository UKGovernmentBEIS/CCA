import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toVariationDetailsSummaryData } from '../../underlying-agreement/summaries';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-una-submitted-variation-details',
  template: `
    <div>
      <netz-page-heading>Variation details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [SummaryComponent, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toVariationDetailsSummaryData(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectVariationDetails)(),
      false,
    ),
  );
}
