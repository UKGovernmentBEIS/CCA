import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';

import {
  createAccountAddressForm,
  createResponsibleForm,
  ResponsiblePersonFormConfig,
  ResponsiblePersonFormModel,
} from '@shared/components';

import { CreateTargetUnitStore } from '../create-target-unit.store';

export const TARGET_UNIT_RESPONSIBLE_PERSON_FORM = new InjectionToken<ResponsiblePersonFormModel>(
  'Target Unit Responsible Person Form',
);

export const TargetUnitResponsiblePersonFormProvider: Provider = {
  provide: TARGET_UNIT_RESPONSIBLE_PERSON_FORM,
  deps: [FormBuilder, CreateTargetUnitStore],
  useFactory: (fb: FormBuilder, createTargetUnitStore: CreateTargetUnitStore) => {
    const addressPayload = createTargetUnitStore.state.address;
    const responsiblePersonPayload = createTargetUnitStore.state.responsiblePerson;
    const addressFormGroup = createAccountAddressForm(responsiblePersonPayload?.address);

    const formConfig: ResponsiblePersonFormConfig = {
      email: { value: responsiblePersonPayload?.email ?? null, disabled: false },
      firstName: { value: responsiblePersonPayload?.firstName ?? null, disabled: false },
      lastName: { value: responsiblePersonPayload?.lastName ?? null, disabled: false },
      jobTitle: { value: responsiblePersonPayload?.jobTitle ?? null, disabled: false },
      phoneNumber: { value: responsiblePersonPayload?.phoneNumber ?? null, disabled: false },
      address: { value: addressFormGroup, disabled: createTargetUnitStore.sameAddressWithOperator || false },
      sameAddress: { value: [createTargetUnitStore.sameAddressWithOperator], disabled: false },
    };

    const group = createResponsibleForm(fb, formConfig, createTargetUnitStore.sameAddressWithOperator);

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed()).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.address.setValue(addressPayload);
        group.controls.address.disable();
      } else {
        group.controls.address.reset();
        group.controls.address.enable();
      }
    });

    return group;
  },
};
