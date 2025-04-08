import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toBaselineAndTargetsSummaryDataWithDecision, underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-target-period-6',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
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
      review: this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments,
      )(),
    },
  };

  readonly summaryData = computed(() =>
    toBaselineAndTargetsSummaryDataWithDecision(
      this.requestActionStore.select(
        underlyingAgreementRequestActionQuery.selectAccountReferenceDataSectorAssociationDetails,
      )(),
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectTargetPeriod6Details)(),
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectSubtaskDecision('TARGET_PERIOD6_DETAILS'),
      )(),
      this.summaryMetadata,
    ),
  );
}
