import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';
import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { BuyOutAndSurplusInfoService } from 'cca-api';

import { BuyoutAndSurplusTabStore } from './buyout-and-surplus-tab.store';

@Component({
  selector: 'cca-buyout-and-surplus-tab',
  templateUrl: './buyout-and-surplus-tab.component.html',
  styles: [
    `
      .opacity-m {
        opacity: 0.5;
      }
    `,
  ],
  standalone: true,
  imports: [
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListComponent,
    SummaryListRowActionsDirective,
    RouterLink,
    DecimalPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuyoutAndSurplusTabComponent {
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);
  private readonly authStore = inject(AuthStore);
  private readonly buyoutAndSurplusTabStore = inject(BuyoutAndSurplusTabStore);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  private readonly roleType = this.authStore.select(selectUserRoleType);

  protected readonly buyOutSurplusInfo = toSignal(
    this.buyOutAndSurplusInfoService.getBuyOutSurplusInfoByAccountId(this.targetUnitId),
  );

  protected readonly userIsRegulator = this.roleType() === 'REGULATOR';

  constructor() {
    effect(
      () => {
        const surplusInfo = this.buyOutSurplusInfo();
        if (surplusInfo) this.buyoutAndSurplusTabStore.setState({ surplusInfo });
      },
      { allowSignalWrites: true },
    );
  }
}
