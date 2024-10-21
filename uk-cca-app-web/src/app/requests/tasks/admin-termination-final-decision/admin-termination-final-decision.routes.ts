import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  provideAdminTerminationFinalDecisionPayloadMutators,
  provideAdminTerminationFinalDecisionSideEffects,
  provideAdminTerminationFinalDecisionStepFlowManagers,
  provideAdminTerminationFinalDecisionTaskServices,
} from './admin-termination-final-decision.providers';

export const ADMIN_TERMINATION_FINAL_DECISION_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideAdminTerminationFinalDecisionTaskServices(),
      provideAdminTerminationFinalDecisionPayloadMutators(),
      provideAdminTerminationFinalDecisionStepFlowManagers(),
      provideAdminTerminationFinalDecisionSideEffects(),
    ],
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
      {
        path: '**',
        redirectTo: '/dashboard',
      },
    ],
  },
];
