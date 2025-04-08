import { Routes } from '@angular/router';

import { PendingRequestGuard } from '@shared/guards';

import { ACTIVE_TARGET_UNIT_ROUTES } from '../sectors/sector/target-units-tab/target-unit.routes';

export const ACCOUNT_ROUTES: Routes = [
  {
    path: '',
    data: { pageTitle: 'Search for target unit accounts' },
    loadComponent: () => import('./account-search/account-search.component').then((c) => c.AccountSearchComponent),
    canDeactivate: [PendingRequestGuard],
  },
  ...ACTIVE_TARGET_UNIT_ROUTES,
];
