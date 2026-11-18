import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  toReviewTargetUnitDetailsSummaryDataWithDecision,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-review-target-unit-details',
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
    toReviewTargetUnitDetailsSummaryDataWithDecision({
      targetUnitDetails: this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      decision: this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
      )(),
      countries: this.countries(),
      attachments: this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectReviewAttachments,
      )(),
      downloadUrl: '../../file-download',
      isEditable: false,
    }),
  );
}
