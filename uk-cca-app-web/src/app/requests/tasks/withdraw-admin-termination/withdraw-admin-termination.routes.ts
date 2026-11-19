import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const WITHDRAW_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    title: 'Withdraw admin termination',
    path: '',
    children: [
      {
        title: 'Reason for withdrawing admin termination',
        path: 'reason-for-withdraw-admin-termination',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/reason-for-withdraw-admin-termination/reason-for-withdraw-admin-termination.routes').then(
            (r) => r.REASON_FOR_WITHDRAW_ADMIN_TERMINATION_ROUTES,
          ),
      },
      {
        title: 'Notify operator of decision',
        path: 'withdraw-notify-operator',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/withdraw-admin-termination-notify-operator.routes').then(
            (r) => r.WITHDRAW_ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
