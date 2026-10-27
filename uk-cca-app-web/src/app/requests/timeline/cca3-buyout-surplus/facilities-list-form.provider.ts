import { InjectionToken, Provider } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { FacilityResult, FacilityTargetPeriod } from './facilities-list';

export type FacilitiesListFormModel = FormGroup<{
  search: FormControl<string>;
  targetPeriod: FormControl<FacilityTargetPeriod | null>;
  result: FormControl<FacilityResult | null>;
}>;

export const FACILITIES_LIST_FORM_PROVIDER = new InjectionToken<FacilitiesListFormModel>(
  'CCA3 facilities list filters',
);

export const FacilitiesListFormProvider: Provider = {
  provide: FACILITIES_LIST_FORM_PROVIDER,
  useFactory: (): FacilitiesListFormModel =>
    new FormGroup({
      search: new FormControl('', { nonNullable: true }),
      targetPeriod: new FormControl<FacilityTargetPeriod | null>(null),
      result: new FormControl<FacilityResult | null>(null),
    }),
};
