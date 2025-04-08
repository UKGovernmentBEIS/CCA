import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toOverallDecisionSummaryData } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-overall-decision',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './overall-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  readonly summaryData = computed(() =>
    toOverallDecisionSummaryData(
      this.requestActionStore.select(underlyingAgreementReviewedRequestActionQuery.selectDetermination)(),
      this.requestActionStore.select(underlyingAgreementReviewedRequestActionQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}
