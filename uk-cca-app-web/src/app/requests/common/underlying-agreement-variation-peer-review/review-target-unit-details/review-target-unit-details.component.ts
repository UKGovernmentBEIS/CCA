import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';

import { toVariationReviewTargetUnitDetailsSummaryDataWithDecision } from '../../underlying-agreement/summaries';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-target-unit-details',
  template: `
    <div>
      <netz-page-heading>Target unit details</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    </div>
    <netz-return-to-task-or-action-page />
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewTargetUnitDetailsComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  private readonly countries = inject(CountryService).countries;

  protected readonly summaryData = computed(() =>
    toVariationReviewTargetUnitDetailsSummaryDataWithDecision({
      targetUnitDetails: this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementTargetUnitDetails,
      )(),
      decision: this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectSubtaskDecision('TARGET_UNIT_DETAILS'),
      )(),
      countries: this.countries(),
      attachments: this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
      downloadUrl: '../../file-download',
      isEditable: false,
    }),
  );
}
