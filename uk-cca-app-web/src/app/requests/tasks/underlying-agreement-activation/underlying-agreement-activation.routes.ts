import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  provideUNAActivationPayloadMutators,
  provideUNAActivationSideEffects,
  provideUNAActivationStepFlowManagers,
  provideUNAActivationTaskServices,
} from './underlying-agreement-activation.providers';

export const UNDERLYING_AGREEMENT_ACTIVATION_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideUNAActivationTaskServices(),
      provideUNAActivationPayloadMutators(),
      provideUNAActivationSideEffects(),
      provideUNAActivationStepFlowManagers(),
    ],
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
          import('./notify-operator/underlying-agreement-activation-notify-operator.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_ACTIVATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
