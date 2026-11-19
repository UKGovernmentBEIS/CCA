import { inject, InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { BuyOutSurplusTransactionMarkPaymentStatusDTO } from 'cca-api';

import { Cca3MarkAllStatus } from './cca3-mark-all-facilities.types';

export type Cca3MarkAllFacilitiesFormModel = FormGroup<{
  paymentDate: FormControl<BuyOutSurplusTransactionMarkPaymentStatusDTO['paymentDate']>;
  comments: FormControl<BuyOutSurplusTransactionMarkPaymentStatusDTO['comments']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
}>;

export const COMMENTS_MAX_LENGTH = 10000;

export const CCA3_MARK_ALL_FACILITIES_FORM = new InjectionToken<Cca3MarkAllFacilitiesFormModel>(
  'CCA3 mark all facilities form',
);

export const Cca3MarkAllFacilitiesFormProvider: Provider = {
  provide: CCA3_MARK_ALL_FACILITIES_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);
    const status = activatedRoute.snapshot.data.markAllStatus as Cca3MarkAllStatus;

    const group = fb.group({
      paymentDate: fb.control('', [
        GovukValidators.required('Enter the date when the payment was received in the bank account'),
        futureDateValidator('The date when the payment was received must be today or in the past'),
      ]),
      comments: fb.control('', [
        GovukValidators.maxLength(
          COMMENTS_MAX_LENGTH,
          `The comments should not be more than ${COMMENTS_MAX_LENGTH} characters`,
        ),
      ]),
      evidenceFiles: fileEvidenceUploadService.buildFormControl([], false, false) as FormControl<UuidFilePair[]>,
    });

    // refunds have no payment date
    if (status === 'REFUNDED') group.controls.paymentDate.disable();

    return group;
  },
};
