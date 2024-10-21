import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { RegulatorUsersRegistrationService } from 'cca-api';

import { InvitedRegulatorUserStore } from './invited-regulator-user.store';

export function RegulatorInvitationGuard(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
  const router = inject(Router);
  const regulatorUsersRegistrationService = inject(RegulatorUsersRegistrationService);
  const token = route.queryParamMap.get('token');
  const store = inject(InvitedRegulatorUserStore);

  return token
    ? regulatorUsersRegistrationService.acceptRegulatorInvitation({ token }).pipe(
        tap((invitedUser) => {
          store.setState(invitedUser);
        }),
        map((invitedUser) => {
          if (invitedUser.invitationStatus === 'ALREADY_REGISTERED') {
            router.navigate(['invitation/regulator/confirmed'], { replaceUrl: true });
            return;
          }

          return ['PENDING_TO_REGISTERED_SET_PASSWORD_ONLY', 'ALREADY_REGISTERED_SET_PASSWORD_ONLY'].includes(
            invitedUser.invitationStatus,
          );
        }),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            router.navigate(['invitation/regulator/invalid-link'], {
              queryParams: { code: res.error.code },
              replaceUrl: true,
            });

            return of(false);
          } else {
            return throwError(() => res);
          }
        }),
      )
    : of(router.parseUrl('landing'));
}
