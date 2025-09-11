import { InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from '@netz/govuk-components';
import { textFieldValidators } from '@shared/validators';

import { SectorAssociationSchemesDTO } from 'cca-api';

import { TargetUnitCreationFormModel } from '../common/types';
import { addSicCodeFormControl } from '../common/utils';
import { CreateTargetUnitStore } from './create-target-unit.store';

export const TARGET_UNIT_CREATION_FORM = new InjectionToken<TargetUnitCreationFormModel>('Target Unit Creation Form');

export const TargetUnitCreationFormProvider: Provider = {
  provide: TARGET_UNIT_CREATION_FORM,
  deps: [FormBuilder, ActivatedRoute, CreateTargetUnitStore],
  useFactory: (fb: FormBuilder, activatedRoute: ActivatedRoute, createTargetUnitStore: CreateTargetUnitStore) => {
    const subSectors = (activatedRoute.snapshot.data.subSectorScheme as SectorAssociationSchemesDTO)
      .subsectorAssociations;

    const targetUnitAccountDetails = createTargetUnitStore.state;
    const sicCodes = targetUnitAccountDetails?.sicCodes ?? [];

    const sicCodeFormControls =
      sicCodes.length > 0 ? sicCodes.map((code) => addSicCodeFormControl(code)) : [addSicCodeFormControl()];

    const group = fb.group<TargetUnitCreationFormModel>(
      {
        operatorType: fb.control(targetUnitAccountDetails?.operatorType ?? null, [
          GovukValidators.required('You must select an operator type'),
        ]),
        name: fb.control(targetUnitAccountDetails?.name ?? null, textFieldValidators('operator name')),
        isCompanyRegistrationNumber: fb.control(targetUnitAccountDetails?.isCompanyRegistrationNumber ?? null, [
          GovukValidators.required('You must select an option'),
        ]),
        companyRegistrationNumber: fb.control(
          targetUnitAccountDetails?.companyRegistrationNumber ?? null,
          textFieldValidators('registration number'),
        ),
        registrationNumberMissingReason: fb.control(
          targetUnitAccountDetails?.registrationNumberMissingReason ?? null,
          textFieldValidators('reason for not having registration number'),
        ),
        sicCodes: fb.array(sicCodeFormControls),
      },
      { updateOn: 'change' },
    );

    if (subSectors.length > 0) {
      group.addControl(
        'subsectorAssociationId',
        fb.control(
          targetUnitAccountDetails?.subsectorAssociationId ?? null,
          subSectors.length ? [GovukValidators.required('You must select a subsector')] : null,
        ),
      );
    } else {
      group.removeControl('subsectorAssociationId');
    }

    group.controls.isCompanyRegistrationNumber.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe((hasRegistrationNumber: boolean) => {
        if (hasRegistrationNumber) {
          group.controls.companyRegistrationNumber.enable();

          group.controls.registrationNumberMissingReason.reset();
          group.controls.registrationNumberMissingReason.disable();
        } else {
          group.controls.registrationNumberMissingReason.enable();

          group.controls.companyRegistrationNumber.reset();
          group.controls.companyRegistrationNumber.disable();
        }
      });

    return group;
  },
};
