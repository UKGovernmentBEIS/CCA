import { Routes } from '@angular/router';

export const FACILITY_AUDIT_PRE_AUDIT_REVIEW_COMPLETED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'reason',
        loadComponent: () =>
          import('./reason/pre-audit-review-completed-reason.component').then(
            (c) => c.PreAuditReviewCompletedReasonComponent,
          ),
        data: { breadcrumb: false, backlink: '../../' },
      },
      {
        path: 'requested-documents',
        loadComponent: () =>
          import('./documents/pre-audit-reason-completed-documents.component').then(
            (c) => c.PreAuditReasonCompletedDocumentsComponent,
          ),
        data: { breadcrumb: false, backlink: '../../' },
      },
      {
        path: 'determination',
        loadComponent: () =>
          import('./determination/pre-audit-reason-completed-determination.component').then(
            (c) => c.PreAuditReasonCompletedDeterminationComponent,
          ),
        data: { breadcrumb: false, backlink: '../../' },
      },
    ],
  },
];
