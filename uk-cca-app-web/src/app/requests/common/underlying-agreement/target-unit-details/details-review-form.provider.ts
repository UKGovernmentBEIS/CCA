import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { GovukValidators } from '@netz/govuk-components';
import { underlyingAgreementQuery } from '@requests/common';
import { textFieldValidators } from '@shared/validators';

import { UnderlyingAgreementTargetUnitDetails } from 'cca-api';

export const TARGET_UNIT_DETAILS_REVIEW_FORM = new InjectionToken<TargetUnitDetailsReviewFormModel>(
  'Edit Target Unit Details Review Form',
);

export type TargetUnitDetailsReviewFormModel = {
  operatorName: FormControl<UnderlyingAgreementTargetUnitDetails['operatorName'] | null>;
  operatorType: FormControl<'LIMITED_COMPANY' | 'PARTNERSHIP' | 'SOLE_TRADER' | 'NONE'>;
  subsectorAssociationId?: FormControl<number | null>;
};

export const TargetUnitDetailsReviewFormProvider = {
  provide: TARGET_UNIT_DETAILS_REVIEW_FORM,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const targetUnitDetails = store.select(underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails)();

    const group = fb.group<TargetUnitDetailsReviewFormModel>({
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
