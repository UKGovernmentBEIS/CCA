import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { underlyingAgreementQuery } from '../../+state/underlying-agreement.selectors';
import { BaselineEnergyDraftService } from './baseline-energy-draft.service';

/**
 * Seeds the route-scoped draft from the store when the baseline energy consumption routes activate, so a
 * child page opened directly (add, delete, exclude or undo product) finds an initialised draft.
 *
 * The draft is reset here rather than cleared on leaving: the route providers outlive the routes, and a
 * clearing `canDeactivate` would leave a `null` draft behind if a later guard cancelled the navigation.
 * Moving between the child pages does not re-run this guard, so the draft survives them.
 */
export const initializeBaselineEnergyDraftGuard: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);
  const draftService = inject(BaselineEnergyDraftService);
  const facilityId = route.params.facilityId;
  const una = requestTaskStore.select(underlyingAgreementQuery.selectUnderlyingAgreement)();
  const facilityIndex = una?.facilities?.findIndex((facility) => facility.facilityId === facilityId) ?? -1;

  draftService.clear();
  draftService.initializeFromStore(
    requestTaskStore.select(underlyingAgreementQuery.selectFacilityBaselineEnergyConsumption(facilityIndex))(),
  );

  return true;
};
