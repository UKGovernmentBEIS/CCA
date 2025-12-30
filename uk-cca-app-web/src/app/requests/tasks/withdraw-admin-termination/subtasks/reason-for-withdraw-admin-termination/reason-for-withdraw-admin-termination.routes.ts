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
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/reason-for-withdraw-admin-termination-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./check-your-answers/reason-for-withdraw-admin-termination-check-your-answers.component'),
      },
      {
        path: 'reason-details',
        title: 'Withdrawing admin termination reason details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./reason-details/reason-for-withdraw-admin-termination.component'),
      },
    ],
  },
];
