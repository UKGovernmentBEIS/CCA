import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';

import { TargetPeriod5Details } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';

export type BaselineExistsFormModel = FormGroup<{
  exist: FormControl<TargetPeriod5Details['exist']>;
}>;

export const BASELINE_EXISTS_FORM = new InjectionToken<BaselineExistsFormModel>('Baseline exists form');

export const BaselineExistsFormProvider: Provider = {
  provide: BASELINE_EXISTS_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, requestTaskStore: RequestTaskStore) => {
    const exists = requestTaskStore.select(underlyingAgreementQuery.selectTargetPeriodExists)();
    return fb.group({
      exist: fb.control(exists ?? null, GovukValidators.required('Please select an option')),
    });
  },
};
