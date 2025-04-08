import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { textFieldValidators } from '@shared/validators';

import { underlyingAgreementQuery } from '../../../+state';

export type FacilityItemFormModel = {
  name: FormControl<string>;
  facilityId: FormControl<string>;
  status: FormControl<string>;
};

export const ADD_FACILITY_FORM = new InjectionToken<FacilityItemFormModel>('Add Facility Form');

export const FacilityItemFormProvider: Provider = {
  provide: ADD_FACILITY_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute, requestTaskStore: RequestTaskStore) => {
    const facilityId = activatedRoute.snapshot.params?.facilityId;
    const facility = requestTaskStore.select(underlyingAgreementQuery.selectFacility(facilityId))();
    const name = facility?.facilityDetails.name;

    return fb.group<FacilityItemFormModel>({
      name: fb.control(name, textFieldValidators('site name')),
      facilityId: fb.control(facility?.facilityId),
      status: fb.control(facility?.status),
    });
  },
};
