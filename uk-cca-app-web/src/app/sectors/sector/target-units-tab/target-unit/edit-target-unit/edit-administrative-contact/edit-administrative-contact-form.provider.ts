import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import {
  AdministrativeContactDetailsFormModel,
  createAdministrativeForm,
} from '../../../common/components/administrative-contact-input/administrative-contact-input-controls';

export const EDIT_TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM = new InjectionToken<AdministrativeContactDetailsFormModel>(
  'Edit Target Unit Administrative Contact Form',
);

export const EditAdministrativeContactFormProvider: Provider = {
  provide: EDIT_TARGET_UNIT_ADMINISTRATIVE_CONTACT_FORM,
  deps: [FormBuilder, ActiveTargetUnitStore],
  useFactory: (fb: FormBuilder, store: ActiveTargetUnitStore) => {
    const administrativeContactDetails = store.state.targetUnitAccountDetails.administrativeContactDetails;

    return createAdministrativeForm(fb, administrativeContactDetails, false, true);
  },
};
