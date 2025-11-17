import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import { SummaryComponent } from '@shared/components';

import { ReceivedAmountStore } from '../received-amount.store';
import { toHistoryDetailsSummary } from './history-details-summary';

@Component({
  selector: 'cca-history-details',
  template: `
    <netz-page-heading>
      Amount {{ +amountHistory?.transactionAmount > 0 ? 'added' : 'subtracted' }}
      {{
        +amountHistory?.transactionAmount > 0
          ? (amountHistory?.transactionAmount | number)
          : (amountHistory?.transactionAmount.split('-')[1] | number)
      }}
      GBP by "{{ amountHistory?.submitter }}"
    </netz-page-heading>

    <p class="govuk-!-margin-bottom-9">{{ amountHistory?.submissionDate | govukDate: 'datetime' }}</p>

    <cca-summary [data]="data" />
  `,
  imports: [PageHeadingComponent, GovukDatePipe, DecimalPipe, SummaryComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoryDetailsComponent {
  protected readonly activatedRoute = inject(ActivatedRoute);
  private readonly receivedAmountStore = inject(ReceivedAmountStore);

  protected readonly detailsId = +this.activatedRoute.snapshot.paramMap.get('detailsId');

  protected readonly amountHistory = this.receivedAmountStore
    .stateAsSignal()
    ?.receivedAmountHistoryList.find((i) => i.id === this.detailsId);

  protected readonly data = toHistoryDetailsSummary(this.amountHistory);
}
