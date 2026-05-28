import { Routes } from '@angular/router';

import { isEditableSummaryRedirectGuard } from '@requests/common';

export const TARGET_PERIOD_REPORTING_FORM_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'energy-fuel-amount',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/energy-fuel-amount/energy-fuel-amount.routes').then((r) => r.ENERGY_FUEL_AMOUNT_ROUTES),
      },
      {
        path: 'throughput',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/tpr-throughput/tpr-throughput.routes').then((r) => r.TPR_THROUGHPUT_ROUTES),
      },
    ],
  },
];
