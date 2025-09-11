import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import {
  isCCA3Scheme,
  toFacilityWizardSummaryDataWithDecision,
  underlyingAgreementRequestActionQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { underlyingAgreementReviewedRequestActionQuery } from '../../+state/underlying-agreement-reviewed-request-action.selectors';

@Component({
  selector: 'cca-timeline-review-facility',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>
    }
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectFacility(this.facilityId))(),
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
    toFacilityWizardSummaryDataWithDecision(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
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
