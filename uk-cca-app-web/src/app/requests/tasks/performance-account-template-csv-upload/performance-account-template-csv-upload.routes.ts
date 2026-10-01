import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const PERFORMANCE_ACCOUNT_TEMPLATE_CSV_UPLOAD_ROUTES: Routes = [
  {
    path: '',
    title: 'PAT reporting - Upload CSV file',
    children: [
      {
        path: 'results',
        title: 'Submission results',
        loadComponent: () =>
          import('./results/pat-csv-submission-results.component').then((c) => c.PatCsvSubmissionResultsComponent),
      },
      {
        path: 'confirmation',
        title: 'PAT report submitted',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./confirmation/pat-csv-upload-confirmation.component').then(
            (c) => c.PatCsvUploadConfirmationComponent,
          ),
      },
      {
        path: 'close-task',
        title: 'Are you sure you want to close this task?',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: '../..' },
        loadComponent: () => import('./close-task/close-task.component').then((c) => c.PatCsvCloseTaskComponent),
      },
      {
        path: 'close-confirmation',
        title: 'PAT report closed',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./close-task/confirmation.component').then((c) => c.PatCsvCloseTaskConfirmationComponent),
      },
    ],
  },
];
