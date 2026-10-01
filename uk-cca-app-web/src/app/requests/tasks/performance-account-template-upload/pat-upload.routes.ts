export const PAT_UPLOAD_ROUTES = [
  {
    path: '',
    title: 'Performance account template (PAT) upload',
    children: [
      {
        path: 'confirmation',
        title: 'Confirmation',
        loadComponent: () =>
          import('./confirmation/pat-confirmation.component').then((c) => c.PatConfirmationComponent),
      },
    ],
  },
];
