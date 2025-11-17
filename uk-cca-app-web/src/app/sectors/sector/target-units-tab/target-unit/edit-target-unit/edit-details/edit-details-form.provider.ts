import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { SectorAssociationSchemesDTO } from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { TargetUnitCreationFormModel } from '../../../common/types';
import { addSicCodeFormControl } from '../../../common/utils';
export const EDIT_TARGET_UNIT_DETAILS_FORM = new InjectionToken<TargetUnitCreationFormModel>(
  'Edit Target Unit Details Form',
);

export const EditDetailsFormProvider: Provider = {
  provide: EDIT_TARGET_UNIT_DETAILS_FORM,
  deps: [FormBuilder, ActiveTargetUnitStore, ActivatedRoute],
  useFactory: (fb: FormBuilder, store: ActiveTargetUnitStore, route: ActivatedRoute) => {
    const subSectors =
      (route.snapshot.data?.subSectorScheme as SectorAssociationSchemesDTO)?.subsectorAssociations || [];

    const accountDetails = store.state.targetUnitAccountDetails;

    const sicCodes = accountDetails?.sicCodes ?? [];

    const sicCodeFormControls =
      sicCodes.length > 0 ? sicCodes.map((code) => addSicCodeFormControl(code)) : [addSicCodeFormControl()];

    const group = fb.group<TargetUnitCreationFormModel>(
      {
        operatorType: fb.control({ value: accountDetails?.operatorType ?? null, disabled: true }),
        name: fb.control({ value: accountDetails?.name ?? null, disabled: true }),
        sicCodes: fb.array(sicCodeFormControls),
      },
      { updateOn: 'change' },
    );

    if (subSectors.length > 0) {
      group.addControl(
        'subsectorAssociationId',
        fb.control({ value: accountDetails?.subsectorAssociationId ?? null, disabled: true }),
      );
    } else {
      group.removeControl('subsectorAssociationId');
    }

    return group;
  },
};
