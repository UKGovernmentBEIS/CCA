import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const TARGET_PERIOD_REPORTING_CSV_UPLOAD_ROUTES: Routes = [
  {
    title: 'TP reporting (TP7, TP8, TP9) - Upload CSV file',
    path: '',
    children: [
      {
        title: 'Submission results',
        path: 'results',
        loadComponent: () => import('./results/submission-results.component').then((c) => c.SubmissionResultsComponent),
      },
      {
        title: 'Target period report submitted',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/tpr-csv-upload-confirmation.component').then(
            (c) => c.TprCsvUploadConfirmationComponent,
          ),
        data: { breadcrumb: false, backlink: false },
      },
      {
        title: 'Are you sure you want to close this task?',
        path: 'close-task',
        loadComponent: () => import('./close-task/close-task.component').then((c) => c.CloseTaskComponent),
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: '../..' },
      },
      {
        title: 'Target period report closed',
        path: 'close-confirmation',
        loadComponent: () =>
          import('./close-task/confirmation.component').then((c) => c.CloseTaskConfirmationComponent),
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: false },
      },
    ],
  },
];
