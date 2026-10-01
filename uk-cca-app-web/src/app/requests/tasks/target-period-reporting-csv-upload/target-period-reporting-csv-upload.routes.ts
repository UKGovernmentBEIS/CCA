import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const TARGET_PERIOD_REPORTING_CSV_UPLOAD_ROUTES: Routes = [
  {
    path: '',
    title: 'TP reporting (TP7, TP8, TP9) - Upload CSV file',
    children: [
      {
        path: 'results',
        title: 'Submission results',
        loadComponent: () => import('./results/submission-results.component').then((c) => c.SubmissionResultsComponent),
      },
      {
        path: 'confirmation',
        title: 'Target period report submitted',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./confirmation/tpr-csv-upload-confirmation.component').then(
            (c) => c.TprCsvUploadConfirmationComponent,
          ),
      },
      {
        path: 'close-task',
        title: 'Are you sure you want to close this task?',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: '../..' },
        loadComponent: () => import('./close-task/close-task.component').then((c) => c.CloseTaskComponent),
      },
      {
        path: 'close-confirmation',
        title: 'Target period report closed',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./close-task/confirmation.component').then((c) => c.CloseTaskConfirmationComponent),
      },
    ],
  },
];
