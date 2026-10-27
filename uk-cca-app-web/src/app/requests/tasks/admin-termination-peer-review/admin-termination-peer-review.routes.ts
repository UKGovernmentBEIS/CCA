import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const ADMIN_TERMINATION_PEER_REVIEW_ROUTES: Routes = [
  {
    title: 'Peer review admin termination request',
    path: '',
    children: [
      {
        title: 'Reason for admin termination',
        path: 'reason-for-admin-termination',
        loadComponent: () => import('@requests/common').then((c) => c.ReasonForAdminTerminationComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((r) => r.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
