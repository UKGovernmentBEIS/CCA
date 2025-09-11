import { inject, InjectionToken, Provider } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
} from '@angular/forms';

import { Observable, of } from 'rxjs';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';
import BigNumber from 'bignumber.js';

import { SubsistenceFeesMoaReceivedAmountDetailsDTO } from 'cca-api';

import { SectorMoasReceivedAmountStore } from './received-amount.store';

export const RECEIVED_AMOUNT_FORM = new InjectionToken('Received amount form');

const negativeTotalAmountValidator = (): AsyncValidatorFn => {
  const state = inject(SectorMoasReceivedAmountStore).stateAsSignal;

  return (group: AbstractControl): Observable<ValidationErrors | null> => {
    const bigReceivedAmount = new BigNumber(state().receivedAmount);
    const bigTransactionAmount = new BigNumber(group.value.transactionAmount);

    const newReceivedAmount =
      group.value.changeType === 'add'
        ? bigReceivedAmount.plus(bigTransactionAmount).toNumber()
        : bigReceivedAmount.minus(bigTransactionAmount).toNumber();

    if (newReceivedAmount < 0) {
      return of({
        noNegativeTotalAmount: 'Enter a payment amount that does not result in a negative received amount.',
      });
    }

    return of(null);
  };
};

export type ReceivedAmountFormModel = FormGroup<{
  changeType: FormControl<'add' | 'subtract'>;
  transactionAmount: FormControl<SubsistenceFeesMoaReceivedAmountDetailsDTO['transactionAmount']>;
  comments?: FormControl<SubsistenceFeesMoaReceivedAmountDetailsDTO['comments']>;
  evidenceFiles?: FormControl<UuidFilePair[]>;
}>;

export const ReceivedAmountFormProvider: Provider = {
  provide: RECEIVED_AMOUNT_FORM,
  deps: [FormBuilder, SectorMoasReceivedAmountStore],
  useFactory: (fb: FormBuilder, receivedAmountStore: SectorMoasReceivedAmountStore) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);
    const state = receivedAmountStore.stateAsSignal;

    const transactionAmount =
      state()?.changeType === 'add'
        ? state()?.details?.transactionAmount.split('+')[1]
        : state()?.details?.transactionAmount.split('-')[1];

    const evidenceFiles = Object.entries(state()?.details?.evidenceFiles ?? {}).map(([key, value]) => ({
      uuid: key,
      name: value,
    }));

    return fb.group(
      {
        changeType: fb.control<'add' | 'subtract'>(state()?.changeType),
        transactionAmount: fb.control<string>(transactionAmount, {
          validators: [
            GovukValidators.required('Enter a numerical value, without alpha or special characters'),
            GovukValidators.maxIntegerAndDecimalsValidator(10, 2),
            GovukValidators.positiveNumber('Enter a payment amount that is greater than zero'),
          ],
        }),
        comments: fb.control<string>(state()?.details?.comments),
        evidenceFiles: fileEvidenceUploadService.buildFormControl(evidenceFiles),
      },
      { updateOn: 'submit', asyncValidators: [negativeTotalAmountValidator()] },
    );
  },
};
