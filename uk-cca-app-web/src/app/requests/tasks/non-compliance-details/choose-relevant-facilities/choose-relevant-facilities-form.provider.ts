import { InjectionToken, Provider } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';

export type FacilityFormGroup = FormGroup<{
  facilityBusinessId: FormControl<string | null>;
  isHistorical: FormControl<boolean>;
}>;

export type ChooseRelevantFacilitiesFormModel = FormGroup<{
  facilities: FormArray<FacilityFormGroup>;
}>;

export const CHOOSE_RELEVANT_FACILITIES_FORM = new InjectionToken<ChooseRelevantFacilitiesFormModel>(
  'Choose relevant facilities form',
);

export const ChooseRelevantFacilitiesFormProvider: Provider = {
  provide: CHOOSE_RELEVANT_FACILITIES_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const relevantFacilities = store.select(nonComplianceDetailsQuery.selectRelevantFacilities)() ?? [];
    const facilityControls = relevantFacilities.map((facility) =>
      createFacilityFormGroup(fb, facility.isHistorical, facility.facilityBusinessId),
    );

    return fb.group({
      facilities: fb.array<FacilityFormGroup>(facilityControls),
    });
  },
};

export function createFacilityFormGroup(fb: FormBuilder, isHistorical: boolean, value?: string): FacilityFormGroup {
  return fb.group({
    facilityBusinessId: fb.control(value ?? null),
    isHistorical: fb.control(isHistorical, { nonNullable: true }),
  });
}
