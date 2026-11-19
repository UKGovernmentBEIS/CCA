import { inject, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { UuidFilePair } from '@shared/components';
import { FileEvidenceUploadService } from '@shared/services';
import { futureDateValidator } from '@shared/validators';

import { AccountBuyOutSurplusTransactionDTO, BuyOutSurplusTransactionUpdatePaymentStatusDTO } from 'cca-api';

export type ChangeStatusFormModel = FormGroup<{
  status: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['status']>;
  paymentDate: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['paymentDate']>;
  dateOfAppeal: FormControl<BuyOutSurplusTransactionUpdatePaymentStatusDTO['dateOfAppeal']>;
  evidenceFiles: FormControl<UuidFilePair[]>;
  // we cannot use one field for all comments because of validations and the current implenetation restrictions of conditionalContentDirective
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

export const CHANGE_STATUS_FORM = new InjectionToken('Change status form');

export const ChangeStatusFormProvider: Provider = {
  provide: CHANGE_STATUS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const fileEvidenceUploadService = inject(FileEvidenceUploadService);
    const details = activatedRoute.snapshot.data.transactionDetails as AccountBuyOutSurplusTransactionDTO;

    const group = fb.group({
      status: fb.control(details.paymentStatus),
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

    toggleFeeOnlyControls(group, details, details.paymentStatus);

    group.controls.status.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((status: string) => toggleFeeOnlyControls(group, details, status));

    return group;
  },
};

// the fee only options are not rendered for refunds, so the conditional content directive never disables their controls
function toggleFeeOnlyControls(
  form: ChangeStatusFormModel,
  details: AccountBuyOutSurplusTransactionDTO,
  status: string,
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
