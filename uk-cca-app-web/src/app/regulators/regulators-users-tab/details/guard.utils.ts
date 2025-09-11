import { inject, Injector, runInInjectionContext } from '@angular/core';

import { Observable, tap } from 'rxjs';

import {
  AuthoritiesService,
  AuthorityManagePermissionDTO,
  RegulatorAuthoritiesService,
  RegulatorCurrentUserDTO,
  RegulatorUserDTO,
  RegulatorUsersService,
  UsersService,
} from 'cca-api';

import { DetailsStore } from './details.store';

type PermissionGroupLevels = Record<string, string[]>;

export function fetchUserDetailsAndUpdateStore(
  isCurrentUser: boolean,
  userId: string,
  injector: Injector,
): Observable<RegulatorUserDTO | RegulatorCurrentUserDTO> {
  return runInInjectionContext(injector, () => {
    const regulatorUsersService = inject(RegulatorUsersService);
    const usersService = inject(UsersService);
    const store = inject(DetailsStore);

    return (
      isCurrentUser
        ? (usersService.getCurrentUser() as Observable<RegulatorUserDTO | RegulatorCurrentUserDTO>)
        : regulatorUsersService.getRegulatorUserByCaAndId(userId)
    ).pipe(tap((user) => store.updateState({ user })));
  });
}

export function fetchUserPermissions(
  isCurrentUser: boolean,
  userId: string,
  injector: Injector,
): Observable<AuthorityManagePermissionDTO> {
  return runInInjectionContext(injector, () => {
    const regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
    const store = inject(DetailsStore);

    return (
      isCurrentUser
        ? regulatorAuthoritiesService.getCurrentRegulatorUserPermissionsByCa()
        : regulatorAuthoritiesService.getRegulatorUserPermissionsByCaAndId(userId)
    ).pipe(tap((r: AuthorityManagePermissionDTO) => store.updateState({ userPermissions: r.permissions })));
  });
}

export function fetchRegulatorRolesAndUpdateStore(injector: Injector) {
  return runInInjectionContext(injector, () => {
    const store = inject(DetailsStore);
    const authoritiesService = inject(AuthoritiesService);

    return authoritiesService.getRegulatorRoles().pipe(tap((regulatorRoles) => store.updateState({ regulatorRoles })));
  });
}

export function fetchPermissionGroupLevelsAndUpdateStore(injector: Injector) {
  return runInInjectionContext(injector, () => {
    const regulatorAuthoritiesService = inject(RegulatorAuthoritiesService);
    const store = inject(DetailsStore);

    return regulatorAuthoritiesService
      .getRegulatorPermissionGroupLevels()
      .pipe(tap((groupLevels: PermissionGroupLevels) => store.updateState({ permissionGroupLevels: groupLevels })));
  });
}
