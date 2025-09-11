import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  isCCA3Scheme,
  toFacilityWizardSummaryDataWithDecision,
  underlyingAgreementQuery,
  underlyingAgreementReviewQuery,
} from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>
    }

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" routerLink="../..">Return to: Manage facilities</a>
  `,
  standalone: true,
  imports: [SummaryComponent, PageHeadingComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;

  private readonly decision = this.requestTaskStore.select(
    underlyingAgreementReviewQuery.selectFacilitySubtaskDecision(this.facilityId),
  )();

  protected readonly facility = computed(() =>
    this.requestTaskStore.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly downloadUrl = generateDownloadUrl(
    this.requestTaskStore.select(requestTaskQuery.selectRequestTaskId)().toString(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.requestTaskStore.select(
      underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()),
    )(),
  );

  protected readonly summaryData = computed(() =>
    toFacilityWizardSummaryDataWithDecision(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
      this.decision,
      {
        submit: this.requestTaskStore.select(underlyingAgreementQuery.selectAttachments)(),
        review: this.requestTaskStore.select(underlyingAgreementReviewQuery.selectReviewAttachments)(),
      },
      this.requestTaskStore.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
    ),
  );
}
