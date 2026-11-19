import { Routes } from '@angular/router';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Submit to regulator',
        path: '',
        loadComponent: () =>
          import('./action/action.component').then((c) => c.UnderlyingAgreementSubmitActionComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Application sent to regulator',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/confirmation.component').then((c) => c.UnderlyingAgreementSubmitConfirmationComponent),
      },
    ],
  },
];
