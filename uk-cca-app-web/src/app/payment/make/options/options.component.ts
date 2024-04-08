import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BreadcrumbService } from '@core/navigation';

import { GovukValidators } from 'govuk-components';

import { PaymentMakeRequestTaskPayload } from 'cca-api';

import { getHeadingMap } from '../../core/payment.map';
import { getPaymentBaseLink } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'cca-options',
  templateUrl: './options.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OptionsComponent implements OnInit {
  readonly availablePaymentMethods$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
    map((payload) => payload?.paymentMethodTypes),
  );
  form: UntypedFormGroup;

  constructor(
    readonly store: PaymentStore,
    readonly pendingRequest: PendingRequestService,
    private readonly formBuilder: UntypedFormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.store.subscribe((state) => {
      this.breadcrumbService.show([
        {
          text: getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)[
            state.requestTaskItem?.requestTask.type
          ],
          link: [getPaymentBaseLink(state.requestType) + 'payment', state.requestTaskId, 'make', 'details'],
        },
      ]);
    });

    this.store.pipe(first()).subscribe(
      (state) =>
        (this.form = this.formBuilder.group({
          paymentMethod: [
            { value: null, disabled: !state.isEditable },
            { validators: GovukValidators.required('Select a payment method'), updateOn: 'change' },
          ],
        })),
    );
  }

  onContinue(): void {
    const paymentMethod = this.form.get('paymentMethod').value;
    this.store.setState({
      ...this.store.getState(),
      selectedPaymentMethod: paymentMethod,
    });

    switch (paymentMethod) {
      case 'BANK_TRANSFER':
        this.router.navigate(['../bank-transfer'], { relativeTo: this.route });
        break;
      case 'CREDIT_OR_DEBIT_CARD':
        this.store
          .pipe(
            first(),
            switchMap((state) => this.store.postCreateCardPayment(state.requestTaskId)),
            this.pendingRequest.trackRequest(),
          )
          .subscribe((res) => {
            if (res.pendingPaymentExist) {
              this.router.navigate(['../confirmation'], {
                relativeTo: this.route,
                queryParams: { method: 'CREDIT_OR_DEBIT_CARD' },
              });
            } else {
              window.location.assign(res.nextUrl);
            }
          });
        break;
    }
  }
}
