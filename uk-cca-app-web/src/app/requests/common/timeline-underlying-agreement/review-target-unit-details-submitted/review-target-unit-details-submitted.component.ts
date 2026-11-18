import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';

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
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly countries = inject(CountryService).countries;

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsUNAReviewSummaryData({
      targetUnitDetails: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      countries: this.countries(),
      isEditable: false,
    }),
  );
}
