import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { FacilityBuyOutSurplusTransactionDetailsSearchCriteria } from 'cca-api';

export type CCA3TransactionDetailsFiltersFormModel = FormGroup<{
  term: FormControl<FacilityBuyOutSurplusTransactionDetailsSearchCriteria['term']>;
  paymentStatus: FormControl<FacilityBuyOutSurplusTransactionDetailsSearchCriteria['paymentStatus']>;
}>;

export const CCA3_TRANSACTION_DETAILS_FILTERS_FORM = new InjectionToken<CCA3TransactionDetailsFiltersFormModel>(
  'CCA3 transaction details filters form',
);

export const transactionInitialValues: Pick<
  FacilityBuyOutSurplusTransactionDetailsSearchCriteria,
  'term' | 'paymentStatus'
> = {
  term: null,
  paymentStatus: null,
};

export const CCA3TransactionDetailsFiltersFormProvider: Provider = {
  provide: CCA3_TRANSACTION_DETAILS_FILTERS_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const queryParamMap = route.snapshot.queryParamMap;

    return fb.group({
      term: fb.control(queryParamMap.get('term') ?? null, {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(255, 'Enter up to 255 characters'),
        ],
      }),
      paymentStatus: fb.control(queryParamMap.get('paymentStatus') ?? null),
    });
  },
};
