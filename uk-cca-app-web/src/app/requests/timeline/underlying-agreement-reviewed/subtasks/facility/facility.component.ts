import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { toFacilitySummaryDataWithDecision, underlyingAgreementRequestActionQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-facility',
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  templateUrl: './facility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  readonly facility = computed(() =>
    this.requestActionStore
      .select(underlyingAgreementRequestActionQuery.selectManageFacilities)()
      .facilityItems.find((f) => f.facilityId === this.facilityId),
  );
  readonly summaryData = computed(() =>
    toFacilitySummaryDataWithDecision(
      this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectFacility(this.facilityId))(),
      this.requestActionStore.select(
        underlyingAgreementReviewedRequestActionQuery.selectFacilitySubtaskDecision(this.facilityId),
      )(),
      {
        submit: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
        review: this.requestActionStore.select(underlyingAgreementReviewedRequestActionQuery.selectReviewAttachments)(),
      },
      false,
      '../../../file-download',
    ),
  );
}
