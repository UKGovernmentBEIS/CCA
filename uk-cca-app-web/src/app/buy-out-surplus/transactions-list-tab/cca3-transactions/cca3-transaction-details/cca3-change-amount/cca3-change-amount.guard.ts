import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot, Router } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

import { Cca3ChangeAmountState } from './cca3-change-amount.state';

export const canActivateCca3ChangeAmount: CanActivateFn = (route) =>
  inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR' ||
  inject(Router).createUrlTree(['/buyout-surplus/cca3-transactions', route.paramMap.get('transactionId')]);

export const canActivateCca3ChangeAmountReview: CanActivateFn = (route) => {
  const state = inject(Cca3ChangeAmountState);

  return (state.form.valid && !state.terminated()) || createUrlTreeFromSnapshot(route, ['../']);
};
