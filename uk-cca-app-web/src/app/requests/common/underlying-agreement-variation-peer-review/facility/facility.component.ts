import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';

import { toFacilityWizardSummaryDataWithDecisionAndStatus } from '../../underlying-agreement';
import { isCCA3Scheme } from '../../utils';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-facility',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading>{{ facility.facilityDetails.name }} ({{ facility.facilityId }})</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>
      <netz-return-to-task-or-action-page />
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent, ReturnToTaskOrActionPageComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly route = inject(ActivatedRoute);

  private readonly facilityId = this.route.snapshot.params.facilityId;

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectFacility(this.facilityId))(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  protected readonly summaryData = computed(() => {
    const sectorSchemeData = this.requestTaskStore.select(
      underlyingAgreementVariationPeerReviewQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )();

    return toFacilityWizardSummaryDataWithDecisionAndStatus(
      this.facility(),
      sectorSchemeData,
      this.participatingSchemeVersions(),
      this.requestTaskStore.select(
        underlyingAgreementVariationPeerReviewQuery.selectFacilityDecision(this.facilityId),
      )(),
      {
        submit: this.requestTaskStore.select(
          underlyingAgreementVariationPeerReviewQuery.selectUnderlyingAgreementVariationAttachments,
        )(),
        review: this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectReviewAttachments)(),
      },
      false,
      '../../../file-download',
      { productsLink: './products' },
    );
  });
}
