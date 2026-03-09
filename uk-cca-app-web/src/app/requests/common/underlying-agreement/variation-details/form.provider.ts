import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { UnderlyingAgreementVariationDetails } from 'cca-api';

import { underlyingAgreementVariationQuery } from '../+state';
import { dontRequireOperatorAssentTypes, otherChangesTypes, requireOperatorAssentTypes } from './types';
import { changesRequiredFieldsValidator } from './validators';

export type VariationDetailsFormModel = {
  requireOperatorAssent: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  dontRequireOperatorAssent: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  otherChanges: FormControl<UnderlyingAgreementVariationDetails['modifications']>;
  reason: FormControl<UnderlyingAgreementVariationDetails['reason']>;
};

export const VARIATION_DETAILS_FORM = new InjectionToken<VariationDetailsFormModel>('Variation Details Form');

export const VariationDetailsFormProvider: Provider = {
  provide: VARIATION_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const variationDetails = requestTaskStore.select(underlyingAgreementVariationQuery.selectVariationDetails)();

    let requireOperatorAssent = [];
    let dontRequireOperatorAssent = [];
    let otherChanges = [];

    if (variationDetails?.modifications?.length) {
      requireOperatorAssent = requireOperatorAssentTypes.filter((c) => variationDetails.modifications.includes(c));
      dontRequireOperatorAssent = dontRequireOperatorAssentTypes.filter((c) =>
        variationDetails.modifications.includes(c),
      );
      otherChanges = otherChangesTypes.filter((c) => variationDetails.modifications.includes(c));
    }

    return fb.group(
      {
        requireOperatorAssent: fb.control(requireOperatorAssent),
        dontRequireOperatorAssent: fb.control(dontRequireOperatorAssent),
        otherChanges: fb.control(otherChanges),
        reason: fb.control(variationDetails?.reason ?? null, [
          GovukValidators.required('Explain in more detail what you are changing and the reason for the changes'),
          GovukValidators.maxLength(10000, 'The reason for the changes should not be more than 10000 characters'),
        ]),
      },
      {
        validators: [changesRequiredFieldsValidator()],
        updateOn: 'submit',
      },
    );
  },
};
