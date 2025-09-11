import { inject, InjectionToken, signal, WritableSignal } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn } from '@angular/router';

export const CurrentFacilityId = new InjectionToken<WritableSignal<string>>('CurrentFacilityId', {
  providedIn: 'root',
  factory: () => {
    return signal('');
  },
});

export const setCurrentFacility: CanActivateFn = (route: ActivatedRouteSnapshot): boolean => {
  const currentFacilityId = inject(CurrentFacilityId);
  currentFacilityId.set(route.paramMap.get('facilityId'));
  return true;
};

export const resetCurrentFacility: CanDeactivateFn<unknown> = (): boolean => {
  const currentFacilityId = inject(CurrentFacilityId);
  currentFacilityId.set('');
  return true;
};
