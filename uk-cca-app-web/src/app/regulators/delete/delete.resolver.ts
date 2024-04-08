import { inject } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { RegulatorUsersService, UsersService } from 'cca-api';

import { saveNotFoundRegulatorError } from '../errors/business-error';

export function deleteResolver(route: ActivatedRouteSnapshot) {
  const regulatorUsersService = inject(RegulatorUsersService);
  const usersService = inject(UsersService);
  const authStore = inject(AuthStore);
  const businessErrorService = inject(BusinessErrorService);
  return authStore
    .pipe(
      selectUserState,
      first(),
      switchMap(({ userId }) =>
        userId === route.paramMap.get('userId')
          ? usersService.getCurrentUser()
          : regulatorUsersService.getRegulatorUserByCaAndId(route.paramMap.get('userId')),
      ),
    )
    .pipe(catchBadRequest(ErrorCodes.AUTHORITY1003, () => businessErrorService.showError(saveNotFoundRegulatorError)));
}
