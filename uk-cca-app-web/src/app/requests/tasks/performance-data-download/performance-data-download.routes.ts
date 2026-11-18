import { Routes } from '@angular/router';

export const PERFORMANCE_DATA_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-download-confirmation.component').then(
            (r) => r.PerformanceDataDownloadConfirmationComponent,
          ),
      },
    ],
  },
];
