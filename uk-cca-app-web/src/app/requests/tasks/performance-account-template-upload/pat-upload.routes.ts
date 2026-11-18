export const PAT_UPLOAD_ROUTES = [
  {
    path: '',
    children: [
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/pat-confirmation.component').then((c) => c.PatConfirmationComponent),
      },
    ],
  },
];
