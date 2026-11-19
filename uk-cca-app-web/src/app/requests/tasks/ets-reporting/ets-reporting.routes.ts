import { Routes } from '@angular/router';

import { isEditableGuard } from '@requests/common';

import { etsReportingCanSubmitGuard, etsReportingSummaryRedirectGuard } from './ets-reporting-can-submit.guard';
import { etsReportingConfirmationGuard } from './submit/ets-reporting-confirmation.guard';

export const ETS_REPORTING_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Provide UK ETS fuel amount consumed',
        path: 'energy-fuel-amount',
        canActivate: [etsReportingSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/energy-fuel-amount/energy-fuel-amount.routes').then(
            (r) => r.ETS_ENERGY_FUEL_AMOUNT_ROUTES,
          ),
      },
      {
        path: 'submit',
        children: [
          {
            title: 'Submit your UK ETS report',
            path: '',
            pathMatch: 'full',
            canActivate: [isEditableGuard, etsReportingCanSubmitGuard],
            loadComponent: () => import('./submit/ets-reporting-submit.component'),
            data: { backlink: '../../', breadcrumb: false },
          },
          {
            title: 'UK emissions trading scheme (UK ETS) report submitted',
            path: 'confirmation',
            canActivate: [etsReportingConfirmationGuard],
            loadComponent: () => import('./submit/ets-reporting-confirmation.component'),
            data: { backlink: '/dashboard', breadcrumb: false },
          },
        ],
      },
    ],
  },
];
