import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';
import {
  createAccountAddressForm,
  createResponsibleForm,
  ResponsiblePersonFormConfig,
  ResponsiblePersonFormModel,
} from '@shared/components';

export const RESPONSIBLE_PERSON_FORM = new InjectionToken<ResponsiblePersonFormModel>(
  'Target Unit Responsible Person Form',
);

export const ResponsiblePersonFormProvider: Provider = {
  provide: RESPONSIBLE_PERSON_FORM,
  deps: [FormBuilder, RequestTaskStore, DestroyRef],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, destroyRef: DestroyRef) => {
    const addressPayload = store.select(underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails)().address;

    const responsiblePersonAddress = store.select(
      underlyingAgreementQuery.selectAccountReferenceDataTargetUnitDetails,
    )().responsiblePerson;

    const responsiblePersonDetailsPayload = store.select(
      underlyingAgreementQuery.selectUnderlyingAgreementTargetUnitDetails,
    )().responsiblePersonDetails;

    const addressFormGroup = createAccountAddressForm(responsiblePersonDetailsPayload.address);

    const formConfig: ResponsiblePersonFormConfig = {
      email: { value: responsiblePersonDetailsPayload?.email ?? null, disabled: false },
      firstName: { value: responsiblePersonDetailsPayload?.firstName ?? null, disabled: false },
      lastName: { value: responsiblePersonDetailsPayload?.lastName ?? null, disabled: false },
      jobTitle: { value: responsiblePersonAddress?.jobTitle ?? null, disabled: true },
      phoneNumber: { value: responsiblePersonAddress?.phoneNumber ?? null, disabled: true },
      address: { value: addressFormGroup, disabled: false },
      sameAddress: { value: [false], disabled: false },
    };

    const group = createResponsibleForm(fb, formConfig, true, false);

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.address.setValue({
          ...addressPayload,
          line2: addressPayload.line2 ?? null,
          county: addressPayload.county ?? null,
        });

        group.controls.address.disable();
      } else {
        group.controls.address.enable();
      }
    });

    return group;
  },
};
