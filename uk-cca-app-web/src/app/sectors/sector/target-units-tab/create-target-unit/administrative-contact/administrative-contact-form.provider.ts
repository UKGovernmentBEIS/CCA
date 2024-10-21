import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';

import {
  AdministrativeContactDetailsFormModel,
  createAdministrativeForm,
} from '../../common/components/administrative-contact-input/administrative-contact-input-controls';
import { CreateTargetUnitStore } from '../create-target-unit.store';

export const TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM = new InjectionToken<AdministrativeContactDetailsFormModel>(
  'Target Unit Administrative Contact Form',
);

export const TargetUnitAdministrativeContactFormProvider: Provider = {
  provide: TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  deps: [FormBuilder, CreateTargetUnitStore, DestroyRef],
  useFactory: (fb: FormBuilder, store: CreateTargetUnitStore, destroyRef: DestroyRef) => {
    const responsiblePersonAdressPayload = store.state.responsiblePerson.address;
    const administrativeContactDetails = store.state.administrativeContactDetails;

    const group = createAdministrativeForm(fb, administrativeContactDetails, store.sameAddressWithResponsiblePerson);

    group.controls.sameAddress.valueChanges.pipe(takeUntilDestroyed(destroyRef)).subscribe((isSameAddress) => {
      if (isSameAddress[0]) {
        group.controls.address.setValue(responsiblePersonAdressPayload);
        group.controls.address.disable();
      } else {
        group.controls.address.reset();
        group.controls.address.enable();
      }
    });

    return group;
  },
};
