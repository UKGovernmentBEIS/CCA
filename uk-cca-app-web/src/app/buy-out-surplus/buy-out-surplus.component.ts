import { ChangeDetectionStrategy, Component, computed, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { tap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent, WarningTextComponent } from '@netz/govuk-components';

import { BuyoutSurplusStore } from './buy-out-surplus.store';
import { TransactionsFiltersComponent } from './transactions-list-tab/transactions-filters/transactions-filters.component';
import { TransactionsTableComponent } from './transactions-list-tab/transactions-table/transactions-table.component';
import { WorkflowHistoryTabComponent } from './workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-buy-out-surplus',
  templateUrl: './buy-out-surplus.component.html',
  imports: [
    RouterLink,
    TabsComponent,
    TabLazyDirective,
    PageHeadingComponent,
    ButtonDirective,
    WarningTextComponent,
    WorkflowHistoryTabComponent,
    TransactionsFiltersComponent,
    TransactionsTableComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BuyOutSurplusComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly buyoutSurplusStore = inject(BuyoutSurplusStore);

  protected readonly badgeNumber = computed(() => this.buyoutSurplusStore.stateAsSignal().badgeNumber);
  protected readonly isActivePolling = computed(() => this.buyoutSurplusStore.stateAsSignal().runInProgress);

  constructor() {
    this.route.fragment.pipe(takeUntilDestroyed()).subscribe((fragment) => {
      const paymentStatus = this.route.snapshot.queryParamMap.get('buyOutSurplusPaymentStatus');
      if (fragment === 'transactions' && !paymentStatus) {
        this.router.navigate([], {
          relativeTo: this.route,
          fragment,
          queryParams: { buyOutSurplusPaymentStatus: 'AWAITING_PAYMENT' },
        });
      }
    });
  }

  /**
   * Initiates the valid date period for the payment requests and the polling mechanism.
   */
  ngOnInit() {
    this.buyoutSurplusStore
      .checkForPendingBatchRun()
      .pipe(
        tap((isInProgress) => {
          if (isInProgress) this.buyoutSurplusStore.updateState({ runInProgress: true, badgeNumber: 1 });
        }),
      )
      .subscribe();
  }

  onNewBatchRequest() {
    if (this.isActivePolling()) return;
    this.router.navigate(['/', 'buyout-surplus', 'new-batch']);
  }
}
