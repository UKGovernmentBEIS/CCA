import { AsyncPipe, Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, map, switchMap, take } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';

import { OperatorUsersService, RegulatorUsersService, SectorUsersService } from 'cca-api';

@Component({
  selector: 'cca-reset-two-fa',
  templateUrl: './reset-two-fa.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent, PendingButtonDirective, ButtonDirective, RouterLink, AsyncPipe],
})
export class ResetTwoFaComponent implements OnInit {
  private readonly regulatorUsersService = inject(RegulatorUsersService);
  private readonly operatorUsersService = inject(OperatorUsersService);
  private readonly sectorUsersService = inject(SectorUsersService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly location = inject(Location);

  userId$ = this.activatedRoute.paramMap.pipe(map(() => window.history.state['userId']));
  accountId$ = this.activatedRoute.paramMap.pipe(map(() => window.history.state['accountId']));
  sectorAssociationId$ = this.activatedRoute.paramMap.pipe(map(() => window.history.state['sectorAssociationId']));
  userName$ = this.activatedRoute.paramMap.pipe(map(() => window.history.state['userName']));
  role$ = this.activatedRoute.paramMap.pipe(map(() => window.history.state['role']));

  ngOnInit(): void {
    combineLatest([this.userId$, this.role$, this.userName$])
      .pipe(take(1))
      .subscribe(([userId, role, userName]) => {
        if (userId === undefined || role === undefined || userName === undefined) {
          this.router.navigate(['/landing'], { replaceUrl: true });
        }
      });
  }

  reset() {
    combineLatest([this.userId$, this.accountId$, this.role$, this.sectorAssociationId$])
      .pipe(
        take(1),
        switchMap(([userId, accountId, role, sectorAssociationId]) => {
          switch (role) {
            case 'REGULATOR':
              return this.regulatorUsersService.resetRegulator2Fa(userId);

            case 'OPERATOR':
              return this.operatorUsersService.resetOperator2Fa(accountId, userId);

            case 'SECTOR_USER':
              return this.sectorUsersService.resetSectorUser2Fa(sectorAssociationId, userId);
          }
        }),
      )
      .subscribe(() => this.location.back());
  }
}
