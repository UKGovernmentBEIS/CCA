import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const ADMIN_TERMINATION_FINAL_DECISION_ROUTES: Routes = [
  {
    title: 'Admin termination final decision',
    path: '',
    children: [
      {
        title: 'Admin termination final decision',
        path: 'final-decision-reason',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/final-decision-reason/final-decision-reason.routes').then(
            (r) => r.FINAL_DECISION_REASON_ROUTES,
          ),
      },
      {
        title: 'Notify operator of decision',
        path: 'final-decision-notify-operator',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/final-decision-notify-operator.routes').then(
            (r) => r.FINAL_DECISION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
