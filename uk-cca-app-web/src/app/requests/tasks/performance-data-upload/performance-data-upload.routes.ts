export const PERFORMANCE_DATA_UPLOAD_ROUTES = [
  {
    title: 'TP reporting (TP6) - Upload spreadsheets',
    path: '',
    children: [
      {
        title: 'Confirmation',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-upload-confirmation.component').then(
            (c) => c.PerformanceDataUploadConfirmationComponent,
          ),
      },
    ],
  },
];
