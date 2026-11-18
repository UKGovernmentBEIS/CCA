import { Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/components';

export const PRE_AUDIT_REVIEW_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'reason',
        loadChildren: () =>
          import('./subtasks/reason/pre-audit-review-audit-reason.routes').then(
            (r) => r.PRE_AUDIT_REVIEW_AUDIT_REASON_ROUTES,
          ),
      },
      {
        path: 'requested-documents',
        loadChildren: () =>
          import('./subtasks/documents/pre-audit-review-requested-documents.routes').then(
            (r) => r.PRE_AUDIT_REVIEW_REQUESTED_DOCUMENTS_ROUTES,
          ),
      },
      {
        path: 'determination',
        loadChildren: () =>
          import('./subtasks/determination/pre-audit-review-determination.routes').then(
            (r) => r.PRE_AUDIT_REVIEW_DETERMINATION_ROUTES,
          ),
      },
      {
        path: 'complete-task',
        loadChildren: () =>
          import('./complete-task/complete-task.routes').then((r) => r.PRE_AUDIT_REVIEW_COMPLETE_TASK_ROUTES),
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
];
