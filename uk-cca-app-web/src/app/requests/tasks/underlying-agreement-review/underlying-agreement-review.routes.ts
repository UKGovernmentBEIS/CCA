import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const UNDERLYING_AGREEMENT_REVIEW_ROUTES: Routes = [
  {
    path: '',
    providers: [],
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
        path: 'manage-facilities',
        loadChildren: () =>
          import('./subtasks/manage-facilities/manage-facilities.routes').then((r) => r.MANAGE_FACILITIES_ROUTES),
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
      {
        path: 'send-for-peer-review',
        canActivate: [isEditableGuard],
        loadChildren: () =>
          import('./send-for-peer-review/underlying-agreement-review-send-for-peer-review.routes').then(
            (r) => r.UNDERLYING_AGREEMENT_REVIEW_SEND_FOR_PEER_REVIEW_ROUTES,
          ),
      },
    ],
  },
];
