import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { SchemeVersion, SchemeVersionEnum } from '@shared/types';

import { BuyOutSurplusTransactionsListSearchCriteria } from 'cca-api';

import { extractBuyOutSurplusPaymentStatus, extractSchemeVersion, extractTargetPeriodType } from '../utils';

export type TransactionReportFormModel = FormGroup<{
  term: FormControl<BuyOutSurplusTransactionsListSearchCriteria['term']>;
  targetPeriodType: FormControl<BuyOutSurplusTransactionsListSearchCriteria['targetPeriodType'] | null>;
  paymentStatus: FormControl<BuyOutSurplusTransactionsListSearchCriteria['paymentStatus']>;
  schemeVersion: FormControl<SchemeVersion>;
}>;

export const TRANSACTION_REPORT_FORM = new InjectionToken<TransactionReportFormModel>(
  'Buy-out surplus transactions report form',
);

export const transactionInitialValues: Omit<BuyOutSurplusTransactionsListSearchCriteria, 'pageNumber'> = {
  term: null,
  targetPeriodType: null,
  paymentStatus: 'AWAITING_PAYMENT',
  pageSize: 50,
  schemeVersion: SchemeVersionEnum.CCA_3,
};

/**
 * Maps the URL query params to the filters form value. A missing payment status or target period means "All", as it
 * does for the transactions table.
 */
export function toTransactionFiltersFormValue(queryParamMap: ParamMap): TransactionReportFormModel['value'] {
  return {
    term: queryParamMap.get('term'),
    targetPeriodType: extractTargetPeriodType(queryParamMap),
    paymentStatus: extractBuyOutSurplusPaymentStatus(queryParamMap),
    schemeVersion: extractSchemeVersion(queryParamMap),
  };
}

export const TransactionReportFormProvider: Provider = {
  provide: TRANSACTION_REPORT_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const { term, targetPeriodType, paymentStatus, schemeVersion } = toTransactionFiltersFormValue(
      route.snapshot.queryParamMap,
    );

    return fb.group({
      term: fb.control(term, {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      targetPeriodType: fb.control(targetPeriodType),
      paymentStatus: fb.control(paymentStatus),
      schemeVersion: fb.control(schemeVersion),
    });
  },
};
