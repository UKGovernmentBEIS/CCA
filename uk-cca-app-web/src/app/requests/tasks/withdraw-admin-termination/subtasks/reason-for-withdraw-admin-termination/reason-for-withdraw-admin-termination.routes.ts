import { Routes } from '@angular/router';

import { reasonForWithdrawAdminTerminationRedirectGuard } from './reason-for-withdraw-admin-termination.guard';

export const REASON_FOR_WITHDRAW_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [reasonForWithdrawAdminTerminationRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/reason-for-withdraw-admin-termination-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/reason-for-withdraw-admin-termination-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Withdrawing admin termination reason details',
        path: 'reason-details',
        loadComponent: () => import('./reason-details/reason-for-withdraw-admin-termination.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
