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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectorUserDetailsComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly store = inject(ActiveSectorUserStore);
  private readonly currentUserId = inject(AuthStore).select(selectUserId);

  protected readonly queryParams: Params = { change: true };
  protected readonly sectorUserId = this.activatedRoute.snapshot.paramMap.get('sectorUserId');
  protected readonly sectorAssociationId = this.activatedRoute.snapshot.paramMap.get('sectorId');

  protected readonly isCurrentUser = this.currentUserId() === this.sectorUserId;
  protected readonly isEditable = this.store.state.editable;

  protected readonly details = this.store.state.details;
  protected readonly changeLink = this.currentUserId() === this.sectorUserId || this.store.state.editable;
  protected readonly changeContactType = this.store.state.editable;

  protected readonly userFullName = `${this.details.firstName} ${this.details.lastName}`;
}
