import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toFacilityWizardSummaryDataWithDecision } from '../../underlying-agreement';
import { isCCA3Scheme } from '../../utils';
import { underlyingAgreementPeerReviewQuery } from '../underlying-agreement-peer-review.selectors';

@Component({
  selector: 'cca-una-peer-review-facility',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>

      <a routerLink="../../../" class="govuk-link">Return to: Peer review application for underlying agreement</a>
    }
  `,
  standalone: true,
  imports: [PageHeadingComponent, SummaryComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementPeerReviewQuery.selectFacility(this.facilityId))(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.store.select(
      underlyingAgreementPeerReviewQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )(),
  );

  protected readonly summaryData = computed(() =>
    toFacilityWizardSummaryDataWithDecision(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
      this.store.select(underlyingAgreementPeerReviewQuery.selectFacilityDecision(this.facilityId))(),
      {
        submit: this.store.select(underlyingAgreementPeerReviewQuery.selectUnderlyingAgreementAttachments)(),
        review: this.store.select(underlyingAgreementPeerReviewQuery.selectReviewAttachments)(),
      },
      false,
      '../../../file-download',
    ),
  );
}
