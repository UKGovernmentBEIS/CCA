import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { ReceivedAmountStore } from './received-amount.store';

export const ReceivedAmountGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const receivedAmountStore = inject(ReceivedAmountStore);

  if (!receivedAmountStore.state.receivedAmount) {
    const pathString = route.parent.pathFromRoot
      .flatMap((p) => p.url)
      .map((segment) => segment.path)
      .filter(Boolean)
      .join('/');

    router.navigate([pathString], { replaceUrl: true });
    return false;
  }

  return true;
};
