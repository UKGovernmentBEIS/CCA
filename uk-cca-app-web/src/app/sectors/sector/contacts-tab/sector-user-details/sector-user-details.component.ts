import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';

import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { TwoFaLinkComponent } from '@shared/components';
import { ContactTypePipe, PhoneNumberPipe } from '@shared/pipes';

import { ActiveSectorUserStore } from '../active-sector-user.store';

@Component({
  selector: 'cca-sector-user-details',
  templateUrl: './sector-user-details.component.html',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PageHeadingComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TwoFaLinkComponent,
    ContactTypePipe,
    PhoneNumberPipe,
    RouterLink,
  ],
})
export class SectorUserDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(ActiveSectorUserStore);
  private readonly currentUserId = inject(AuthStore).select(selectUserId);

  readonly queryParams: Params = { change: true };
  readonly sectorUserId = this.activatedRoute.snapshot.paramMap.get('sectorUserId');
  readonly sectorAssociationId = this.activatedRoute.snapshot.paramMap.get('sectorId');

  readonly isCurrentUser = this.currentUserId() === this.sectorUserId;
  readonly isEditable = this.store.state.editable;

  readonly details = this.store.state.details;
  readonly changeLink = this.currentUserId() === this.sectorUserId || this.store.state.editable;
  readonly changeContactType = this.store.state.editable;

  userFullName = `${this.details.firstName} ${this.details.lastName}`;
}
