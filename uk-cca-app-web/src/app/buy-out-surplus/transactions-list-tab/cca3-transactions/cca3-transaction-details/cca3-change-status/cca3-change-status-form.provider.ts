import { inject, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { BuyOutSurplusTransactionUpdatePaymentStatusDTO, FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

type PaymentStatus = BuyOutSurplusTransactionUpdatePaymentStatusDTO['status'];

export type Cca3ChangeStatusFormModel = FormGroup<{
  status: FormControl<PaymentStatus>;
  paymentDate: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['paymentDate']>;
  dateOfAppeal: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['dateOfAppeal']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
  // one comments control per status, because the conditional content directive enables/disables the controls of each option
  awaitingPaymentComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  awaitingRefundComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  appealComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  notRequiredComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  refundedComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
  paidComments: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['comments']>;
}>;

export const COMMENTS_MAX_LENGTH = 10000;

const commentsMaxLength = GovukValidators.maxLength(
  COMMENTS_MAX_LENGTH,
  `The comments should not be more than ${COMMENTS_MAX_LENGTH} characters`,
);

export const CCA3_CHANGE_STATUS_FORM = new InjectionToken<Cca3ChangeStatusFormModel>('CCA3 change status form');

export const Cca3ChangeStatusFormProvider: Provider = {
  provide: CCA3_CHANGE_STATUS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);
    const details = activatedRoute.snapshot.data.transactionDetails as FacilityBuyOutSurplusTransactionDTO;
    const initialStatus: PaymentStatus = details.chargeType === 'FEE' ? 'AWAITING_PAYMENT' : 'AWAITING_REFUND';

    const group = fb.group({
      status: fb.control<PaymentStatus>(initialStatus),
      evidenceFiles: fileEvidenceUploadService.buildFormControl([], false, false) as FormControl<UuidFilePair[]>,
      appealComments: fb.control('', [GovukValidators.required('Please enter a comment'), commentsMaxLength]),
      notRequiredComments: fb.control('', [GovukValidators.required('Please enter a comment'), commentsMaxLength]),
      refundedComments: fb.control('', [commentsMaxLength]),
      paidComments: fb.control('', [commentsMaxLength]),
      paymentDate: fb.control('', [
        GovukValidators.required('Please enter a date'),
        futureDateValidator('The payment date cannot be a future date.'),
      ]),
      dateOfAppeal: fb.control('', [
        GovukValidators.required('Please enter a date'),
        futureDateValidator('The appeal date cannot be a future date.'),
      ]),
      awaitingPaymentComments: fb.control('', [commentsMaxLength]),
      awaitingRefundComments: fb.control('', [commentsMaxLength]),
    });

    toggleFeeOnlyControls(group, details, initialStatus);

    group.controls.status.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((status) => toggleFeeOnlyControls(group, details, status));

    return group;
  },
};

// the fee only options are not rendered for refunds, so the conditional content directive never disables their controls
function toggleFeeOnlyControls(
  form: Cca3ChangeStatusFormModel,
  details: FacilityBuyOutSurplusTransactionDTO,
  status: PaymentStatus,
) {
  const isFee = details.chargeType === 'FEE';

  toggleControl(form.controls.paymentDate, isFee && status === 'PAID');
  toggleControl(form.controls.dateOfAppeal, isFee && status === 'UNDER_APPEAL');
  toggleControl(form.controls.appealComments, isFee && status === 'UNDER_APPEAL');
}

function toggleControl(control: FormControl, enabled: boolean) {
  if (enabled) {
    control.enable();
  } else {
    control.disable();
    control.reset();
  }
}
