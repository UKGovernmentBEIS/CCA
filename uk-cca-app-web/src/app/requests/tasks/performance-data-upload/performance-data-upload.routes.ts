export const PERFORMANCE_DATA_UPLOAD_ROUTES = [
  {
    path: '',
    title: 'TP reporting (TP6) - Upload spreadsheets',
    children: [
      {
        path: 'confirmation',
        title: 'Confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-upload-confirmation.component').then(
            (r) => r.PerformanceDataUploadConfirmationComponent,
          ),
      },
    ],
  },
];
