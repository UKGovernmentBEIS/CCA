import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const PERFORMANCE_ACCOUNT_TEMPLATE_CSV_UPLOAD_ROUTES: Routes = [
  {
    title: 'PAT reporting - Upload CSV file',
    path: '',
    children: [
      {
        title: 'Submission results',
        path: 'results',
        loadComponent: () =>
          import('./results/pat-csv-submission-results.component').then((c) => c.PatCsvSubmissionResultsComponent),
      },
      {
        title: 'PAT report submitted',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/pat-csv-upload-confirmation.component').then(
            (c) => c.PatCsvUploadConfirmationComponent,
          ),
        data: { breadcrumb: false, backlink: false },
      },
      {
        title: 'Are you sure you want to close this task?',
        path: 'close-task',
        loadComponent: () => import('./close-task/close-task.component').then((c) => c.PatCsvCloseTaskComponent),
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: '../..' },
      },
      {
        title: 'PAT report closed',
        path: 'close-confirmation',
        loadComponent: () =>
          import('./close-task/confirmation.component').then((c) => c.PatCsvCloseTaskConfirmationComponent),
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: false },
      },
    ],
  },
];
