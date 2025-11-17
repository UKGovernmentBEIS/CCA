import { Routes } from '@angular/router';

import { preAuditReviewDeterminationRedirectGuard } from './pre-audit-review-determination.guard';

export const PRE_AUDIT_REVIEW_DETERMINATION_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [preAuditReviewDeterminationRedirectGuard],
        children: [],
      },
      {
        path: 'review-determination',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./review-determination/pre-audit-review-determination.component').then(
            (c) => c.PreAuditReviewDeterminationComponent,
          ),
      },
      {
        path: 'check-your-answers',
        data: { breadcrumb: false, backlink: '../review-determination' },
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-determination-check-your-answers.component').then(
            (c) => c.PreAuditReviewDeterminationCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/pre-audit-review-determination-summary.component').then(
            (c) => c.PreAuditReviewDeterminationSummaryComponent,
          ),
      },
    ],
  },
];
