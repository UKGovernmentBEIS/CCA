import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-payment-not-completed',
  template: ` <cca-page-heading>The payment task must be closed before you can proceed</cca-page-heading> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentNotCompletedComponent {}
