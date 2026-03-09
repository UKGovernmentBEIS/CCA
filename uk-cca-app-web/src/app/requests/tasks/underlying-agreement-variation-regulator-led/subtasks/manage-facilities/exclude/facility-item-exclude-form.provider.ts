import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery } from '@requests/common';
import { futureDateValidator } from '@shared/validators';

export type FacilityItemExcludeFormModel = {
  excludedDate: FormControl<Date>;
};

export const EXCLUDE_FACILITY_FORM = new InjectionToken<FacilityItemExcludeFormModel>('Exclude Facility Form');

export const FacilityItemExcludeFormProvider: Provider = {
  provide: EXCLUDE_FACILITY_FORM,
  deps: [FormBuilder, ActivatedRoute, RequestTaskStore],
  useFactory: (fb: FormBuilder, route: ActivatedRoute, store: RequestTaskStore) => {
    const facilityId = route.snapshot.params?.facilityId;
    const facility = store.select(underlyingAgreementQuery.selectFacility(facilityId))();
    const excludedDate = facility?.excludedDate;

    return fb.group<FacilityItemExcludeFormModel>({
      excludedDate: fb.control(excludedDate ? new Date(excludedDate) : null, {
        validators: [
          GovukValidators.required('Select a date'),
          futureDateValidator('The exclusion date can not be a future date'),
        ],
      }),
    });
  },
};
