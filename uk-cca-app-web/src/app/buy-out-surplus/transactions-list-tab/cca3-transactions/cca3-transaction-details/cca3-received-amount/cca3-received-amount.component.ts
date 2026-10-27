import { ChangeDetectionStrategy, Component, computed, inject, OnDestroy, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { GovukDatePipe } from '@netz/common/pipes';
import {
  DateInputComponent,
  DetailsComponent,
  GovukSelectOption,
  SelectComponent,
  TextareaComponent,
} from '@netz/govuk-components';
import {
  MultipleFileInputComponent,
  SummaryComponent,
  TextInputComponent,
  WizardStepComponent,
} from '@shared/components';

import { BuyOutSurplusTransactionReceivedAmountHistoryDTO, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { CCA3_RECEIVED_AMOUNT_FORM } from './cca3-received-amount-form.provider';
import { receivedAmountHistoryTitle, toReceivedAmountSummaryData } from './cca3-received-amount-summary-data';

@Component({
  selector: 'cca-cca3-received-amount',
  templateUrl: './cca3-received-amount.component.html',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    GovukDatePipe,
    DateInputComponent,
    DetailsComponent,
    SelectComponent,
    TextareaComponent,
    MultipleFileInputComponent,
    SummaryComponent,
    TextInputComponent,
    WizardStepComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cca3ReceivedAmountComponent implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly form = inject(CCA3_RECEIVED_AMOUNT_FORM);

  private readonly initialPaymentDateValidator = this.form.controls.paymentDate.validator;

  protected readonly isSubtract = toSignal(
    this.form.controls.changeType.valueChanges.pipe(map((type) => type === 'subtract')),
    { initialValue: this.form.controls.changeType.value === 'subtract' },
  );

  private readonly transaction = signal(
    this.route.snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO,
  );

  protected readonly summaryData = computed(() => toReceivedAmountSummaryData(this.transaction()));

  protected readonly history = signal(
    this.route.snapshot.data.receivedAmountHistory as BuyOutSurplusTransactionReceivedAmountHistoryDTO[],
  );

  protected readonly historyTitle = receivedAmountHistoryTitle;

  protected readonly changeTypeOptions: GovukSelectOption[] = [
    { text: 'Add', value: 'add' },
    { text: 'Subtract', value: 'subtract' },
  ];

  onSubmit() {
    if (!this.form.valid) return;

    this.router.navigate(['check-your-answers'], { relativeTo: this.route });
  }

  ngOnDestroy() {
    // Keep the date-input cleanup local until the shared validator lifecycle is addressed separately.
    this.form.controls.paymentDate.setValidators(this.initialPaymentDateValidator);
    this.form.controls.paymentDate.updateValueAndValidity({ emitEvent: false });
  }
}
