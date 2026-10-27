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
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/reason-for-admin-termination-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/reason-for-admin-termination-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Admin termination reason details',
        path: 'reason-details',
        loadComponent: () => import('./reason-details/reason-for-admin-termination.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
