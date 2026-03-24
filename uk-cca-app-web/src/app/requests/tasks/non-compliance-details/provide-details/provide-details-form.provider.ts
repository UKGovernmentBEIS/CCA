import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { NonComplianceDetails } from 'cca-api';

import { nonComplianceDetailsQuery } from '../non-compliance-details.selectors';

export type ProvideDetailsFormModel = FormGroup<{
  nonComplianceType: FormControl<NonComplianceDetails['nonComplianceType']>;
  nonCompliantDate: FormControl<NonComplianceDetails['nonCompliantDate']>;
  compliantDate: FormControl<NonComplianceDetails['compliantDate']>;
  comment: FormControl<string>;
}>;

export const PROVIDE_DETAILS_FORM = new InjectionToken<ProvideDetailsFormModel>('Provide details form');

export const ProvideDetailsFormProvider: Provider = {
  provide: PROVIDE_DETAILS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const nonComplianceDetails = store.select(nonComplianceDetailsQuery.selectNonComplianceDetails)();

    return fb.group({
      nonComplianceType: fb.control(nonComplianceDetails?.nonComplianceType ?? null, [
        GovukValidators.required('You must select a type'),
      ]),
      nonCompliantDate: fb.control(
        nonComplianceDetails?.nonCompliantDate ? new Date(nonComplianceDetails.nonCompliantDate) : null,
      ),
      compliantDate: fb.control(
        nonComplianceDetails?.compliantDate ? new Date(nonComplianceDetails.compliantDate) : null,
      ),

      comment: fb.control(nonComplianceDetails?.comment ?? null),
    });
  },
};
