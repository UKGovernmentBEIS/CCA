import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  toReviewTargetUnitDetailsSummaryDataWithDecision,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-review-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  private readonly countries = inject(CountryService).countries;

  protected readonly summaryData = computed(() =>
    toReviewTargetUnitDetailsSummaryDataWithDecision(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
      )(),
      this.countries(),
      this.requestActionStore.select(underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments)(),
      '../../file-download',
      false,
    ),
  );
}
