import { Routes } from '@angular/router';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Submit to regulator',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./action/action.component').then((c) => c.UnderlyingAgreementSubmitActionComponent),
      },
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.UnderlyingAgreementSubmitConfirmationComponent),
      },
    ],
  },
];
