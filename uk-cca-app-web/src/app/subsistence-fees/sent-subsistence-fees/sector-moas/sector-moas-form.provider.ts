import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';

import { SubsistenceFeesMoaSearchCriteria } from 'cca-api';

export const SECTOR_MOAS_FORM = new InjectionToken('Sector Moas Form');

export type SectorMoasFormModel = FormGroup<{
  term: FormControl<SubsistenceFeesMoaSearchCriteria['term']>;
  paymentStatus: FormControl<SubsistenceFeesMoaSearchCriteria['paymentStatus']>;
  markFacilitiesStatus: FormControl<SubsistenceFeesMoaSearchCriteria['markFacilitiesStatus']>;
}>;

export const INITIAL_VALUES = {
  term: null,
  paymentStatus: null,
  markFacilitiesStatus: null,
};

export const SectorMoasFormProvider: Provider = {
  provide: SECTOR_MOAS_FORM,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    return fb.group({
      term: fb.control<string | null>(null, [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(255, 'Enter up to 255 characters'),
      ]),
      paymentStatus: fb.control<string | null>(null),
      markFacilitiesStatus: fb.control<string | null>(null),
    });
  },
};
