import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, createUrlTreeFromSnapshot, UrlTree } from '@angular/router';

import { SectorAssociationSchemesDTO } from 'cca-api';

import { CreateTargetUnitStore } from './create-target-unit.store';
import { isTargetUnitDetailsCompleted, isWizardCompleted } from './create-target-unit.wizard';

export const CanActivateTargetUnitCreationStep: CanActivateFn = (route): boolean | UrlTree => {
  const change = route.queryParamMap.get('change') === 'true';
  const payload = inject(CreateTargetUnitStore).state;
  const subSectorsExist =
    (route.data.subSectorScheme as SectorAssociationSchemesDTO)?.subsectorAssociations?.length > 0;

  if (change && isWizardCompleted(payload, subSectorsExist)) return true;
  if (!change && isWizardCompleted(payload, subSectorsExist)) return createUrlTreeFromSnapshot(route, ['../summary']);
  if (isTargetUnitDetailsCompleted(payload, subSectorsExist)) return true;
  return createUrlTreeFromSnapshot(route, ['../company-registration-number']);
};

export const CanActivateTargetUnitCreationSummary: CanActivateFn = (route): boolean | UrlTree => {
  const payload = inject(CreateTargetUnitStore).state;
  const subSectorsExist =
    (route.data.subSectorScheme as SectorAssociationSchemesDTO)?.subsectorAssociations?.length > 0;

  return isWizardCompleted(payload, subSectorsExist) || createUrlTreeFromSnapshot(route, ['../']);
};

export const ResetCreateUnitAccountStore: CanDeactivateFn<boolean> = () => {
  inject(CreateTargetUnitStore).reset();
  return true;
};
