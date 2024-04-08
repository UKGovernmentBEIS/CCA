import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'cca-completed',
  template: `
    <ng-container *ngIf="store | async as state">
      <cca-request-action-heading
        headerText="Payment completed"
        [timelineCreationDate]="state.requestActionCreationDate"
      >
      </cca-request-action-heading>
      <cca-payment-summary [details]="details$ | async" [shouldDisplayAmount]="shouldDisplayAmount$ | async">
        <h2 cca-summary-header class="govuk-heading-m">Details</h2>
      </cca-payment-summary>
    </ng-container>
    <cca-return-link [requestType]="(store | async).requestType" [home]="true"></cca-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedComponent {
  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  constructor(readonly store: PaymentStore) {}
  details$ = this.store.pipe(
    map((state: any) => {
      return { ...state.actionPayload, amount: +state.actionPayload.amount };
    }),
  );
}
