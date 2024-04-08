import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'cca-not-success',
  template: `
    <cca-page-heading>{{ message$ | async }}</cca-page-heading>
    <cca-return-link [requestType]="(store | async).requestType" [home]="true"></cca-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotSuccessComponent {
  message$ = this.route.queryParams.pipe(map((params) => params?.message));

  constructor(
    readonly store: PaymentStore,
    private readonly route: ActivatedRoute,
  ) {}
}
