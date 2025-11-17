import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { isCCA3Scheme, toFacilityWizardSummaryData, underlyingAgreementQuery } from '@requests/common';
import { SummaryComponent } from '@shared/components';
import { SchemeVersion } from '@shared/types';
import { generateDownloadUrl } from '@shared/utils';

@Component({
  selector: 'cca-facility-summary',
  template: `
    @if (facility(); as facility) {
      <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
      <cca-summary [data]="summaryData()" />
    }

    <hr class="govuk-footer__section-break govuk-!-margin-bottom-3" />
    <a class="govuk-link" [routerLink]="['../../']"> Return to: Manage facilities </a>
  `,
  imports: [SummaryComponent, PageHeadingComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilitySummaryComponent {
  private readonly store = inject(RequestTaskStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly taskId = this.activatedRoute.snapshot.params.taskId;
  private readonly facilityId = this.activatedRoute.snapshot.params.facilityId;
  private readonly downloadUrl = generateDownloadUrl(this.taskId);

  protected readonly facility = computed(() =>
    this.store.select(underlyingAgreementQuery.selectFacility(this.facilityId))(),
  );

  private readonly participatingSchemeVersions = computed(
    () => this.facility()?.facilityDetails?.participatingSchemeVersions,
  );

  private readonly schemeVersion = computed(() =>
    isCCA3Scheme(this.participatingSchemeVersions()) ? SchemeVersion.CCA_3 : SchemeVersion.CCA_2,
  );

  private readonly sectorSchemeData = computed(() =>
    this.store.select(underlyingAgreementQuery.selectSectorAssociationDetailsSchemeData(this.schemeVersion()))(),
  );

  protected readonly summaryData = computed(() =>
    toFacilityWizardSummaryData(
      this.facility(),
      this.sectorSchemeData(),
      this.participatingSchemeVersions(),
      this.store.select(underlyingAgreementQuery.selectAttachments)(),
      this.store.select(requestTaskQuery.selectIsEditable)(),
      this.downloadUrl,
      { changeName: true },
    ),
  );
}
