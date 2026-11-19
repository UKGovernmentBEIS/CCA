import { Routes } from '@angular/router';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        title: 'Submit to regulator',
        path: '',
        loadComponent: () =>
          import('./action/variation-submit-action.component').then((c) => c.VariationSubmitActionComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Variation application sent to regulator',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/variation-submit-confirmation.component').then(
            (c) => c.VariationSubmitConfirmationComponent,
          ),
      },
    ],
  },
];
