import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';

import { BuyOutSurplusTransactionDetailsDTO } from 'cca-api';

@Component({
  selector: 'cca-terminated-transaction',
  imports: [PageHeadingComponent, RouterLink],
  template: `
    <netz-page-heading caption="Change">{{ heading() }}</netz-page-heading>
    <div class="govuk-grid-row govuk-!-margin-top-3">
      <div class="govuk-grid-column-two-thirds">
        <div class="govuk-label--s">
          Changes are not allowed because the transaction has been automatically marked as terminated.
        </div>
      </div>
    </div>

    <div class="govuk-grid-row govuk-!-margin-top-6">
      <div class="govuk-grid-column-two-thirds">
        <hr class="govuk-footer__section-break" />
        <a class="govuk-link" [routerLink]="['..']" [replaceUrl]="true" fragment="transaction-details">
          Return to: {{ transactionDetails().transactionCode }}</a
        >
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TerminatedTransactionComponent {
  protected readonly transactionDetails = input<BuyOutSurplusTransactionDetailsDTO>();
  protected readonly heading = input<'Current amount' | 'Status'>();
}
