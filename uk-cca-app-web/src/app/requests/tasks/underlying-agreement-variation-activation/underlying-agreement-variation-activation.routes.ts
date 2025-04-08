import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  provideUnderlyingAgreementVariationActivationPayloadMutators,
  provideUnderlyingAgreementVariationActivationSideEffects,
  provideUnderlyingAgreementVariationActivationStepFlowManagers,
  provideUnderlyingAgreementVariationActivationTaskServices,
} from './underlying-agreement-variation-activation.providers';

export const UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideUnderlyingAgreementVariationActivationTaskServices(),
      provideUnderlyingAgreementVariationActivationPayloadMutators(),
      provideUnderlyingAgreementVariationActivationSideEffects(),
      provideUnderlyingAgreementVariationActivationStepFlowManagers(),
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
          import('./notify-operator/underlying-agreement-variation-activation-notify-operator.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
