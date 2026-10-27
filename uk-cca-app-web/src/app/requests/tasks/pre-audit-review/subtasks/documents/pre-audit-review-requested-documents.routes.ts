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
        title: 'Upload documents',
        path: 'upload-documents',
        loadComponent: () =>
          import('./upload-documents/pre-audit-review-requested-documents-upload.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsUploadComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () =>
          import('./check-your-answers/pre-audit-review-requested-documents-check-your-answers.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsCheckYourAnswersComponent,
          ),
        data: { breadcrumb: false, backlink: '../upload-documents' },
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () =>
          import('./summary/pre-audit-review-requested-documents-summary.component').then(
            (c) => c.PreAuditReviewRequestedDocumentsSummaryComponent,
          ),
        data: { breadcrumb: false, backlink: '../../../' },
      },
    ],
  },
];
