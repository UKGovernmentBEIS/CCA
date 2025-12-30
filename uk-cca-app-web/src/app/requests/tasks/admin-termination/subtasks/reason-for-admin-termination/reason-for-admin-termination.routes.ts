import { Routes } from '@angular/router';

import { reasonForAdminTerminationRedirectGuard } from './reason-for-admin-termination.guard';

export const REASON_FOR_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [reasonForAdminTerminationRedirectGuard],
        children: [],
      },
      {
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/reason-for-admin-termination-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/reason-for-admin-termination-check-your-answers.component'),
      },
      {
        path: 'reason-details',
        title: 'Admin termination reason details',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./reason-details/reason-for-admin-termination.component'),
      },
    ],
  },
];
