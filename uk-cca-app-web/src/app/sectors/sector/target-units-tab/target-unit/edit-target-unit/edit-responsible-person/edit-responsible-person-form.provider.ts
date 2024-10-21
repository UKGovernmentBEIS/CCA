import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { createAccountAddressForm } from '@shared/components';
import {
  createResponsibleForm,
  ResponsiblePersonFormConfig,
} from '@shared/components/responsible-person-input/responsible-person-input.controls';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';

export const EDIT_TARGET_UNIT_RESPONSIBLE_PERSON_FORM = new InjectionToken<string>(
  'Edit Target Unit Responsible person Form',
);

export const EditResponsiblePersonFormProvider: Provider = {
  provide: EDIT_TARGET_UNIT_RESPONSIBLE_PERSON_FORM,
  deps: [FormBuilder, ActiveTargetUnitStore],
  useFactory: (fb: FormBuilder, store: ActiveTargetUnitStore) => {
    const responsiblePersonPayload = store.state.targetUnitAccountDetails.responsiblePerson;
    const addressFormGroup = createAccountAddressForm(responsiblePersonPayload?.address);
    const formConfig: ResponsiblePersonFormConfig = {
      email: { value: responsiblePersonPayload?.email ?? null, disabled: true },
      firstName: { value: responsiblePersonPayload?.firstName ?? null, disabled: true },
      lastName: { value: responsiblePersonPayload?.lastName ?? null, disabled: true },
      jobTitle: { value: responsiblePersonPayload?.jobTitle ?? null, disabled: false },
      phoneNumber: { value: responsiblePersonPayload?.phoneNumber ?? null, disabled: false },
      address: { value: addressFormGroup, disabled: true },
      sameAddress: { value: [false], disabled: false },
    };

    return createResponsibleForm(fb, formConfig, false, true);
  },
};
