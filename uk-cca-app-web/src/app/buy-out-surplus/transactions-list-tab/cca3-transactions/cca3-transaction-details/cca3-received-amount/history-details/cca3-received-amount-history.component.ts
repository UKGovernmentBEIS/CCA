import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryComponent } from '@shared/components';

import { BuyOutSurplusTransactionReceivedAmountHistoryDTO } from 'cca-api';

import { receivedAmountHistoryTitle, toReceivedAmountHistorySummaryData } from '../cca3-received-amount-summary-data';

@Component({
  selector: 'cca-cca3-received-amount-history',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <netz-page-heading>{{ title() }}</netz-page-heading>
        <p class="govuk-!-margin-bottom-9">{{ history().submissionDate | govukDate: 'datetime' }}</p>
        <cca-summary [data]="data()" />
      </div>
    </div>
  `,
  imports: [PageHeadingComponent, GovukDatePipe, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3ReceivedAmountHistoryComponent {
  protected readonly history = signal(
    inject(ActivatedRoute).snapshot.data.historyEntry as BuyOutSurplusTransactionReceivedAmountHistoryDTO,
  );

  protected readonly title = computed(() => receivedAmountHistoryTitle(this.history()));

  protected readonly data = computed(() => toReceivedAmountHistorySummaryData(this.history()));
}
