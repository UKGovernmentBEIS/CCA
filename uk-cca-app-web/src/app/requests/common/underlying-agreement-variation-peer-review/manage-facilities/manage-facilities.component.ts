import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';

import { FacilityItemListComponent } from '../../underlying-agreement';
import { underlyingAgreementVariationPeerReviewQuery } from '../underlying-agreement-variation-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-peer-review-manage-facilities',
  template: `
    <div>
      <netz-page-heading>Manage facilities</netz-page-heading>
      <cca-facility-item-list [facilityItems]="facilities()" baseUrl="../facility" />
    </div>
  `,
  imports: [PageHeadingComponent, FacilityItemListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly facilities = computed(() =>
    this.requestTaskStore.select(underlyingAgreementVariationPeerReviewQuery.selectManageFacilities)(),
  );
}
