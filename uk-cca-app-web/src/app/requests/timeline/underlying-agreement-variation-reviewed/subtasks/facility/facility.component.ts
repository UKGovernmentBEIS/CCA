import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  isCCA3Scheme,
  toFacilityWizardSummaryDataWithDecisionAndStatus,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { underlyingAgreementVariationReviewedRequestActionQuery } from '../../+state/underlying-agreement-variation-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-variation-review-facility',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading>{{ facility.facilityDetails.name }} ({{ facility.facilityId }})</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectFacility(this.facilityId),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.requestActionStore.select(
      underlyingAgreementRequestActionQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )(),
  );

  protected readonly summaryData = computed(() =>
    toFacilityWizardSummaryDataWithDecisionAndStatus(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
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
      { productsLink: './products' },
    ),
  );
}
