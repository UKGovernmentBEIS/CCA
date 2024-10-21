import { inject, Injector } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { combineLatest, map, of, switchMap } from 'rxjs';

import { RegulatorAuthoritiesService } from 'cca-api';

import { DetailsStore } from './details.store';
import { fetchPermissionGroupLevelsAndUpdateStore, fetchRegulatorRolesAndUpdateStore } from './guard.utils';

export const CanAddUsers: CanActivateFn = () => {
  const injector = inject(Injector);
  const router = inject(Router);
  const store = inject(DetailsStore);

  return inject(RegulatorAuthoritiesService)
    .getCurrentRegulatorUserPermissionsByCa()
    .pipe(
      switchMap((v) => {
        const allowAdd = v.permissions['MANAGE_USERS_AND_CONTACTS'] === 'EXECUTE';

        if (!allowAdd) return of(router.createUrlTree(['dashboard']));

        store.setState({
          isAdd: true,
          isEditable: true,
        });

        return combineLatest([
          fetchRegulatorRolesAndUpdateStore(injector),
          fetchPermissionGroupLevelsAndUpdateStore(injector),
        ]).pipe(map(() => true));
      }),
    );
};
