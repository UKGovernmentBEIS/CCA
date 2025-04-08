import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TagComponent,
} from '@netz/govuk-components';
import { FacilityStatusPipe } from '@shared/pipes';

import { FacilityDataDetailsDTO } from 'cca-api';

@Component({
  selector: 'cca-facility-details',
  templateUrl: './facility-details.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    TagComponent,
    FacilityStatusPipe,
    DatePipe,
    RouterLink,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class FacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);

  readonly roleType = this.authStore.select(selectUserRoleType);

  readonly queryParams: Params = { change: true };
  readonly facilityDetails = this.activatedRoute.snapshot.data.facilityDetails as FacilityDataDetailsDTO;
}
