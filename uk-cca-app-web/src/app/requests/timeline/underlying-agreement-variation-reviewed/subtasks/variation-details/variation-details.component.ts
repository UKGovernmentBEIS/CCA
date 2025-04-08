import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toVariationDetailsSummaryDataWithDecision } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-details',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './variation-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  readonly summaryData = computed(() =>
    toVariationDetailsSummaryDataWithDecision(
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectVariationDetails)(),
      false,
      '../../file-download',
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectSubtaskDecision('VARIATION_DETAILS'),
      )(),
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments)(),
    ),
  );
}
