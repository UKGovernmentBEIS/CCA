import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup } from '@angular/forms';

import { RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';
import {
  createAccountAddressForm,
  createResponsibleForm,
  ResponsiblePersonFormConfig,
  ResponsiblePersonFormModel,
} from '@shared/components';

export const UNA_RESPONSIBLE_PERSON_FORM = new InjectionToken<FormGroup<ResponsiblePersonFormModel>>(
  'Target Unit Responsible Person Form',
);

export const UnaTargetUnitResponsiblePersonFormProvider: Provider = {
  provide: UNA_RESPONSIBLE_PERSON_FORM,
  deps: [FormBuilder, RequestTaskStore, DestroyRef],
  useFactory: (fb: FormBuilder, store: RequestTaskStore, destroyRef: DestroyRef) => {
    const payload = store.select(underlyingAgreementQuery.selectPayload)();

    if (!payload.underlyingAgreement?.underlyingAgreementTargetUnitDetails) {
      throw new Error('Target unit details data is missing from payload');
    }

    const accountData = payload.accountReferenceData.targetUnitAccountDetails;
    const targetUnitDetails = payload.underlyingAgreement.underlyingAgreementTargetUnitDetails;

    const addressPayload = accountData.address;
    const readonlyPayload = accountData.responsiblePerson;
    const updatePayload = targetUnitDetails.responsiblePersonDetails;

    const addressFormGroup = createAccountAddressForm(updatePayload.address);

    const formConfig: ResponsiblePersonFormConfig = {
      email: { value: updatePayload?.email ?? null, disabled: false },
      firstName: { value: updatePayload?.firstName ?? null, disabled: false },
      lastName: { value: updatePayload?.lastName ?? null, disabled: false },
      jobTitle: { value: readonlyPayload?.jobTitle ?? null, disabled: true },
      phoneNumber: { value: readonlyPayload?.phoneNumber ?? null, disabled: true },
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
