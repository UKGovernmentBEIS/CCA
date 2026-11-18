import { Routes } from '@angular/router';

import { calculatedResultsResolver } from './calculated-results.resolver';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    resolve: { calculatedResults: calculatedResultsResolver },
    children: [
      {
        path: '',
        title: 'Confirm results and submit',
        data: { backlink: '../../', breadcrumb: false },
        loadComponent: () =>
          import('./action/tpr-form-submit-action.component').then((c) => c.TprFormSubmitActionComponent),
      },
      {
        path: 'confirmation',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () =>
          import('./confirmation/tpr-form-submit-confirmation.component').then(
            (c) => c.TprFormSubmitConfirmationComponent,
          ),
      },
      {
        path: 'expired',
        data: { backlink: false, breadcrumb: false },
        loadComponent: () =>
          import('./expired/tpr-form-submit-expired.component').then((c) => c.TprFormSubmitExpiredComponent),
      },
      {
        path: 'products',
        title: 'View Products',
        data: { breadcrumb: false, backlink: '../' },
        loadComponent: () => import('@requests/common').then((c) => c.TprProductsComponent),
      },
    ],
  },
];
