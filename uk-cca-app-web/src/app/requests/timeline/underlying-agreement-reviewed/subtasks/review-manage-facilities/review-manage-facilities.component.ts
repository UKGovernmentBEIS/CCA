import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';
import { FacilityItemListComponent, underlyingAgreementRequestActionQuery } from '@requests/common';

@Component({
  selector: 'cca-review-manage-facilities',
  template: `
    <div>
      <netz-page-heading>Manage facilities list</netz-page-heading>
      <h2 class="govuk-heading-m">Facilities</h2>
      <cca-facility-item-list [facilityItems]="manageFacilities()" />
    </div>
  `,
  standalone: true,
  imports: [PageHeadingComponent, FacilityItemListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewManageFacilitiesComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly manageFacilities = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectManageFacilities,
  );
}
