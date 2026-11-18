import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';

import { FacilityItemListComponent } from '../../underlying-agreement';
import { underlyingAgreementPeerReviewQuery } from '../underlying-agreement-peer-review.selectors';

@Component({
  selector: 'cca-una-peer-review-manage-facilities',
  template: `
    <div>
      <netz-page-heading>Manage facilities list</netz-page-heading>
      <h2 class="govuk-heading-m">Facilities</h2>
      <cca-facility-item-list [facilityItems]="manageFacilities()" />
    </div>
  `,
  imports: [PageHeadingComponent, FacilityItemListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesComponent {
  private readonly store = inject(RequestTaskStore);

  protected readonly manageFacilities = this.store.select(underlyingAgreementPeerReviewQuery.selectManageFacilities);
}
