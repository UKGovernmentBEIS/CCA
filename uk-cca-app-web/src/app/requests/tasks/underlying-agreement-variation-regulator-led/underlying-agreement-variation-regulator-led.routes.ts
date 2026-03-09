import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'variation-details',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/variation-details/variation-details.routes').then((r) => r.VARIATION_DETAILS_ROUTES),
      },
      {
        path: 'review-target-unit-details',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/review-target-unit-details/review-target-unit-details.routes').then(
            (r) => r.REVIEW_TARGET_UNIT_DETAILS_ROUTES,
          ),
      },
      {
        path: 'manage-facilities',
        loadChildren: () =>
          import('./subtasks/manage-facilities/manage-facilities.routes').then((r) => r.MANAGE_FACILITIES_ROUTES),
      },
      {
        path: 'target-period-5',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () => import('./subtasks/tp5/tp5.routes').then((r) => r.TARGET_PERIOD_5_ROUTES),
      },
      {
        path: 'target-period-6',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () => import('./subtasks/tp6/tp6.routes').then((r) => r.TARGET_PERIOD_6_ROUTES),
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
        path: 'operator-assent-decision',
        canActivate: [isEditableSummaryRedirectGuard],
        loadChildren: () =>
          import('./subtasks/operator-assent-decision/operator-assent-decision.routes').then(
            (r) => r.OPERATOR_ASSENT_DECISION_ROUTES,
          ),
      },
      {
        path: 'notify-operator',
        title: 'Notify operator of decision',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./notify-operator/notify-operator.routes').then(
            (r) => r.REGULATOR_LED_VARIATION_NOTIFY_OPERATOR_ROUTES,
          ),
      },
      {
        path: 'send-for-peer-review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/underlying-agreement-variation-regulator-led-send-for-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
    ],
  },
];
