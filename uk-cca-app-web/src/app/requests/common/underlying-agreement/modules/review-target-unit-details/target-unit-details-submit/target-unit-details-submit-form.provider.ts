import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { underlyingAgreementQuery } from '../../../+state';

export const TARGET_UNIT_DETAILS_SUBMIT_FORM = new InjectionToken<TargetUnitDetailsSubmitFormModel>(
  'Edit Target Unit Details Submit Form',
);

export type TargetUnitDetailsSubmitFormModel = {
  operatorName: FormControl<UnderlyingAgreementTargetUnitDetails['operatorName'] | null>;
  operatorType: FormControl<'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'NONE'>;
  companyRegistrationNumber: FormControl<string | null>;
  subsectorAssociationName?: FormControl<string | null>;
};

export const TargetUnitDetailsSubmitFormProvider = {
  provide: TARGET_UNIT_DETAILS_SUBMIT_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const updatePayload = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();
    const readonlyPayload = store.select(underlyingAgreementQuery.selectAccountReferenceData)();
    return fb.group<TargetUnitDetailsSubmitFormModel>({
      operatorName: fb.control({
        value: updatePayload.operatorName,
        disabled: false,
      }),
      operatorType: fb.control({
        value: readonlyPayload?.targetUnitAccountDetails?.operatorType,
        disabled: true,
      }),
      companyRegistrationNumber: fb.control({
        value: readonlyPayload?.targetUnitAccountDetails?.companyRegistrationNumber,
        disabled: true,
      }),
      subsectorAssociationName: fb.control({
        value: readonlyPayload?.sectorAssociationDetails?.subsectorAssociationName,
        disabled: true,
      }),
    });
  },
};
