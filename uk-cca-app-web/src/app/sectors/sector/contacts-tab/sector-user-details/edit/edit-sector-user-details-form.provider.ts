import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { GovukValidators } from '@netz/govuk-components';
import { phoneInputValidators } from '@shared/components';
import { textFieldValidators } from '@shared/validators/validators';

import { ActiveSectorUserStore } from '../../active-sector-user.store';

export type PhoneNumberFormModel = {
  countryCode: FormControl<string>;
  number: FormControl<string>;
};

export type SectorUserDetailsFormModel = {
  email: FormControl<string>;
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  jobTitle?: FormControl<string>;
  phoneNumber?: FormGroup<PhoneNumberFormModel>;
  mobileNumber?: FormGroup<PhoneNumberFormModel>;
  contactType: FormControl<'SECTOR_ASSOCIATION' | 'CONSULTANT'>;
  organisationName?: FormControl<string>;
};

export const SECTOR_USER_DETAILS_FORM = new InjectionToken<SectorUserDetailsFormModel>('Sector User Details Form');

export const SectorUserDetailsFormProvider: Provider = {
  provide: SECTOR_USER_DETAILS_FORM,
  deps: [FormBuilder, ActiveSectorUserStore],
  useFactory: (fb: FormBuilder, store: ActiveSectorUserStore) => {
    const sectorUserDetails = store.state.details;

    return fb.group(
      {
        firstName: fb.control(sectorUserDetails.firstName, textFieldValidators('first name')),
        lastName: fb.control(sectorUserDetails.lastName, textFieldValidators('last name')),
        jobTitle: fb.control(sectorUserDetails.jobTitle),
        email: fb.control({ value: sectorUserDetails.email, disabled: true }),
        contactType: fb.control({ value: sectorUserDetails.contactType, disabled: !store.state.editable }, [
          GovukValidators.required('Choose your contact type'),
        ]),
        organisationName: fb.control(sectorUserDetails.organisationName),
        phoneNumber: fb.control(sectorUserDetails.phoneNumber, phoneInputValidators),
        mobileNumber: fb.control(sectorUserDetails.mobileNumber, phoneInputValidators),
      },
      {
        updateOn: 'submit',
      },
    );
  },
};
