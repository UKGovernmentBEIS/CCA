import { Component, inject, ViewEncapsulation } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { GovukDatePipe } from '@netz/common/pipes';
import { AccordionComponent, AccordionItemComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';

import {
  AccountBuyOutSurplusTransactionDTO,
  BuyOutAndSurplusTransactionDetailInfoViewService,
  BuyOutSurplusTransactionDetailHistoryDTO,
} from 'cca-api';

import {
  isAmountChangedPayload,
  toAmountHistorySummaryData,
  toStatusHistorySummaryData,
} from './transaction-history-summary-data';

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
  encapsulation: ViewEncapsulation.None,
  imports: [AccordionComponent, AccordionItemComponent, GovukDatePipe, SummaryComponent],
})
export class TransactionHistoryTabComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly buyOutAndSurplusTransactionDetailInfoViewService = inject(
    BuyOutAndSurplusTransactionDetailInfoViewService,
  );

  private readonly transactionDetails = this.activatedRoute.snapshot.data
    .transactionDetails as AccountBuyOutSurplusTransactionDTO;

  protected readonly transactionHistory = toSignal(
    this.buyOutAndSurplusTransactionDetailInfoViewService.getBuyOutSurplusTransactionDetailHistory(
      this.transactionDetails.detailId,
    ),
  );

  toTransactionHistorySummaryData(th: BuyOutSurplusTransactionDetailHistoryDTO) {
    if (isAmountChangedPayload(th.payload)) return toAmountHistorySummaryData(th.payload);
    return toStatusHistorySummaryData(th.payload);
  }

  toHeader(th: BuyOutSurplusTransactionDetailHistoryDTO) {
    return isAmountChangedPayload(th.payload) ? 'Amount' : 'Status';
  }
}
