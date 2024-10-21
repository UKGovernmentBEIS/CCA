import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';

import { underlyingAgreementQuery } from '../../+state';

export const canActivateFacilityItem: CanActivateFn = (route) => {
  const requestTaskStore = inject(RequestTaskStore);

  const isEditable = requestTaskStore.select(requestTaskQuery.selectIsEditable)();
  const facilities = requestTaskStore.select(underlyingAgreementQuery.selectManageFacilities)()?.facilityItems;
  const facilityId = route.params.facilityId;

  return (
    (facilityId && facilities.findIndex((f) => f.facilityId === facilityId) !== -1 && isEditable) ||
    createUrlTreeFromSnapshot(route, ['../../'])
  );
};
