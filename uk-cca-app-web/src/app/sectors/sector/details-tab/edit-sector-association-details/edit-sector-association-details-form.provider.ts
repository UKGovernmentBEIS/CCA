import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { AddressFormModel, createCountyAddressControl } from '@shared/components';
import { textFieldValidators } from '@shared/validators';

import { ActiveSectorStore } from '../../active-sector.store';

export type SectorAssociationDetailsFormModel = {
  commonName: FormControl<string>;
  legalName: FormControl<string>;
  noticeServiceAddress: FormGroup<AddressFormModel>;
};

export const SECTOR_ASSOCIATION_DETAILS_FORM = new InjectionToken<SectorAssociationDetailsFormModel>(
  'Sector Association Details Form',
);

export const SectorAssociationDetailsFormProvider: Provider = {
  provide: SECTOR_ASSOCIATION_DETAILS_FORM,
  deps: [FormBuilder, ActiveSectorStore],
  useFactory: (fb: FormBuilder, store: ActiveSectorStore) => {
    const sectorAssociationDetails = store.state.sectorAssociationDetails;

    const addressFormGroup = createCountyAddressControl(sectorAssociationDetails.noticeServiceAddress);

    return fb.group(
      {
        commonName: fb.control(sectorAssociationDetails.commonName, textFieldValidators('sector name')),
        legalName: fb.control(
          sectorAssociationDetails.legalName,
          textFieldValidators('sector / trade association name'),
        ),
        noticeServiceAddress: addressFormGroup,
      },
      { updateOn: 'submit' },
    );
  },
};
