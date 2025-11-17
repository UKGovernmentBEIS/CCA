import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent } from '@netz/govuk-components';
import { StatusPipe } from '@shared/pipes';
import { StatusColorPipe } from '@shared/pipes';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

import { TransactionHistoryTabComponent } from './transaction-history-tab/transaction-history-tab.component';
import { TransactionDetailsTabComponent } from './transcation-details-tab/transaction-details-tab.component';

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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionDetailsContainerComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly transactionSummary = this.activatedRoute.snapshot.data
    .transactionDetails as BuyOutSurplusTransactionDetailsDTO;
}
