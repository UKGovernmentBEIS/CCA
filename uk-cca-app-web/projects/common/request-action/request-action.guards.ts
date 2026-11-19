import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  CanDeactivateFn,
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  Router,
} from '@angular/router';

import { catchError, filter, map, of, take } from 'rxjs';

import { RequestActionStore } from '@netz/common/store';

import { RequestActionsService } from 'cca-api';

export function getRequestActionPageCanActivateGuard(actionIdParam = 'actionId'): CanActivateFn {
  return (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const store = inject(RequestActionStore);
    const service = inject(RequestActionsService);

    const idParam = route.paramMap.get(actionIdParam);
    if (!idParam) {
      console.warn(`No :${actionIdParam} param in route`);
      return true;
    }
    const id = +idParam;
    if (Number.isNaN(id)) {
      console.warn(`Invalid :${actionIdParam} param in route`);
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
    const store = inject(RequestActionStore);
    const router = inject(Router);
    const leavingAction = store.state.action;
    const navigationId = router.currentNavigation()?.id;

    router.events
      .pipe(
        filter(
          (event) =>
            (event instanceof NavigationEnd || event instanceof NavigationCancel || event instanceof NavigationError) &&
            event.id === navigationId,
        ),
        take(1),
      )
      .subscribe((event) => {
        // Keep the displayed action on cancellation, and never clear an action loaded by the destination.
        if (event instanceof NavigationEnd && store.state.action === leavingAction) store.reset();
      });
    return true;
  };
}
