import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { ACTIVE_TARGET_UNIT_ROUTES } from '../sectors/sector/target-units-tab/target-unit.routes';

export const TARGET_UNIT_ACCOUNT_ROUTES: Routes = [
  {
    title: 'Search for target unit accounts',
    path: '',
    loadComponent: () =>
      import('./target-unit-account-search/target-unit-account-search.component').then(
        (c) => c.TargetUnitAccountSearchComponent,
      ),
    canDeactivate: [PendingRequestGuard],
  },
  ...ACTIVE_TARGET_UNIT_ROUTES,
];
