import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { RequestActionStore } from '@netz/common/store';

import { FacilityItemListComponent } from '../../../underlying-agreement';
import { underlyingAgreementRequestActionQuery } from '../../+state';

@Component({
  selector: 'cca-una-submitted-manage-facilities',
  standalone: true,
  imports: [PageHeadingComponent, FacilityItemListComponent],
  templateUrl: './manage-facilities-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManageFacilitiesSubmittedComponent {
  private readonly requestActionStore = inject(RequestActionStore);

  protected readonly manageFacilities = this.requestActionStore.select(
    underlyingAgreementRequestActionQuery.selectManageFacilities,
  );
}
