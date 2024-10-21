import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn, Router } from '@angular/router';

import { catchError, map, of } from 'rxjs';

import { RequestActionStore } from '@netz/common/store';

import { RequestActionsService } from 'cca-api';

export function getRequestActionPageCanActivateGuard(actionIdParam = 'actionId'): CanActivateFn {
  return (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const store = inject(RequestActionStore);
    const service = inject(RequestActionsService);

    const id = +route.paramMap.get(actionIdParam);
    if (!route.paramMap.has(actionIdParam) || Number.isNaN(id)) {
      console.warn(`No :${actionIdParam} param in route`);
      return true;
    }

    return service.getRequestActionById(id).pipe(
      map((action) => {
        store.setAction(action);
        return true;
      }),
      catchError(() => {
        return of(router.createUrlTree(['dashboard']));
      }),
    );
  };
}

export function getRequestActionPageCanDeactivateGuard(): CanDeactivateFn<unknown> {
  return () => {
    inject(RequestActionStore).reset();
    return true;
  };
}
