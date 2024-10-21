import { Routes } from '@angular/router';

import { PayloadMutatorsHandler, SideEffectsHandler } from '@netz/common/forms';
import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

import {
  providePayloadMutators,
  provideSideEffects,
  provideStepFlowManagers,
  provideTaskServices,
} from './underlying-agreement-review.providers';

export const UNDERLYING_AGREEMENT_REVIEW_ROUTES: Routes = [
  {
    path: '',
    providers: [
      SideEffectsHandler,
      PayloadMutatorsHandler,
      provideTaskServices(),
      providePayloadMutators(),
      provideSideEffects(),
      provideStepFlowManagers(),
    ],
    children: [
      {
        path: 'review-target-unit-details',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/review-target-unit-details/review-target-unit-details.routes').then(
            (r) => r.REVIEW_TARGET_UNIT_DETAILS_ROUTES,
          ),
      },
      {
        path: 'facility',
        loadChildren: () => import('./subtasks/facility/facility.routes').then((r) => r.FACILITY_ROUTES),
      },
      {
        path: 'authorisation-additional-evidence',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/authorisation-additional-evidence/authorisation-additional-evidence.routes').then(
            (r) => r.AUTHORISATION_ADDITIONAL_EVIDENCE_ROUTES,
          ),
      },
      {
        path: 'target-period-5',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/baseline-and-targets/tp5/tp5.routes').then((r) => r.TARGET_PERIOD_5_ROUTES),
      },
      {
        path: 'target-period-6',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/baseline-and-targets/tp6/tp6.routes').then((r) => r.TARGET_PERIOD_6_ROUTES),
      },
      {
        path: 'send-application',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/overall-decision/overall-decision.routes').then((r) => r.OVERALL_DECISION_ROUTES),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/notify-operator.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_REVIEW_NOTIFY_OPERATOR_ROUTES,
          ),
      },
    ],
  },
];
