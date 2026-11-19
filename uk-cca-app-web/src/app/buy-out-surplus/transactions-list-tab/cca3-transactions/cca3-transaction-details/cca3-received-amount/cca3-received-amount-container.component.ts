import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Cca3ReceivedAmountState } from './cca3-received-amount.state';
import { CCA3_RECEIVED_AMOUNT_FORM, cca3ReceivedAmountFormProvider } from './cca3-received-amount-form.provider';

@Component({
  selector: 'cca-cca3-received-amount-container',
  template: '<router-outlet />',
  imports: [RouterOutlet],
  providers: [cca3ReceivedAmountFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3ReceivedAmountContainerComponent implements OnDestroy {
  private readonly state = inject(Cca3ReceivedAmountState);

  constructor() {
    this.state.form = inject(CCA3_RECEIVED_AMOUNT_FORM);
  }

  ngOnDestroy() {
    this.state.form = null;
  }
}
