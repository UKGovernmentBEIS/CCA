import { inject, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import {
  paymentDateRequiredValidator,
  paymentDateValidator,
  receivedAmountTotalValidator,
} from './cca3-received-amount.validators';

export type Cca3ReceivedAmountForm = FormGroup<{
  changeType: FormControl<'add' | 'subtract'>;
  adjustmentAmount: FormControl<string>;
  paymentDate: FormControl<Date | null>;
  comments: FormControl<string>;
  evidenceFiles: FormControl<UuidFilePair[]>;
}>;

export const CCA3_RECEIVED_AMOUNT_FORM = new InjectionToken<Cca3ReceivedAmountForm>('CCA3 received amount form');

export const cca3ReceivedAmountFormProvider: Provider = {
  provide: CCA3_RECEIVED_AMOUNT_FORM,
  useFactory: (): Cca3ReceivedAmountForm => {
    const transaction = inject(ActivatedRoute).snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO;
    const changeType = new FormControl<'add' | 'subtract'>('add', { nonNullable: true });
    const paymentDate = new FormControl<Date | null>(null, [
      paymentDateRequiredValidator(changeType),
      paymentDateValidator(),
    ]);
    const amountErrorMessage = 'Enter a number greater than 0 with up to 2 decimal places';

    const form = new FormGroup(
      {
        changeType,
        adjustmentAmount: new FormControl('', [
          GovukValidators.required('Enter a positive value'),
          GovukValidators.positiveNumber(amountErrorMessage),
          GovukValidators.builder(amountErrorMessage, GovukValidators.maxIntegerAndDecimalsValidator(18, 2)),
        ]),
        paymentDate,
        comments: new FormControl('', [
          GovukValidators.required('Enter a comment'),
          GovukValidators.pattern(/\S/, 'Enter a comment'),
          GovukValidators.maxLength(10000, 'Comments must be 10000 characters or fewer'),
        ]),
        evidenceFiles: inject(FileEvidenceUploadService).buildFormControl([], false, false) as FormControl<
          UuidFilePair[]
        >,
      },
      { validators: receivedAmountTotalValidator(transaction.receivedAmount ?? '0') },
    );

    changeType.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => paymentDate.updateValueAndValidity());

    return form;
  },
};
