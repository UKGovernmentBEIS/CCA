import { Routes } from '@angular/router';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        title: 'Submit to regulator',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () =>
          import('./action/variation-submit-action.component').then((c) => c.VariationSubmitActionComponent),
      },
      {
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/variation-submit-confirmation.component').then(
            (c) => c.VariationSubmitConfirmationComponent,
          ),
      },
    ],
  },
];
