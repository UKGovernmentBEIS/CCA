import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  provideAdminTerminationPayloadMutators,
  provideAdminTerminationSideEffects,
  provideAdminTerminationStepFlowManagers,
  provideAdminTerminationTaskServices,
} from './admin-termination.providers';

export const ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideAdminTerminationTaskServices(),
      provideAdminTerminationPayloadMutators(),
      provideAdminTerminationStepFlowManagers(),
      provideAdminTerminationSideEffects(),
    ],
    children: [
      {
        path: 'reason-for-admin-termination',
        title: 'Reason for admin termination',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/reason-for-admin-termination/reason-for-admin-termination.routes').then(
            (r) => r.REASON_FOR_ADMIN_TERMINATION_ROUTES,
          ),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/admin-termination-notify-operator.routes').then(
            (r) => r.ADMIN_TERMINATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
