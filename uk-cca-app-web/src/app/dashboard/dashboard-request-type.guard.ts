import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

import { getWorkflowFilterOptions } from './+store';

export const dashboardRequestTypeGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const requestType = route.queryParamMap.get('requestType');
  if (!requestType) return true;

  const roleType = inject(AuthStore).select(selectUserRoleType)();
  const requestTypeIsAllowed = getWorkflowFilterOptions(roleType).some(({ value }) => value === requestType);
  if (requestTypeIsAllowed) return true;

  return inject(Router).createUrlTree(['/dashboard'], {
    queryParams: { ...route.queryParams, requestType: null },
    fragment: route.fragment,
  });
};
