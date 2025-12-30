import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const ADMIN_TERMINATION_FINAL_DECISION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'final-decision-reason',
        title: 'Admin termination final decision',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/final-decision-reason/final-decision-reason.routes').then(
            (r) => r.FINAL_DECISION_REASON_ROUTES,
          ),
      },
      {
        path: 'final-decision-notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/final-decision-notify-operator.routes').then(
            (r) => r.FINAL_DECISION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
