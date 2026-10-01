import { Routes } from '@angular/router';

export const PERFORMANCE_DATA_ROUTES: Routes = [
  {
    path: '',
    title: 'TP reporting (TP6) - Download spreadsheets',
    children: [
      {
        path: 'confirmation',
        title: 'Confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-download-confirmation.component').then(
            (r) => r.PerformanceDataDownloadConfirmationComponent,
          ),
      },
    ],
  },
];
