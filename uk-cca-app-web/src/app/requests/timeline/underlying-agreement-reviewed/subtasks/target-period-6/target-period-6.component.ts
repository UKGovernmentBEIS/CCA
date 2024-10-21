import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toBaselineAndTargetsSummaryDataWithDecision, underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-target-period-6',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './target-period-6.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TargetPeriod6Component {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly summaryMetadata = {
    isTp5Period: false,
    baselineExists: null,
    downloadUrl: '../../file-download',
    isEditable: false,
    attachments: {
      submit: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      review: this.requestActionStore.select(underlyingAgreementReviewedRequestActionQuery.selectReviewAttachments)(),
    },
  };
  readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryDataWithDecision(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAccountReferenceDataSectorAssociationDetails,
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod6Details)(),
      this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
      )(),
      this.summaryMetadata,
    ),
  );
}
