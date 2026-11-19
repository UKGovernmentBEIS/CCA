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
        title: 'Why should this facility be audited?',
        path: 'audit-reason',
        loadComponent: () =>
          import('./audit-reason/pre-audit-review-audit-reason.component').then(
            (c) => c.PreAuditReviewAuditReasonComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-audit-reason-check-your-answers.component').then(
            (c) => c.PreAuditReviewAuditReasonCheckYourAnswersComponent,
          ),
        data: { breadcrumb: false, backlink: '../audit-reason' },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/pre-audit-review-audit-reason-summary.component').then(
            (c) => c.PreAuditReviewAuditReasonSummaryComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];
