import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toFacilitySummaryDataWithDecision, underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-facility',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  templateUrl: './facility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  readonly facility = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectFacility(this.facilityId),
  );

  readonly summaryData = computed(() =>
    toFacilitySummaryDataWithDecision(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectFacility(this.facilityId))(),
      this.requestActionStore.select(
        underlyingAgreementVariationReviewedRequestActionQuery.selectFacilitySubtaskDecision(this.facilityId),
      )(),
      {
        submit: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
        review: this.requestActionStore.select(
          underlyingAgreementVariationReviewedRequestActionQuery.selectReviewAttachments,
        )(),
      },
      false,
      '../../../file-download',
    ),
  );
}
