import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'provide-evidence',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/provide-evidence/provide-evidence.routes').then((r) => r.PROVIDE_EVIDENCE_ROUTES),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/underlying-agreement-variation-activation-notify-operator.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
