import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { SummaryComponent } from '@shared/components';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

import { toTransactionDetailsSummaryData } from './transaction-details-summary-data';

@Component({
  selector: 'cca-transaction-details-tab',
  template: `<cca-summary [data]="summaryData" />`,
  standalone: true,
  imports: [SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionDetailsTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly summaryData = toTransactionDetailsSummaryData(
    this.activatedRoute.snapshot.data.transactionDetails as BuyOutSurplusTransactionDetailsDTO,
  );
}
