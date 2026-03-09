import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestTaskStore } from '@netz/common/store';

import { FacilityItemListComponent } from '../../underlying-agreement';
import { unaRegulatorLedVariationPeerReviewQuery } from '../underlying-agreement-variation--regulator-led-peer-review.selectors';

@Component({
  selector: 'cca-una-variation-regulator-led-peer-review-manage-facilities',
  template: `
    <div>
      <netz-page-heading>Manage facilities</netz-page-heading>
      <cca-facility-item-list [facilityItems]="facilities()" />
    </div>
  `,
  imports: [PageHeadingComponent, FacilityItemListComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorLedPeerReviewManageFacilitiesComponent {
  private readonly requestTaskStore = inject(RequestTaskStore);

  protected readonly facilities = this.requestTaskStore.select(
    unaRegulatorLedVariationPeerReviewQuery.selectManageFacilities,
  );
}
