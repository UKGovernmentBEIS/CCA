export const PAT_UPLOAD_ROUTES = [
  {
    title: 'Performance account template (PAT) upload',
    path: '',
    children: [
      {
        title: 'Confirmation',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/pat-confirmation.component').then((c) => c.PatConfirmationComponent),
      },
    ],
  },
];
