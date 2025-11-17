import { ChangeDetectionStrategy, Component, inject, ViewEncapsulation } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { GovukDatePipe } from '@netz/common/pipes';
import { AccordionComponent, AccordionItemComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import { BuyOutAndSurplusTransactionInfoViewService, BuyOutSurplusTransactionHistoryDTO } from 'cca-api';

import { toAmountHistorySummaryData, toStatusHistorySummaryData } from './transaction-history-summary-data';

@Component({
  selector: 'cca-transaction-history-tab',
  template: `
    @if (transactionHistory()?.length === 0) {
      <p>There is no change history yet.</p>
      <p>More information will be available when changes to the amount or status have been made.</p>
    } @else {
      <govuk-accordion>
        @for (history of transactionHistory(); track history.id) {
          <govuk-accordion-item
            [id]="history.id"
            header="{{ toHeader(history) }} changed by &ldquo;{{ history.submitter }}&rdquo;"
            [caption]="history.submissionDate | govukDate: 'datetime'"
          >
            <cca-summary [data]="toTransactionHistorySummaryData(history)" />
          </govuk-accordion-item>
        }
      </govuk-accordion>
    }
  `,
  imports: [AccordionComponent, AccordionItemComponent, GovukDatePipe, SummaryComponent],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionHistoryTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly buyOutAndSurplusTransactionInfoViewService = inject(BuyOutAndSurplusTransactionInfoViewService);

  private readonly transactionId = +this.activatedRoute.snapshot.paramMap.get('transactionId');

  protected readonly transactionHistory = toSignal(
    this.buyOutAndSurplusTransactionInfoViewService.getBuyOutSurplusTransactionHistory(this.transactionId),
  );

  toTransactionHistorySummaryData(th: BuyOutSurplusTransactionHistoryDTO) {
    if (th.payload.type === 'AMOUNT_CHANGED') return toAmountHistorySummaryData(th);
    return toStatusHistorySummaryData(th);
  }

  toHeader(th: BuyOutSurplusTransactionHistoryDTO) {
    return th.payload.type === 'AMOUNT_CHANGED' ? 'Amount' : 'Status';
  }
}
