import { Routes } from '@angular/router';

export const ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'reason-for-admin-termination',
        title: 'Reason for admin termination',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () => import('@requests/common').then((c) => c.ReasonForAdminTerminationComponent),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
