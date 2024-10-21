import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  provideWithdrawAdminTerminationPayloadMutators,
  provideWithdrawAdminTerminationSideEffects,
  provideWithdrawAdminTerminationStepFlowManagers,
  provideWithdrawAdminTerminationTaskServices,
} from './withdraw-admin-termination.providers';

export const WITHDRAW_ADMIN_TERMINATION_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideWithdrawAdminTerminationTaskServices(),
      provideWithdrawAdminTerminationPayloadMutators(),
      provideWithdrawAdminTerminationStepFlowManagers(),
      provideWithdrawAdminTerminationSideEffects(),
    ],
    children: [
      {
        path: 'reason-for-withdraw-admin-termination',
        title: 'Reason for withdrawing admin termination',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/reason-for-withdraw-admin-termination/reason-for-withdraw-admin-termination.routes').then(
            (r) => r.REASON_FOR_WITHDRAW_ADMIN_TERMINATION_ROUTES,
          ),
      },
      {
        path: 'withdraw-notify-operator',
        title: 'Notify operator of decision',
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
