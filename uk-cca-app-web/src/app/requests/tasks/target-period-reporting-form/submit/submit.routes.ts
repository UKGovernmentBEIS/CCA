import { Routes } from '@angular/router';

import { calculatedResultsResolver } from './calculated-results.resolver';

export const SUBMIT_ROUTES: Routes = [
  {
    path: '',
    resolve: { calculatedResults: calculatedResultsResolver },
    children: [
      {
        title: 'Confirm results and submit',
        path: '',
        loadComponent: () =>
          import('./action/tpr-form-submit-action.component').then((c) => c.TprFormSubmitActionComponent),
        data: { backlink: '../../', breadcrumb: false },
      },
      {
        title: 'Target period report submitted',
        path: 'confirmation',
        loadComponent: () =>
          import('./confirmation/tpr-form-submit-confirmation.component').then(
            (c) => c.TprFormSubmitConfirmationComponent,
          ),
        data: { backlink: false, breadcrumb: false },
      },
      {
        title: 'Target period report expired',
        path: 'expired',
        loadComponent: () =>
          import('./expired/tpr-form-submit-expired.component').then((c) => c.TprFormSubmitExpiredComponent),
        data: { backlink: false, breadcrumb: false },
      },
      {
        title: 'View Products',
        path: 'products',
        loadComponent: () => import('@requests/common').then((c) => c.TprProductsComponent),
        data: { breadcrumb: false, backlink: '../' },
      },
    ],
  },
];
