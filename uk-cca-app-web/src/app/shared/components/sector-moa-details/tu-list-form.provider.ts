import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { SubsistenceFeesMoaSearchCriteria } from 'cca-api';

export const TARGET_UNITS_LIST_FORM = new InjectionToken('Sector Moas Form');

export type TargetUnitsListFormModel = FormGroup<{
  term: FormControl<SubsistenceFeesMoaSearchCriteria['term']>;
  markFacilitiesStatus: FormControl<SubsistenceFeesMoaSearchCriteria['markFacilitiesStatus']>;
}>;

export const INITIAL_FORM_VALUES = {
  term: null,
  markFacilitiesStatus: null,
};

export const TargetUnitsListFormProvider: Provider = {
  provide: TARGET_UNITS_LIST_FORM,
  deps: [FormBuilder, ActivatedRoute],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
    const queryParamMap = activatedRoute.snapshot.queryParamMap;

    return fb.group({
      term: fb.control<string | null>(queryParamMap.get('term') || INITIAL_FORM_VALUES.term, [
        GovukValidators.minLength(3, 'Enter at least 3 characters'),
        GovukValidators.maxLength(255, 'Enter up to 255 characters'),
      ]),
      markFacilitiesStatus: fb.control<string | null>(
        queryParamMap.get('markFacilitiesStatus') || INITIAL_FORM_VALUES.markFacilitiesStatus,
      ),
    });
  },
};
