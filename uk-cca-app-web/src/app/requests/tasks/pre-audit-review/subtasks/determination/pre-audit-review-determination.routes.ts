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
        title: 'Pre-audit review determination',
        path: 'review-determination',
        loadComponent: () =>
          import('./review-determination/pre-audit-review-determination.component').then(
            (c) => c.PreAuditReviewDeterminationComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-determination-check-your-answers.component').then(
            (c) => c.PreAuditReviewDeterminationCheckYourAnswersComponent,
          ),
        data: { breadcrumb: false, backlink: '../review-determination' },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/pre-audit-review-determination-summary.component').then(
            (c) => c.PreAuditReviewDeterminationSummaryComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];
