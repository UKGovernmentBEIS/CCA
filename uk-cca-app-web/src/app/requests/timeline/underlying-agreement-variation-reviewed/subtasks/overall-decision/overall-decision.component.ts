import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toOverallDecisionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-overall-decision',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './overall-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  readonly summaryData = computed(() =>
    toOverallDecisionSummaryData(
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectDetermination)(),
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}
