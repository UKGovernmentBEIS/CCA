import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { VerifierUsersRegistrationService } from 'cca-api';

export let invitedUser: { email?: string } | null = null;

export function VerifierInvitationGuard(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
  const router = inject(Router);
  const verifierUsersRegistrationService = inject(VerifierUsersRegistrationService);
  const token = route.queryParamMap.get('token');
  return token
    ? verifierUsersRegistrationService.acceptVerifierInvitation({ token }).pipe(
        tap((user) => {
          invitedUser = user;
        }),
        map(() => true),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            router.navigate(['invitation/verifier/invalid-link'], {
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
