import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { RegulatorUsersRegistrationService } from 'cca-api';

export let invitedUser: { email?: string } | null = null;

export function RegulatorInvitationGuard(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
  const router = inject(Router);
  const regulatorUsersRegistrationService = inject(RegulatorUsersRegistrationService);
  const token = route.queryParamMap.get('token');

  return token
    ? regulatorUsersRegistrationService.acceptRegulatorInvitation({ token }).pipe(
        tap((user) => {
          invitedUser = user;
        }),
        map(() => true),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            router.navigate(['invitation/regulator/invalid-link'], {
              queryParams: { code: res.error.code },
            });

            return of(false);
          } else {
            return throwError(() => res);
          }
        }),
      )
    : of(router.parseUrl('landing'));
}
