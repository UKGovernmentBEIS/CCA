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
        path: 'summary',
        title: 'Summary',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./summary/variation-details-summary.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-your-answers/variation-details-check-your-answers.component'),
      },
      {
        path: 'variation-details',
        title: 'Describe the changes',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./details/variation-details.component').then((c) => c.VariationDetailsComponent),
      },
    ],
  },
];
