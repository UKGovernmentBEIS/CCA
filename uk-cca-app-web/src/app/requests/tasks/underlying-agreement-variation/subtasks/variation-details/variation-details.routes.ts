import { Routes } from '@angular/router';

import { variationDetailsRedirectGuard } from './variation-details-redirect.guard';

export const VARIATION_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [variationDetailsRedirectGuard],
        children: [],
      },
      {
        title: 'Summary',
        path: 'summary',
        loadComponent: () => import('./summary/variation-details-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-your-answers/variation-details-check-your-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Describe the changes',
        path: 'variation-details',
        loadComponent: () => import('./details/variation-details.component').then((c) => c.VariationDetailsComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
