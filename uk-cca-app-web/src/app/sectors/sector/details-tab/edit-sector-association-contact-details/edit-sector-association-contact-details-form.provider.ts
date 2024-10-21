import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { AddressFormModel, createCountyAddressControl } from '@shared/components';
import { CCAGovukValidators, textFieldValidators } from '@shared/validators/validators';

import { ActiveSectorStore } from '../../active-sector.store';

export type SectorAssociationContactDetailsFormModel = {
  title: FormControl<string>;
  firstName: FormControl<string>;
  lastName: FormControl<string>;
  jobTitle: FormControl<string>;
  organisationName: FormControl<string>;
  address: FormGroup<AddressFormModel>;
  phoneNumber: FormControl<string>;
  email: FormControl<string>;
};

export const SECTOR_ASSOCIATION_CONTACT_DETAILS_FORM = new InjectionToken<
  FormGroup<SectorAssociationContactDetailsFormModel>
>('Sector Association Contact Details Form');

export const SectorAssociationContactDetailsFormProvider: Provider = {
  provide: SECTOR_ASSOCIATION_CONTACT_DETAILS_FORM,
  deps: [FormBuilder, ActivatedRoute, ActiveSectorStore],
  useFactory: (fb: FormBuilder, route: ActivatedRoute, store: ActiveSectorStore) => {
    const sectorAssociationContact = store.state.sectorAssociationContact;

    const addressFormGroup = createCountyAddressControl(sectorAssociationContact.address);

    return fb.group(
      {
        title: fb.control(
          sectorAssociationContact.title,
          GovukValidators.maxLength(20, 'The Title should not be more than 20 characters'),
        ),
        firstName: fb.control(sectorAssociationContact.firstName, textFieldValidators('first name')),
        lastName: fb.control(sectorAssociationContact.lastName, textFieldValidators('last name')),
        email: fb.control(sectorAssociationContact.email, [
          ...textFieldValidators('email address'),
          CCAGovukValidators.email(),
        ]),
        jobTitle: fb.control(sectorAssociationContact.jobTitle, [CCAGovukValidators.maxLength('Job title')]),
        organisationName: fb.control(sectorAssociationContact.organisationName),
        address: addressFormGroup,
        phoneNumber: fb.control(sectorAssociationContact.phoneNumber),
      },
      { updateOn: 'submit' },
    );
  },
};
