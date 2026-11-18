import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toVariationDetailsSummaryDataWithDecision } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-details',
  template: `
    <div>
      <netz-page-heading>Variation details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toVariationDetailsSummaryDataWithDecision({
      variationDetails: this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectVariationDetails,
      )(),
      isEditable: false,
      downloadUrl: '../../file-download',
      decision: this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectSubtaskDecision('VARIATION_DETAILS'),
      )(),
      reviewAttachments: this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments,
      )(),
    }),
  );
}
