import { Routes } from '@angular/router';

import { userIsAssigneeGuard } from '@shared/guards';

export const TARGET_PERIOD_REPORTING_CSV_UPLOAD_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'results',
        loadComponent: () => import('./results/submission-results.component').then((c) => c.SubmissionResultsComponent),
      },
      {
        path: 'confirmation',
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./confirmation/tpr-csv-upload-confirmation.component').then(
            (c) => c.TprCsvUploadConfirmationComponent,
          ),
      },
      {
        path: 'close-task',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: '../..' },
        loadComponent: () => import('./close-task/close-task.component').then((c) => c.CloseTaskComponent),
      },
      {
        path: 'close-confirmation',
        canActivate: [userIsAssigneeGuard],
        data: { breadcrumb: false, backlink: false },
        loadComponent: () =>
          import('./close-task/confirmation.component').then((c) => c.CloseTaskConfirmationComponent),
      },
    ],
  },
];
