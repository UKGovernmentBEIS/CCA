import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot, RedirectCommand } from '@angular/router';

import { AuthStore, selectUserRoleType } from '@netz/common/auth';

import { Cca3ReceivedAmountState } from './cca3-received-amount.state';

export const canActivateCca3ReceivedAmount: CanActivateFn = (route) =>
  inject(AuthStore).select(selectUserRoleType)() === 'REGULATOR' || createUrlTreeFromSnapshot(route, ['..']);

export const canActivateCca3ReceivedAmountReview: CanActivateFn = (route) =>
  inject(Cca3ReceivedAmountState).form?.valid ||
  new RedirectCommand(createUrlTreeFromSnapshot(route, ['..']), { replaceUrl: true });
