export const PERFORMANCE_DATA_UPLOAD_ROUTES = [
  {
    path: '',
    children: [
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/performance-data-upload-confirmation.component').then(
            (r) => r.PerformanceDataUploadConfirmationComponent,
          ),
      },
    ],
  },
];
