import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';

import { FacilityDataDetailsDTO } from 'cca-api';

export type EditFacilityDetailsFormModel = FormGroup<{
  hasFacilityExited: FormControl<boolean>;
  schemeExitDate: FormControl<Date | null>;
}>;

export const EDIT_FACILITY_DETAILS_FORM_PROVIDER = new InjectionToken<EditFacilityDetailsFormModel>(
  'Facility details form provider',
);

export const editFacilityDetailsFormProvider = (): Provider => {
  return {
    provide: EDIT_FACILITY_DETAILS_FORM_PROVIDER,
    deps: [FormBuilder, ActivatedRoute],
    useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute) => {
      const facilityDetails = activatedRoute.snapshot.data.facilityDetails as FacilityDataDetailsDTO;

      const group = fb.group({
        hasFacilityExited: fb.control(!!facilityDetails?.schemeExitDate),
        schemeExitDate: fb.control(facilityDetails?.schemeExitDate ? new Date(facilityDetails?.schemeExitDate) : null, [
          GovukValidators.required('Enter a date'),
        ]),
      });

      group.controls.hasFacilityExited.valueChanges.pipe(takeUntilDestroyed()).subscribe((value) => {
        if (value) {
          group.controls.schemeExitDate.enable();
        } else {
          group.controls.schemeExitDate.disable();
          group.controls.schemeExitDate.reset();
        }
      });

      return group;
    },
  };
};
