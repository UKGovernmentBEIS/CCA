import { Routes } from '@angular/router';

import { preAuditReviewAuditReasonRedirectGuard } from './pre-audit-review-audit-reason.guard';

export const PRE_AUDIT_REVIEW_AUDIT_REASON_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [preAuditReviewAuditReasonRedirectGuard],
        children: [],
      },
      {
        path: 'audit-reason',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./audit-reason/pre-audit-review-audit-reason.component').then(
            (c) => c.PreAuditReviewAuditReasonComponent,
          ),
      },
      {
        path: 'check-your-answers',
        data: { breadcrumb: false, backlink: '../audit-reason' },
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-audit-reason-check-your-answers.component').then(
            (c) => c.PreAuditReviewAuditReasonCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/pre-audit-review-audit-reason-summary.component').then(
            (c) => c.PreAuditReviewAuditReasonSummaryComponent,
          ),
      },
    ],
  },
];
