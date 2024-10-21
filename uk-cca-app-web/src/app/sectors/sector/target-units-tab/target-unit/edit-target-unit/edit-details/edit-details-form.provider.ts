import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { SectorAssociationSchemeDTO, TargetUnitAccountPayload } from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import {
  createTargetUnitAccountDetailsForm,
  TargetUnitCreationFormModel,
} from '../../../common/components/target-unit-details-input/target-unit-details-input-controls';

export const EDIT_TARGET_UNIT_DETAILS_FORM = new InjectionToken<TargetUnitCreationFormModel>(
  'Edit Target Unit Details Form',
);

export const EditDetailsFormProvider: Provider = {
  provide: EDIT_TARGET_UNIT_DETAILS_FORM,
  deps: [FormBuilder, ActiveTargetUnitStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: ActiveTargetUnitStore, route: ActivatedRoute) => {
    const subSectors = (route.snapshot.data?.subSectorScheme as SectorAssociationSchemeDTO)
      ?.subsectorAssociationSchemes;
    const accountDetails = store.state.targetUnitAccountDetails;

    return createTargetUnitAccountDetailsForm(fb, accountDetails as TargetUnitAccountPayload, subSectors ?? [], true);
  },
};
