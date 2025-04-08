import { ChangeDetectionStrategy, Component, computed, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { tap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { ButtonDirective, TabLazyDirective, TabsComponent, WarningTextComponent } from '@netz/govuk-components';

import { SentSubsistenceFeesTabComponent } from './sent-subsistence-fees-tab/sent-subsistence-fees-tab.component';
import { SubsistenceFeesStore } from './subsistence-fees.store';
import { WorkflowHistoryTabComponent } from './workflow-history-tab/workflow-history-tab.component';

@Component({
  selector: 'cca-subsistence-fees',
  templateUrl: './subsistence-fees.component.html',
  standalone: true,
  imports: [
    RouterLink,
    TabsComponent,
    TabLazyDirective,
    PageHeadingComponent,
    ButtonDirective,
    SentSubsistenceFeesTabComponent,
    WorkflowHistoryTabComponent,
    WarningTextComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubsistenceFeesComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly subsistenceFeesStore = inject(SubsistenceFeesStore);

  readonly badgeNumber = computed(() => this.subsistenceFeesStore.stateAsSignal().badgeNumber);
  readonly isValidChargeDate = computed(() => this.subsistenceFeesStore.stateAsSignal().isValidChargeDate);
  readonly isActivePolling = computed(() => this.subsistenceFeesStore.stateAsSignal().runInProgress);

  readonly canInitiateSubsistenceRun = computed(() => this.isValidChargeDate() && !this.isActivePolling());

  /**
   * Initiates the valid date period for the payment requests and the polling mechanism.
   */
  ngOnInit() {
    this.subsistenceFeesStore
      .checkForPendingSubsistenceRun()
      .pipe(
        tap((isInProgress) => {
          if (isInProgress) {
            this.subsistenceFeesStore.updateState({ runInProgress: true, badgeNumber: 1 });
          }
        }),
      )
      .subscribe();
  }

  onNewPaymentRequest() {
    if (!this.canInitiateSubsistenceRun()) return;
    this.router.navigate(['/', 'subsistence-fees', 'new-payment-request']);
  }
}
