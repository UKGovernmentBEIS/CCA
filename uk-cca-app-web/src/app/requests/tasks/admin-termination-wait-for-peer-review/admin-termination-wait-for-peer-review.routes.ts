import { Routes } from '@angular/router';

export const ADMIN_TERMINATION_WAIT_FOR_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Admin termination sent for peer review',
    path: '',
    children: [
      {
        title: 'Reason for admin termination',
        path: 'reason-for-admin-termination',
        loadComponent: () =>
          import('@requests/common/admin-termination-peer-review/reason-for-admin-termination/reason-for-admin-termination.component').then(
            (c) => c.ReasonForAdminTerminationComponent,
          ),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
