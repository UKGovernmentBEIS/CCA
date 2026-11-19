import { Location } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Observable } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';

import { OperatorUsersService, RegulatorUsersService, SectorUsersService } from 'cca-api';

/** Navigation state passed by the user details pages that link here. */
type ResetTwoFaState = {
  userId?: string;
  accountId?: number;
  sectorAssociationId?: number;
  userName?: string;
  role?: 'REGULATOR' | 'OPERATOR' | 'SECTOR_USER';
};

@Component({
  selector: 'cca-reset-two-fa',
  templateUrl: './reset-two-fa.component.html',
  imports: [PageHeadingComponent, PendingButtonDirective, ButtonDirective, RouterLink],
})
export class ResetTwoFaComponent implements OnInit {
  private readonly regulatorUsersService = inject(RegulatorUsersService);
  private readonly operatorUsersService = inject(OperatorUsersService);
  private readonly sectorUsersService = inject(SectorUsersService);
  private readonly router = inject(Router);
  protected readonly location = inject(Location);

  // The router writes the navigation state to history before it activates this component.
  private readonly state: ResetTwoFaState = window.history.state ?? {};
  protected readonly userName = this.state.userName;

  ngOnInit() {
    const { userId, role, userName } = this.state;
    if (userId === undefined || role === undefined || userName === undefined) {
      this.router.navigate(['/landing'], { replaceUrl: true });
    }
  }

  reset() {
    this.resetRequest().subscribe(() => this.location.back());
  }

  private resetRequest(): Observable<unknown> {
    const { userId, accountId, role, sectorAssociationId } = this.state;
    switch (role) {
      case 'REGULATOR':
        return this.regulatorUsersService.resetRegulator2Fa(userId);

      case 'OPERATOR':
        return this.operatorUsersService.resetOperator2Fa(accountId, userId);

      case 'SECTOR_USER':
        return this.sectorUsersService.resetSectorUser2Fa(sectorAssociationId, userId);
    }
  }
}
