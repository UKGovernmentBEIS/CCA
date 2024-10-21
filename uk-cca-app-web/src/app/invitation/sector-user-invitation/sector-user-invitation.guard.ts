import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { SectorUsersRegistrationService } from 'cca-api';

import { SectorUserInvitationStore } from './sector-user-invitation.store';

export const SectorUserInvitationGuard: CanActivateFn = (route): Observable<boolean | UrlTree> => {
  const router = inject(Router);
  const store = inject(SectorUserInvitationStore);
  const sectorUsersRegistrationService = inject(SectorUsersRegistrationService);
  const token = route.queryParamMap.get('token') ?? store.state.emailToken;

  if (!token) return of(router.parseUrl('landing'));

  return sectorUsersRegistrationService.acceptSectorUserInvitation({ token }).pipe(
    tap((invitedUser) => {
      store.updateState({ ...invitedUser, emailToken: token });
    }),
    map((invitedUser) => {
      if (invitedUser.invitationStatus === 'ALREADY_REGISTERED' || invitedUser.invitationStatus === 'ACCEPTED') {
        router.navigate(['invitation/sector-user/confirmed-existing'], { replaceUrl: true });
        return;
      }

      if (
        ['PENDING_TO_REGISTERED_SET_PASSWORD_ONLY', 'ALREADY_REGISTERED_SET_PASSWORD_ONLY'].includes(
          invitedUser.invitationStatus,
        )
      ) {
        router.navigate(['invitation/sector-user/set-password-only'], { replaceUrl: true });
        return;
      }

      return invitedUser.invitationStatus === 'PENDING_TO_REGISTERED_SET_REGISTER_FORM';
    }),
    catchError((res: unknown) => {
      if (isBadRequest(res)) {
        router.navigate(['invitation/sector-user/invalid-link'], {
          queryParams: { code: res.error.code },
          replaceUrl: true,
        });

        return of(false);
      } else {
        return throwError(() => res);
      }
    }),
  );
};

export const SectorUserNoTokenGuard: CanActivateFn = (): Observable<boolean | UrlTree> => {
  const router = inject(Router);
  const storeUser = inject(SectorUserInvitationStore).state;

  if (!storeUser.emailToken) {
    return of(router.parseUrl('landing'));
  } else {
    return of(true);
  }
};

export const resetSectorInvitationStore: CanDeactivateFn<boolean> = () => {
  inject(SectorUserInvitationStore).reset();
  return true;
};
