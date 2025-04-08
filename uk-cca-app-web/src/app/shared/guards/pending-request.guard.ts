import { inject } from '@angular/core';
import { Router } from '@angular/router';

import { combineLatest, first, map, tap } from 'rxjs';

import { PendingRequestService } from '@netz/common/services';

export type PendingRequest = {
  pendingRequest: PendingRequestService;
};

export function PendingRequestGuard(component: PendingRequest | any) {
  const router = inject(Router);
  const pendingRequest = inject(PendingRequestService);
  return (
    router.getCurrentNavigation()?.extras?.state?.forceNavigation ||
    combineLatest([
      pendingRequest.isRequestPending$,
      ...(isPendingRequest(component) ? [component.pendingRequest.isRequestPending$] : []),
    ]).pipe(
      map((pendingRequests) => pendingRequests.some((isRequestPending) => isRequestPending)),
      first(),
      tap((isPending) => {
        if (isPending) {
          alert(
            'A server request is pending. We suggest that you stay on this page in order not to lose your progress.',
          );
        }
      }),
      map((isPending) => !isPending),
    )
  );
}
function isPendingRequest(component: PendingRequest | any): component is PendingRequest {
  return component.pendingRequest;
}
