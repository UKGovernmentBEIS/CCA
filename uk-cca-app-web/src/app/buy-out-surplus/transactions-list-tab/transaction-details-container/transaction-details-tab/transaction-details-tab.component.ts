import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SummaryComponent } from '@shared/components';

import { AccountBuyOutSurplusTransactionDTO } from 'cca-api';

import { toTransactionDetailsSummaryData } from './transaction-details-summary-data';

@Component({
  selector: 'cca-transaction-details-tab',
  template: `<cca-summary [data]="summaryData" />`,
  imports: [SummaryComponent],
})
export class TransactionDetailsTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly summaryData = toTransactionDetailsSummaryData(
    this.activatedRoute.snapshot.data.transactionDetails as AccountBuyOutSurplusTransactionDTO,
  );
}
