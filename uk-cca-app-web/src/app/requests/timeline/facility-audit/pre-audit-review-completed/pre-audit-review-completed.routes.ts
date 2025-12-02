import { Routes } from '@angular/router';

export const FACILITY_AUDIT_PRE_AUDIT_REVIEW_COMPLETED_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'reason',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./reason/pre-audit-review-completed-reason.component').then(
            (c) => c.PreAuditReviewCompletedReasonComponent,
          ),
      },
      {
        path: 'requested-documents',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./documents/pre-audit-reason-completed-documents.component').then(
            (c) => c.PreAuditReasonCompletedDocumentsComponent,
          ),
      },
      {
        path: 'determination',
        data: { breadcrumb: false, backlink: '../../' },
        loadComponent: () =>
          import('./determination/pre-audit-reason-completed-determination.component').then(
            (c) => c.PreAuditReasonCompletedDeterminationComponent,
          ),
      },
    ],
  },
];
