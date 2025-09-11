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
import { StatusPipe } from '@shared/pipes';
import { DurationPipe } from '@shared/pipes';

import { FacilityInfoDTO } from 'cca-api';

@Component({
  selector: 'cca-facility-details',
  templateUrl: './facility-details.component.html',
  standalone: true,
  imports: [
    PageHeadingComponent,
    TagComponent,
    StatusPipe,
    RouterLink,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
    DurationPipe,
    DatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FacilityDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly authStore = inject(AuthStore);

  protected readonly roleType = this.authStore.select(selectUserRoleType);
  protected readonly queryParams: Params = { change: true };
  protected readonly facilityInfoDTO = this.activatedRoute.snapshot.data.facilityDetails as FacilityInfoDTO;
}
