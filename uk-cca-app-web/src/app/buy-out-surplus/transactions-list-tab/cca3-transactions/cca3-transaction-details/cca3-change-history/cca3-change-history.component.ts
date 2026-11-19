import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { AccordionComponent, AccordionItemComponent } from '@netz/govuk-components';
import { SummaryComponent, SummaryFactory } from '@shared/components';

import {
  BuyOutAndSurplusTransactionDetailInfoViewService,
  FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO,
  FacilityBuyOutSurplusTransactionDTO,
} from 'cca-api';

import { toCca3ChangeHistorySummaryData } from './cca3-change-history-summary-data';

@Component({
  selector: 'cca-cca3-change-history',
  templateUrl: './cca3-change-history.component.html',
  imports: [PageHeadingComponent, GovukDatePipe, AccordionComponent, AccordionItemComponent, SummaryComponent],
})
export class Cca3ChangeHistoryComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly historyService = inject(BuyOutAndSurplusTransactionDetailInfoViewService);

  protected readonly facility = this.route.snapshot.data
    .facilityDetails as FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
  private readonly transaction = this.route.snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO;

  protected readonly summary = new SummaryFactory()
    .addSection('')
    .addRow('Transaction ID', this.transaction.transactionCode)
    .addRow('Site name', this.facility.siteName || 'Not provided')
    .create();

  protected readonly history = toSignal(
    this.historyService.getBuyOutSurplusTransactionDetailHistory(this.facility.id).pipe(
      map((history) =>
        [...history]
          .sort((a, b) => {
            if (!a.submissionDate) return b.submissionDate ? 1 : 0;
            if (!b.submissionDate) return -1;
            return Date.parse(b.submissionDate) - Date.parse(a.submissionDate);
          })
          .map((entry) => ({
            ...entry,
            header: `${entry.payload?.type === 'AMOUNT_CHANGED' ? 'Amount' : 'Status'} changed by ${
              entry.submitter?.startsWith('batch run ID ')
                ? entry.submitter
                : `“${entry.submitter?.trim() || 'Unknown user'}”`
            }`,
            summary: toCca3ChangeHistorySummaryData(entry.payload, this.facility.id),
          })),
      ),
    ),
  );
}
