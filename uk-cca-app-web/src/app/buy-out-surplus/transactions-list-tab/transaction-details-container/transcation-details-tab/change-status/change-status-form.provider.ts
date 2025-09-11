import { inject, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { BuyOutSurplusTransactionDetailsDTO, BuyOutSurplusTransactionUpdatePaymentStatusDTO } from 'cca-api';

export type ChangeStatusFormModel = FormGroup<{
  status: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['status']>;
  paymentDate: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['paymentDate']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
  // we cannot use one field for all comments because of validations and the current implenetation restrictions of conditionalContentDirective
  awaitingPaymentComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  awaitingRefundComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  appealComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  notRequiredComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  refundedComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  paidComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
}>;

export const CHANGE_STATUS_FORM = new InjectionToken('Change status form');

export const ChangeStatusFormProvider: Provider = {
  provide: CHANGE_STATUS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);
    const details = activatedRoute.snapshot.data.transactionDetails as BuyOutSurplusTransactionDetailsDTO;

    const group = fb.group({
      status: fb.control(details.paymentStatus),
      evidenceFiles: fileEvidenceUploadService.buildFormControl([], false, false) as FormControl<UuidFilePair[]>,
      appealComments: fb.control('', [GovukValidators.required('Please enter a comment')]),
      notRequiredComments: fb.control('', [GovukValidators.required('Please enter a comment')]),
      refundedComments: fb.control(''),
      paidComments: fb.control(''),
      paymentDate: fb.control('', [
        GovukValidators.required('Please enter a date'),
        futureDateValidator('The payment date cannot be a future date.'),
      ]),
      awaitingPaymentComments: fb.control(''),
      awaitingRefundComments: fb.control(''),
    });

    const initialStatus = details.paymentStatus;
    toggleDateValidator(group, details, initialStatus);
    toggleAppealCommentsValidator(group, details, initialStatus);

    group.controls.status.valueChanges.pipe(takeUntilDestroyed()).subscribe((status: string) => {
      toggleDateValidator(group, details, status);
      toggleAppealCommentsValidator(group, details, status);
    });

    return group;
  },
};

function toggleDateValidator(form: ChangeStatusFormModel, details: BuyOutSurplusTransactionDetailsDTO, status: string) {
  const paymentDateCtrl = form.get('paymentDate');

  if (details.chargeType === 'FEE' && status === 'PAID') {
    paymentDateCtrl.enable();
  } else {
    paymentDateCtrl.disable();
    paymentDateCtrl.reset();
  }
}

function toggleAppealCommentsValidator(
  form: ChangeStatusFormModel,
  details: BuyOutSurplusTransactionDetailsDTO,
  status: string,
) {
  const appealCommentsCtrl = form.get('appealComments')!;

  if (details.chargeType === 'FEE' && status === 'UNDER_APPEAL') {
    appealCommentsCtrl.enable();
  } else {
    appealCommentsCtrl.disable();
    appealCommentsCtrl.reset();
  }
}
