import { Routes } from '@angular/router';

import { unaActivationRedirectGuard } from './provide-evidence.guard';

export const PROVIDE_EVIDENCE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {
        path: '',
        pathMatch: 'full',
        canActivate: [unaActivationRedirectGuard],
        children: [],
      },
      {
        path: 'details',
        title: 'Provide evidence',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./details/provide-evidence-details.component'),
      },
      {
        path: 'check-your-answers',
        title: 'Check your answers',
        data: { backlink: '../../../', breadcrumb: false },
        loadComponent: () => import('./check-answers/provide-evidence-check-answers.component'),
      },
      {
        path: 'summary',
        data: { backlink: '../../../', breadcrumb: false },
        title: 'Summary details',
        loadComponent: () => import('./summary/provide-evidence-summary.component'),
      },
    ],
  },
];
