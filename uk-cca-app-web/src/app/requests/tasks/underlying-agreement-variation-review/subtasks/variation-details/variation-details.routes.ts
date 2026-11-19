import { Routes } from '@angular/router';

import { variationDetailsRedirectGuard } from './variation-details-redirect.guard';

export const VARIATION_DETAILS_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        canActivate: [variationDetailsRedirectGuard],
        children: [],
      },
      {
        title: 'Variation details decision',
        path: 'decision',
        loadComponent: () =>
          import('./decision/variation-details-decision.component').then((c) => c.VariationDetailsDecisionComponent),
        data: { backlink: '../../../', breadcrumb: false },
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
        title: 'Variation details',
        path: 'variation-details',
        loadComponent: () => import('./details/variation-details.component').then((c) => c.VariationDetailsComponent),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
