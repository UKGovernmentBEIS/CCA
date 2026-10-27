import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const ADMIN_TERMINATION_ROUTES: Routes = [
  {
    title: 'Admin termination',
    path: '',
    children: [
      {
        title: 'Reason for admin termination',
        path: 'reason-for-admin-termination',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/reason-for-admin-termination/reason-for-admin-termination.routes').then(
            (r) => r.REASON_FOR_ADMIN_TERMINATION_ROUTES,
          ),
      },
      {
        title: 'Notify operator of decision',
        path: 'notify-operator',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/admin-termination-notify-operator.routes').then(
            (r) => r.ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        title: 'Send for peer review',
        path: 'send-for-peer-review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/admin-termination-send-for-peer-review.routes').then(
            (r) => r.ADMIN_TERMINATION_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
    ],
  },
];
