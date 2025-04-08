import { DestroyRef, InjectionToken, Provider } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { SectorAssociationSchemeDTO } from 'cca-api';

import {
  createTargetUnitAccountDetailsForm,
  TargetUnitCreationFormModel,
} from '../common/components/target-unit-details-input/target-unit-details-input-controls';
import { CreateTargetUnitStore } from './create-target-unit.store';

export const TARGET_UNIT_CREATION_FORM = new InjectionToken<TargetUnitCreationFormModel>('Target Unit Creation Form');

export const TargetUnitCreationFormProvider: Provider = {
  provide: TARGET_UNIT_CREATION_FORM,
  deps: [FormBuilder, ActivatedRoute, CreateTargetUnitStore, DestroyRef],
  useFactory: (
    fb: FormBuilder,
    activatedRoute: ActivatedRoute,
    createTargetUnitStore: CreateTargetUnitStore,
    destroyRef: DestroyRef,
  ) => {
    const subSectors = (activatedRoute.snapshot.data.subSectorScheme as SectorAssociationSchemeDTO)
      .subsectorAssociationSchemes;

    const payload = createTargetUnitStore.state;

    const group = createTargetUnitAccountDetailsForm(fb, payload, subSectors);

    group.controls.isCompanyRegistrationNumber.valueChanges
      .pipe(takeUntilDestroyed(destroyRef))
      .subscribe((hasRegistrationNumber) => {
        if (hasRegistrationNumber) {
          group.controls.registrationNumberMissingReason.reset();
          group.controls.registrationNumberMissingReason.disable();
        } else {
          group.controls.companyRegistrationNumber.reset();
          group.controls.companyRegistrationNumber.disable();
        }
      });

    return group;
  },
};
