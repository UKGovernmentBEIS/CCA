import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import {
  baselineChangesTypes,
  changesRequiredFieldsValidator,
  facilityChangesTypes,
  otherChangesTypes,
  targetCurrencyChangesTypes,
  underlyingAgreementVariationQuery,
} from '@requests/common';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

export type VariationDetailsFormModel = {
  facilityChanges: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  baselineChanges: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  targetCurrencyChanges: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  otherChanges: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  reason: FormControl<UnderlyingAgreementVariationDetails['reason']>;
};

export const VARIATION_DETAILS_FORM = new InjectionToken<VariationDetailsFormModel>('Variation Details Form');

export const VariationDetailsFormProvider: Provider = {
  provide: VARIATION_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const variationDetails = store.select(underlyingAgreementVariationQuery.selectVariationDetails)();

    let facilityChanges,
      baselineChanges,
      targetCurrencyChanges,
      otherChanges = [];

    if (variationDetails?.modifications?.length) {
      facilityChanges = facilityChangesTypes.filter((c) => variationDetails.modifications.includes(c));
      baselineChanges = baselineChangesTypes.filter((c) => variationDetails.modifications.includes(c));
      targetCurrencyChanges = targetCurrencyChangesTypes.filter((c) => variationDetails.modifications.includes(c));
      otherChanges = otherChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    }

    return fb.group(
      {
        facilityChanges: fb.control(facilityChanges),
        baselineChanges: fb.control(baselineChanges),
        targetCurrencyChanges: fb.control(targetCurrencyChanges),
        otherChanges: fb.control(otherChanges),
        reason: fb.control(variationDetails?.reason ?? null, [
          GovukValidators.required('Explain what you are changing and the reason for the changes'),
          GovukValidators.maxLength(10000, 'The reason for the changes should not be more than 10000 characters'),
        ]),
      },
      {
        validators: [changesRequiredFieldsValidator()],
      },
    );
  },
};
