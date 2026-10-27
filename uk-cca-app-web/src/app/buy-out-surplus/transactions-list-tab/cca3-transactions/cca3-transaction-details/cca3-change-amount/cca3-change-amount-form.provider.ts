import { inject, InjectionToken, Provider } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';

export type Cca3ChangeAmountForm = FormGroup<{
  amount: FormControl<string>;
  comments: FormControl<string>;
  evidenceFiles: FormControl<UuidFilePair[]>;
}>;

export const CCA3_CHANGE_AMOUNT_FORM = new InjectionToken<Cca3ChangeAmountForm>('CCA3 change amount form');

const AMOUNT_ERROR_MESSAGE = 'Enter a number greater than 0 with up to 2 decimal place';

export const cca3ChangeAmountFormProvider: Provider = {
  provide: CCA3_CHANGE_AMOUNT_FORM,
  useFactory: (): Cca3ChangeAmountForm =>
    new FormGroup({
      // Keep the API's decimal string representation, including values beyond Number.MAX_SAFE_INTEGER.
      amount: new FormControl('', [
        GovukValidators.required(AMOUNT_ERROR_MESSAGE),
        GovukValidators.positiveNumber(AMOUNT_ERROR_MESSAGE),
        GovukValidators.builder(AMOUNT_ERROR_MESSAGE, GovukValidators.maxIntegerAndDecimalsValidator(18, 2)),
      ]),
      comments: new FormControl('', [
        GovukValidators.required('Enter a reason'),
        GovukValidators.pattern(/\S/, 'Enter a reason'),
        GovukValidators.maxLength(10000, 'Comments must be 10000 characters or fewer'),
      ]),
      evidenceFiles: inject(FileEvidenceUploadService).buildFormControl([], false, false) as FormControl<
        UuidFilePair[]
      >,
    }),
};
