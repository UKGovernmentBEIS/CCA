import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';

import { BuyOutSurplusTransactionUpdateAmountDTO } from 'cca-api';

export const CHANGE_AMOUNT_FORM = new InjectionToken('Change amount form');

export type ChangeAmountFormModel = FormGroup<{
  amount: FormControl<BuyOutSurplusTransactionUpdateAmountDTO['amount']>;
  comments: FormControl<BuyOutSurplusTransactionUpdateAmountDTO['comments']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
}>;

export const ChangeAmountFormProvider: Provider = {
  provide: CHANGE_AMOUNT_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);

    return fb.group({
      amount: fb.control('', [
        GovukValidators.required('Enter an amount'),
        GovukValidators.maxDecimalsValidator(2),
        GovukValidators.positiveNumber('Enter a positive number with up to 2 decimal places'),
      ]),
      comments: fb.control('', [GovukValidators.required('Enter a comment')]),
      evidenceFiles: fileEvidenceUploadService.buildFormControl([], false, false),
    });
  },
};
