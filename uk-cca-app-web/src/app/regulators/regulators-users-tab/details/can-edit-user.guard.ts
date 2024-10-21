import { inject, Injector } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { combineLatest, map, Observable, of, switchMap } from 'rxjs';

import { AuthStore, selectUserId } from '@netz/common/auth';

import { RegulatorAuthoritiesService } from 'cca-api';

import { DetailsStore } from './details.store';
import {
  fetchPermissionGroupLevelsAndUpdateStore,
  fetchRegulatorRolesAndUpdateStore,
  fetchUserDetailsAndUpdateStore,
  fetchUserPermissions,
} from './guard.utils';

export const CanEditUserGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const injector = inject(Injector);
  const store = inject(DetailsStore);
  const router = inject(Router);
  const routeUserId = route.paramMap.get('userId');
  const userId = inject(AuthStore).select(selectUserId);
  const isCurrentUser = userId() === routeUserId;

  return inject(RegulatorAuthoritiesService)
    .getCurrentRegulatorUserPermissionsByCa()
    .pipe(
      switchMap((v) => {
        const allowEdit = v.permissions['MANAGE_USERS_AND_CONTACTS'] === 'EXECUTE';

        if (!allowEdit && !isCurrentUser) {
          return of(router.createUrlTree(['dashboard']));
        }

        const op$: Observable<unknown>[] = [
          fetchUserDetailsAndUpdateStore(isCurrentUser, routeUserId, injector),
          fetchUserPermissions(isCurrentUser, routeUserId, injector),
        ];

        if (allowEdit) {
          store.setState({ isAdd: false, isEditable: true });
          op$.push(fetchRegulatorRolesAndUpdateStore(injector), fetchPermissionGroupLevelsAndUpdateStore(injector));
        } else {
          store.setState({ isAdd: false, isEditable: false });
        }

        return combineLatest(op$).pipe(map(() => true));
      }),
    );
};

export function ResetRegulatorDetails() {
  const store = inject(DetailsStore);
  store.reset();
}
