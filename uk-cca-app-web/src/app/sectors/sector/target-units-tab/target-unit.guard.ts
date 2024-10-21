import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, tap } from 'rxjs';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { ActiveTargetUnitStore } from './active-target-unit.store';

export const TargetUnitGuard: CanActivateFn = (route) => {
  const targetUnitId = route.paramMap.get('targetUnitId');
  const targetUnitsView = inject(TargetUnitAccountInfoViewService);
  const store = inject(ActiveTargetUnitStore);

  return targetUnitsView.getTargetUnitAccountDetailsById(+targetUnitId).pipe(
    tap((tu) => store.setState(tu)),
    map(() => true),
  );
};
