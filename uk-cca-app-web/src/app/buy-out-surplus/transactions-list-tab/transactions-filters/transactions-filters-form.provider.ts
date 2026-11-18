import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { TransactionsCriteria } from '../utils';

export type TransactionReportFormModel = FormGroup<{
  term: FormControl<TransactionsCriteria['term']>;
  targetPeriodType: FormControl<'TP5' | 'TP6' | 'TP7' | 'TP8' | 'TP9'>;
  buyOutSurplusPaymentStatus: FormControl<TransactionsCriteria['buyOutSurplusPaymentStatus']>;
}>;

export const TRANSACTION_REPORT_FORM = new InjectionToken<TransactionReportFormModel>(
  'Buy-out surplus transactions report form',
);

export const transactionInitialValues: Omit<TransactionsCriteria, 'pageNumber'> = {
  term: null,
  targetPeriodType: 'TP6',
  buyOutSurplusPaymentStatus: 'AWAITING_PAYMENT',
  pageSize: 50,
};

export const TransactionReportFormProvider: Provider = {
  provide: TRANSACTION_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      term: fb.control(queryParamMap.get('term'), {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      targetPeriodType: fb.control('TP6'),
      buyOutSurplusPaymentStatus: fb.control(queryParamMap.get('buyOutSurplusPaymentStatus') ?? 'AWAITING_PAYMENT'),
    });
  },
};
