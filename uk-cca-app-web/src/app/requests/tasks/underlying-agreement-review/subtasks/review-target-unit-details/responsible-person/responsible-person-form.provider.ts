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

    const targetUnitDetails = payload.underlyingAgreement.underlyingAgreementTargetUnitDetails;
    if (!targetUnitDetails) {
      throw new Error('Target unit details data is missing from payload');
    }

    const accountReferenceData = payload.accountReferenceData.targetUnitAccountDetails;

    const operatorAddress = targetUnitDetails.operatorAddress;
    const readonlyResponsiblePerson = accountReferenceData.responsiblePerson;
    const updatedResponsiblePerson = targetUnitDetails.responsiblePersonDetails;

    const addressFormGroup = createAccountAddressForm(updatedResponsiblePerson.address);

    const formConfig: ResponsiblePersonFormConfig = {
      email: { value: updatedResponsiblePerson?.email ?? null, disabled: false },
      firstName: { value: updatedResponsiblePerson?.firstName ?? null, disabled: false },
      lastName: { value: updatedResponsiblePerson?.lastName ?? null, disabled: false },
      jobTitle: { value: readonlyResponsiblePerson?.jobTitle ?? null, disabled: true },
      phoneNumber: { value: readonlyResponsiblePerson?.phoneNumber ?? null, disabled: true },
      address: { value: addressFormGroup, disabled: false },
      sameAddress: { value: [false], disabled: false },
    };

    const group = createResponsibleForm(fb, formConfig, true, false);

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.address.setValue({
          ...operatorAddress,
          line2: operatorAddress.line2 ?? null,
          county: operatorAddress.county ?? null,
        });

        group.controls.address.disable();
      } else {
        group.controls.address.enable();
      }
    });

    return group;
  },
};
