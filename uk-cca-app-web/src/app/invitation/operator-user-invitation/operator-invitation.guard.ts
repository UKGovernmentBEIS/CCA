import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, Router, UrlTree } from '@angular/router';

import { catchError, map, Observable, of, tap, throwError } from 'rxjs';

import { isBadRequest } from '@error/business-errors';

import { OperatorUsersRegistrationService } from 'cca-api';

import { OperatorUserInvitationStore } from './store';

export const OperatorUserInvitationGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const store = inject(OperatorUserInvitationStore);
  const operatorUsersRegistrationService = inject(OperatorUsersRegistrationService);
  const token = route.queryParamMap.get('token') ?? store.state.emailToken;

  return operatorUsersRegistrationService.acceptOperatorInvitation({ token }).pipe(
    tap((invitedUser) => {
      store.updateState({ ...invitedUser, emailToken: token });
    }),
    map((invitedUser) => {
      if (invitedUser.invitationStatus === 'ALREADY_REGISTERED' || invitedUser.invitationStatus === 'ACCEPTED') {
        router.navigate(['registration/invitation/confirmed-existing'], { replaceUrl: true });
      }

      if (
        ['PENDING_TO_REGISTERED_SET_PASSWORD_ONLY', 'ALREADY_REGISTERED_SET_PASSWORD_ONLY'].includes(
          invitedUser.invitationStatus,
        )
      ) {
        router.navigate(['registration/invitation/set-password-only'], { replaceUrl: true });
        return;
      }

      return invitedUser.invitationStatus === 'PENDING_TO_REGISTERED_SET_REGISTER_FORM';
    }),
    catchError((res: unknown) => {
      if (isBadRequest(res)) {
        router.navigate(['registration/invitation/invalid-link'], {
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
export const OperatorUserNoTokenGuard: CanActivateFn = (): Observable<boolean | UrlTree> => {
  const router = inject(Router);
  const storeUser = inject(OperatorUserInvitationStore).state;

  if (!storeUser.emailToken) {
    return of(router.parseUrl('landing'));
  } else {
    return of(true);
  }
};

export const resetOperatorInvitationStore: CanDeactivateFn<boolean> = () => {
  inject(OperatorUserInvitationStore).reset();
  return true;
};
