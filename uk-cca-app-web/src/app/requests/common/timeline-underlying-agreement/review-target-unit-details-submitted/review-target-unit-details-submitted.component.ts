import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';

import { toReviewTargetUnitDetailsUNAReviewSummaryData } from '../../underlying-agreement/summaries';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-una-submitted-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsUNAReviewSummaryData(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      false,
    ),
  );
}
