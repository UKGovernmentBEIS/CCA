import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

export const canActivateCca3ChangeHistory: CanActivateFn = (route) =>
  inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR' ||
  inject(Router).createUrlTree(['/buyout-surplus/cca3-transactions', route.paramMap.get('transactionId')]);
