import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { textFieldValidators } from '@shared/validators';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

import { underlyingAgreementQuery } from '../+state';

export const TARGET_UNIT_DETAILS_SUBMIT_FORM = new InjectionToken<TargetUnitDetailsSubmitFormModel>(
  'Edit Target Unit Details Submit Form',
);

export type TargetUnitDetailsSubmitFormModel = {
  operatorName: FormControl<UnderlyingAgreementTargetUnitDetails['operatorName'] | null>;
  operatorType: FormControl<'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'NONE'>;
  subsectorAssociationId?: FormControl<number | null>;
};

export const TargetUnitDetailsSubmitFormProvider = {
  provide: TARGET_UNIT_DETAILS_SUBMIT_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const targetUnitDetails = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();

    const group = fb.group<TargetUnitDetailsSubmitFormModel>({
      operatorName: fb.control(targetUnitDetails.operatorName, textFieldValidators('operator name')),
      operatorType: fb.control(
        targetUnitDetails.operatorType,
        GovukValidators.required('You must select an operator type'),
      ),
    });

    if (targetUnitDetails.subsectorAssociationId) {
      group.addControl(
        'subsectorAssociationId',
        fb.control(targetUnitDetails.subsectorAssociationId, [GovukValidators.required('You must select a subsector')]),
      );
    } else {
      group.removeControl('subsectorAssociationId');
    }

    return group;
  },
};
