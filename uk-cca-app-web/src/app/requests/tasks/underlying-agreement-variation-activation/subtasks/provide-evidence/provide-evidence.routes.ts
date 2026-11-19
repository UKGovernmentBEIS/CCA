import { Routes } from '@angular/router';

import { unaVariationActivationRedirectGuard } from './provide-evidence.guard';

export const PROVIDE_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [unaVariationActivationRedirectGuard],
        children: [],
      },
      {
        title: 'Provide evidence',
        path: 'details',
        loadComponent: () => import('./details/provide-evidence-details.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Check your answers',
        path: 'check-your-answers',
        loadComponent: () => import('./check-answers/provide-evidence-check-answers.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
      {
        title: 'Summary details',
        path: 'summary',
        loadComponent: () => import('./summary/provide-evidence-summary.component'),
        data: { backlink: '../../../', breadcrumb: false },
      },
    ],
  },
];
