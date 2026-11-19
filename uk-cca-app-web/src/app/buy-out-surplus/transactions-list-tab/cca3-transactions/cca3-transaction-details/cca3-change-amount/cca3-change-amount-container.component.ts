import { Component, inject, OnDestroy } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';

import { Cca3ChangeAmountState } from './cca3-change-amount.state';

@Component({
  selector: 'cca-cca3-change-amount-container',
  template: '<router-outlet />',
  imports: [RouterOutlet],
})
export class Cca3ChangeAmountContainerComponent implements OnDestroy {
  private readonly state = inject(Cca3ChangeAmountState);

  constructor() {
    const { transactionDetails, facilityDetails } = inject(ActivatedRoute).snapshot.data;
    this.state.initialize(transactionDetails, facilityDetails);
  }

  ngOnDestroy() {
    this.state.resetDraft();
  }
}
