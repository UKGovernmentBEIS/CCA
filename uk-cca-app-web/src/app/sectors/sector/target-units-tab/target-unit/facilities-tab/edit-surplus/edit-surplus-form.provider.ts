import { InjectionToken, Provider } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import BigNumber from 'bignumber.js';

import { FacilityInfoDTO } from 'cca-api';

export type EditFacilitySurplusForm = FormGroup<{
  surplus: FormControl<string>;
  comments: FormControl<string>;
}>;

export const EDIT_FACILITY_SURPLUS_FORM = new InjectionToken<EditFacilitySurplusForm>('Edit facility surplus form');

export const editFacilitySurplusFormProvider: Provider = {
  provide: EDIT_FACILITY_SURPLUS_FORM,
  deps: [ActivatedRoute],
  useFactory: (route: ActivatedRoute): EditFacilitySurplusForm => {
    const details = route.snapshot.data.facilityDetails as FacilityInfoDTO;
    const currentSurplus = details.surplusDetails?.surplusGained ?? '0';

    return new FormGroup({
      surplus: new FormControl<string>(null, [
        GovukValidators.required('Enter a surplus amount'),
        GovukValidators.wholeNumber('Enter a positive whole number or zero'),
        (control) =>
          /^\d+$/.test(control.value ?? '') && new BigNumber(control.value).eq(currentSurplus)
            ? { sameAsCurrent: 'The new amount cannot be equal to the current amount' }
            : null,
      ]),
      comments: new FormControl('', {
        nonNullable: true,
        validators: [
          GovukValidators.required('Enter a comment'),
          GovukValidators.pattern(/\S/, 'Enter a comment'),
          GovukValidators.maxLength(10000, 'Comments must be 10000 characters or fewer'),
        ],
      }),
    });
  },
};
