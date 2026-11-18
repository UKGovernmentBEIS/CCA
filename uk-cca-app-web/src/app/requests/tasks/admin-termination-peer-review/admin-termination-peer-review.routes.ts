import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from 'src/app/shared/guards/user-is-assignee.guard';

export const ADMIN_TERMINATION_PEER_REVIEW_ROUTES: Routes = [
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
        path: 'peer-review-decision',
        canActivate: [userIsAssigneeGuard],
        loadChildren: () =>
          import('./peer-review-decision/peer-review-decision.routes').then((m) => m.PEER_REVIEW_DECISION_ROUTES),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
