import { Routes } from '@angular/router';

export const PERFORMANCE_DATA_ROUTES: Routes = [
  {
    title: 'TP reporting (TP6) - Download spreadsheets',
    path: '',
    children: [
      {
        title: 'Confirmation',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-download-confirmation.component').then(
            (c) => c.PerformanceDataDownloadConfirmationComponent,
          ),
      },
    ],
  },
];
