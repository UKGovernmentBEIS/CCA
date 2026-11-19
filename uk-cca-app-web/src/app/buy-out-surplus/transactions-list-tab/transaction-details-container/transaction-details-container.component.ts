import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { StatusPipe } from '@shared/pipes';
import { StatusColorPipe } from '@shared/pipes';

import { AccountBuyOutSurplusTransactionDTO } from 'cca-api';

import { TransactionDetailsTabComponent } from './transaction-details-tab/transaction-details-tab.component';
import { TransactionHistoryTabComponent } from './transaction-history-tab/transaction-history-tab.component';

@Component({
  selector: 'cca-transaction-details-container',
  templateUrl: './transaction-details-container.component.html',
  imports: [
    TabLazyDirective,
    TabsComponent,
    TransactionDetailsTabComponent,
    PageHeadingComponent,
    StatusPipe,
    StatusColorPipe,
    PageHeadingComponent,
    TransactionHistoryTabComponent,
  ],
})
export class TransactionDetailsContainerComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly transactionSummary = this.activatedRoute.snapshot.data
    .transactionDetails as AccountBuyOutSurplusTransactionDTO;
}
