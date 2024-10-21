import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toOverallDecisionSummaryData } from '../../../../tasks/underlying-agreement-review/subtasks/overall-decision/to-overall-decision-summary-data';
import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-overall-decision',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
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
