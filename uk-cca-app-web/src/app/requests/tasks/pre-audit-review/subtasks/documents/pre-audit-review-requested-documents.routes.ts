import { Routes } from '@angular/router';

import { preAuditReviewRequestedDocumentsRedirectGuard } from './pre-audit-review-requested-documents.guard';

export const PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [preAuditReviewRequestedDocumentsRedirectGuard],
        children: [],
      },
      {
        path: 'upload-documents',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./upload-documents/pre-audit-review-requested-documents-upload.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsUploadComponent,
          ),
      },
      {
        path: 'check-your-answers',
        data: { breadcrumb: false, backlink: '../upload-documents' },
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-requested-documents-check-your-answers.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsCheckYourAnswersComponent,
          ),
      },
      {
        path: 'summary',
        data: { breadcrumb: false, backlink: '../../../' },
        loadComponent: () =>
          import('./summary/pre-audit-review-requested-documents-summary.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsSummaryComponent,
          ),
      },
    ],
  },
];
