import { Routes } from '@angular/router';

import { isEditableGuard, isEditableSummaryRedirectGuard } from '@requests/common';

export const UNDERLYING_AGREEMENT_APPLICATION_ROUTES: Routes = [
  {
    path: '',
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
        canActivate: [isEditableGuard],
        loadChildren: () => import('./submit/submit.routes').then((r) => r.SUBMIT_ROUTES),
      },
    ],
  },
];
