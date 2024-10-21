import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { textFieldValidators } from '@shared/validators';

import { underlyingAgreementQuery } from '../../../+state';

export type FacilityItemFormModel = {
  name: FormControl<string>;
};

export const ADD_FACILITY_FORM = new InjectionToken<FacilityItemFormModel>('Add Facility Form');

export const FacilityItemFormProvider: Provider = {
  provide: ADD_FACILITY_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore],
  useFactory: (fb: FormBuilder, route: ActivatedRoute, store: RequestTaskStore) => {
    const facilityId = route.snapshot.params?.facilityId;
    const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();
    const name = facility?.facilityDetails.name;

    return fb.group<FacilityItemFormModel>({
      name: fb.control(name, textFieldValidators('site name')),
    });
  },
};
