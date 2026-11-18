import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { SummaryComponent } from '@shared/components';
import { CountryService } from '@shared/services';
import { SchemeVersion } from '@shared/types';

import { toFacilitySummaryDataWithStatus } from '../../underlying-agreement/summaries/facility-summary-data';
import { isCCA3Scheme } from '../../utils';
import { underlyingAgreementRequestActionQuery } from '../timeline-underlying-agreement.selectors';

@Component({
  selector: 'cca-una-submitted-facility',
  template: `
    @if (facility(); as facility) {
      <div>
        <netz-page-heading [caption]="facility.facilityDetails.name">Summary</netz-page-heading>
        <cca-summary [data]="summaryData()" />
      </div>
    }
  `,
  imports: [PageHeadingComponent, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilitySubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly countries = inject(CountryService).countries;

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
    toFacilitySummaryDataWithStatus({
      facility: this.facility(),
      sectorSchemeData: this.sectorSchemeData(),
      schemeVersions: this.participatingSchemeVersions(),
      countries: this.countries(),
      attachments: this.requestActionStore.select(underlyingAgreementRequestActionQuery.selectAttachments)(),
      isEditable: false,
      downloadUrl: '../../../file-download',
      opts: { productsLink: './products' },
    }),
  );
}
