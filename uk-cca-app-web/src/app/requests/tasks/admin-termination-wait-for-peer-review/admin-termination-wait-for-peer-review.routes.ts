import { Routes } from '@angular/router';

export const ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Admin termination sent for peer review',
    path: '',
    children: [
      {
        title: 'Reason for admin termination',
        path: 'reason-for-admin-termination',
        loadComponent: () => import('@requests/common').then((c) => c.ReasonForAdminTerminationComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
