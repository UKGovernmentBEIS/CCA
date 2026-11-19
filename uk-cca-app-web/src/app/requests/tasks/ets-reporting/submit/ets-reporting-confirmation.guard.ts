import { Location } from '@angular/common';
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const etsReportingConfirmationGuard: CanActivateFn = () => {
  const router = inject(Router);
  const state = router.currentNavigation()?.extras.state ?? (inject(Location).getState() as Record<string, unknown>);
  return state?.['etsReportSubmitted'] === true || router.createUrlTree(['/dashboard']);
};
